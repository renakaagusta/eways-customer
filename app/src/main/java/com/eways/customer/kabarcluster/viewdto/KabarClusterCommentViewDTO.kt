package com.eways.customer.kabarcluster.viewdto

import com.eways.customer.utils.date.SLDate

data class KabarClusterCommentViewDTO (
    val id: String,
    val creator : String,
    val content : String,
    val createdAt : SLDate
)