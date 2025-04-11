package com.faist.stockexchange.data

import retrofit2.http.GET

interface ApiService {

    @GET("aggs/ticker/AAPL/range/1/hour/2023-11-30/2024-11-29?adjusted=true&sort=desc&limit=50000&apiKey=KEY")
    suspend fun loadBars(): Result
}
