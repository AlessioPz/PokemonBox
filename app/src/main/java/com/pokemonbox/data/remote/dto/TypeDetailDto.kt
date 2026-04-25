package com.pokemonbox.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TypeDetailDto(
    @SerializedName("damage_relations")
    val damageRelations: DamageRelationsDto
)

data class DamageRelationsDto(
    @SerializedName("double_damage_from")
    val doubleDamageFrom: List<TypeDto>,
    @SerializedName("half_damage_from")
    val halfDamageFrom: List<TypeDto>,
    @SerializedName("no_damage_from")
    val noDamageFrom: List<TypeDto>
)
