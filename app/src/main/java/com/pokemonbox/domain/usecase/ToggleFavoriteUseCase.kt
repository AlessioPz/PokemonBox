package com.pokemonbox.domain.usecase

import com.pokemonbox.domain.repository.PokemonRepository

class ToggleFavoriteUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(pokemonId: Int) {
        repository.toggleFavorite(pokemonId)
    }
}
