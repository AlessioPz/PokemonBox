package com.pokemonbox.domain.repository

import com.pokemonbox.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemonPage(offset: Int, limit: Int): List<Pokemon>
    suspend fun getPokemonById(id: Int): Pokemon
    fun observeFavoriteIds(): Flow<Set<Int>>
    suspend fun toggleFavorite(pokemonId: Int)
    suspend fun removeFavorite(pokemonId: Int)
}
