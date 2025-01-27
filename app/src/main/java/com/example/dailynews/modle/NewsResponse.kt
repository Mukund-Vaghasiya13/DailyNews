package com.example.dailynews.modle

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)
