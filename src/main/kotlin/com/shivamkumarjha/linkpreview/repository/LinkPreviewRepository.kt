package com.shivamkumarjha.linkpreview.repository

import com.shivamkumarjha.linkpreview.model.LinkPreview
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface LinkPreviewRepository : CrudRepository<LinkPreview, String> {

    @Query("select * from links")
    fun getAllLinkPreviews(): List<LinkPreview>
}