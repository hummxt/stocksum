package com.example.stocksum.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "c") val currentPrice: Double,
    @Json(name = "d") val change: Double?,
    @Json(name = "dp") val percentChange: Double?,
    @Json(name = "h") val highPrice: Double,
    @Json(name = "l") val lowPrice: Double,
    @Json(name = "o") val openPrice: Double,
    @Json(name = "pc") val previousClose: Double,
    @Json(name = "t") val timestamp: Long?
)

@JsonClass(generateAdapter = true)
data class SearchResponse(
    val count: Int,
    val result: List<SearchResult>
)

@JsonClass(generateAdapter = true)
data class SearchResult(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class CompanyProfileResponse(
    @Json(name = "logo") val logoUrl: String?
)

interface FinnhubApi {
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): QuoteResponse

    @GET("stock/profile2")
    suspend fun getProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): CompanyProfileResponse

    @GET("search")
    suspend fun searchSymbol(
        @Query("q") query: String,
        @Query("token") token: String
    ): SearchResponse
}
