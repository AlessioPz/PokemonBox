package com.pokemonbox.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: List<String>,
    val description: String,
    val isFavorite: Boolean = false
)
