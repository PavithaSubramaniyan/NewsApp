package com.example.zohoproject.apiService

import com.example.zohoproject.pojo.AirQualityResponse
import retrofit2.http.GET

interface AirQualityApi {

    @GET("parameters")
    suspend fun getAirQuality(): AirQualityResponse
}