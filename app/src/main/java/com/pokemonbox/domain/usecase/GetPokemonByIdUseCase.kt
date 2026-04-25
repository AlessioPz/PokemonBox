package com.pokemonbox.domain.usecase

import com.pokemonbox.domain.model.Pokemon
import com.pokemonbox.domain.repository.PokemonRepository

class GetPokemonByIdUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(id: Int): Pokemon = repository.getPokemonById(id)
}
