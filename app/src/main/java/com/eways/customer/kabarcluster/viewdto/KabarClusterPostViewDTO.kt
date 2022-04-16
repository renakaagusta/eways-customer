package com.eways.customer.kabarcluster.viewdto

import com.eways.customer.utils.date.SLDate

data class KabarClusterPostViewDTO(
    var id: String,
    var userId: String,
    var pinned: Boolean,
    var creator: String,
    var content: String,
    var imagePath: String,
    var createdAt: SLDate,
    var commentCount: Int
)