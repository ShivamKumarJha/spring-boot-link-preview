package com.shivamkumarjha.linkpreview.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("LINKS")
data class LinkPreview(
    @Id var url: String,
    var description: String? = null,
    var image: String? = null,
    var siteName: String? = null,
    var title: String? = null,
    var type: String? = null
)