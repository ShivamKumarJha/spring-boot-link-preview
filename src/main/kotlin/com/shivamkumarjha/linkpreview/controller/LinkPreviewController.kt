package com.shivamkumarjha.linkpreview.controller

import com.shivamkumarjha.linkpreview.model.LinkPreview
import org.jsoup.Jsoup
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
class LinkPreviewController {

    companion object {
        private const val DEFAULT_URL = "https://github.com/ShivamKumarJha"
        private val AGENTS = mutableListOf(
            "facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)",
            "Mozilla",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36",
            "WhatsApp/2.19.81 A",
            "facebookexternalhit/1.1",
            "facebookcatalog/1.0"
        )

        private const val REFERRER = "http://www.google.com"
        private const val TIMEOUT = 100000
        private const val DOC_SELECT_QUERY = "meta[property^=og:]"
        private const val OPEN_GRAPH_KEY = "content"
        private const val PROPERTY = "property"
        private const val OG_IMAGE = "og:image"
        private const val OG_DESCRIPTION = "og:description"
        private const val OG_TITLE = "og:title"
        private const val OG_SITE_NAME = "og:site_name"
        private const val OG_TYPE = "og:type"
        //private const val OG_URL = "og:url"
    }

    @GetMapping("/api/link_preview")
    fun link(@RequestParam(name = "url", defaultValue = DEFAULT_URL) url: String): LinkPreview {
        val linkPreview = LinkPreview(url)
        try {
            AGENTS.forEach { agent ->
                val response = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent(agent)
                    .referrer(REFERRER)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .execute()

                val doc = response.parse()
                val ogTags = doc.select(DOC_SELECT_QUERY)
                ogTags.forEach { tag ->
                    when (tag.attr(PROPERTY)) {
                        OG_IMAGE -> linkPreview.image = (tag.attr("abs:$OPEN_GRAPH_KEY"))
                        OG_DESCRIPTION -> linkPreview.description = (tag.attr(OPEN_GRAPH_KEY))
                        OG_TITLE -> linkPreview.title = (tag.attr(OPEN_GRAPH_KEY))
                        OG_SITE_NAME -> linkPreview.siteName = (tag.attr(OPEN_GRAPH_KEY))
                        OG_TYPE -> linkPreview.type = (tag.attr(OPEN_GRAPH_KEY))
                    }
                }

                if (linkPreview.title.isNullOrEmpty()) {
                    linkPreview.title = doc.title()
                }

                if (linkPreview.description.isNullOrEmpty()) {
                    linkPreview.description =
                        if (doc.select("meta[name=description]").size != 0) {
                            doc.select("meta[name=description]").first()?.attr(OPEN_GRAPH_KEY)
                        } else null
                }
                if (linkPreview.description == "undefined") {
                    linkPreview.description = null
                }
                if (!linkPreview.title.isNullOrEmpty()) {
                    return@forEach
                }
            }
        } catch (exception: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting preview.", exception)
        }
        return linkPreview
    }
}