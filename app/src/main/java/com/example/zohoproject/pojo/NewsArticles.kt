package com.example.zohoproject.pojo

data class NewsArticles(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<NewsItem>

)

