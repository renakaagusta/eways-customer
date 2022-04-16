package com.proyek.infrastructures.user.agent.network

import com.google.gson.annotations.SerializedName
import com.proyek.infrastructures.user.agent.entities.Error
import com.proyek.infrastructures.user.agent.entities.ErrorString
import com.proyek.infrastructures.user.agent.entities.UserAgent

data class AgentListResponse(
    val errors: Error,
    @SerializedName("message")
    val message: String,
    val data: List<UserAgent>
)