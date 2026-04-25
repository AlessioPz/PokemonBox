package com.pokemonbox.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val sprites: SpritesDto,
    val types: List<TypeSlotDto>,
    val height: Int,
    val weight: Int,
    val abilities: List<AbilitySlotDto>
)

data class SpritesDto(
    @SerializedName("front_default")
    val frontDefault: String?,
    val other: OtherSpritesDto?
)

data class OtherSpritesDto(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtworkDto?
)

data class OfficialArtworkDto(
    @SerializedName("front_default")
    val frontDefault: String?
)

data class TypeSlotDto(
    val slot: Int,
    val type: TypeDto
)

data class TypeDto(
    val name: String
)

data class AbilitySlotDto(
    val ability: AbilityDto
)

data class AbilityDto(
    val name: String
)
