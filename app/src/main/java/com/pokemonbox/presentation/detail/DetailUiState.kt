package com.pokemonbox.presentation.detail

import com.pokemonbox.domain.model.Pokemon

data class DetailUiState(
    val pokemon: Pokemon? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
