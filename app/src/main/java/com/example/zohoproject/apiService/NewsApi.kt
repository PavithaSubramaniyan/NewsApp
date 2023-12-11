package com.example.zohoproject.apiService

import com.example.zohoproject.pojo.AirQualityResponse
import com.example.zohoproject.pojo.NewsArticles
import com.example.zohoproject.pojo.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi
{
    @GET("articles")
    suspend fun getNews(
        @Query("format") format: String = "json",
        @Query("limit") limit: Int ,
        @Query("offset") offset: Int
    ): NewsArticles


}