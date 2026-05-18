package com.nkhearn.psm.api

import com.nkhearn.psm.models.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SolarApiService {
    @GET("/api/last")
    suspend fun getLastData(): LastDataResponse

    @GET("/api/history")
    suspend fun getHistory(
        @Query("limit") limit: Int? = null,
        @Query("start") start: String? = null,
        @Query("end") end: String? = null
    ): List<SolarDataResponse>

    @GET("/api/keys")
    suspend fun getKeys(): List<String>

    @GET("/api/data/{key}/last")
    suspend fun getMetricLast(@Path("key") key: String): Map<String, Any>

    @GET("/api/data/{key}/history")
    suspend fun getMetricHistory(
        @Path("key") key: String,
        @Query("limit") limit: Int? = null,
        @Query("start") start: String? = null,
        @Query("end") end: String? = null
    ): List<List<Any>>

    @GET("/api/data/{key}/stats")
    suspend fun getMetricStats(
        @Path("key") key: String,
        @Query("start") start: String? = null,
        @Query("end") end: String? = null
    ): StatsResponse

    @GET("/api/virtual_metrics")
    suspend fun getVirtualMetrics(): List<VirtualMetric>
}
