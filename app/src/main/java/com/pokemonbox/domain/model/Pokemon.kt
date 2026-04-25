package com.pokemonbox.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: List<String>,
    val description: String,
    val isFavorite: Boolean = false,
    val hp: Int? = null,
    val heightMeters: Double? = null,
    val weightKg: Double? = null,
    val abilities: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val resistances: List<String> = emptyList()
)
