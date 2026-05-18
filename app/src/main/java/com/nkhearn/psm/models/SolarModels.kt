package com.nkhearn.psm.models

import com.google.gson.annotations.SerializedName

data class SolarDataResponse(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: Map<String, Any>
)

data class LastDataResponse(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("data") val data: Map<String, Any>
)

data class WebSocketMessage(
    @SerializedName("type") val type: String,
    @SerializedName("payload") val payload: LastDataResponse
)

data class StatsResponse(
    @SerializedName("avg") val avg: Double?,
    @SerializedName("min") val min: Double?,
    @SerializedName("max") val max: Double?,
    @SerializedName("sum") val sum: Double?,
    @SerializedName("count") val count: Int?
)

data class VirtualMetric(
    @SerializedName("name") val name: String,
    @SerializedName("formula") val formula: String
)
