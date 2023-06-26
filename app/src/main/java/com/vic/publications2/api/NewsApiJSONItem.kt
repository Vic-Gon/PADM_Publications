package com.vic.publications2.api

data class NewsApiJSONItem(
    val author: String,
    val categoria: Categoria,
    val concelho: Concelho,
    val date: String,
    val destaque: String,
    val fonte: String,
    val id: Int,
    val image: String,
    val link: String,
    val subcategoria: Subcategoria,
    val title: String
)