package com.pokemonbox.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesDto(
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntryDto>
)

data class FlavorTextEntryDto(
    @SerializedName("flavor_text")
    val flavorText: String,
    val language: LanguageDto
)

data class LanguageDto(
    val name: String
)
