package com.pokemonbox.data.remote.dto

data class PokemonListResponseDto(
    val results: List<PokemonListItemDto>
)

data class PokemonListItemDto(
    val name: String,
    val url: String
)
