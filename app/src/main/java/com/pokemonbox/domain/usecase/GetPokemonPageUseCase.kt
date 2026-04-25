package com.pokemonbox.domain.usecase

import com.pokemonbox.domain.model.Pokemon
import com.pokemonbox.domain.repository.PokemonRepository

class GetPokemonPageUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(offset: Int, limit: Int): List<Pokemon> {
        return repository.getPokemonPage(offset, limit)
    }
}
