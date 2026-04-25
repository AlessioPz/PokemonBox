package com.pokemonbox.domain.usecase

import com.pokemonbox.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteIdsUseCase(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<Set<Int>> = repository.observeFavoriteIds()
}
