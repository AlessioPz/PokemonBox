package com.pokemonbox.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemonbox.domain.usecase.GetPokemonByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getPokemonByIdUseCase: GetPokemonByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState(isLoading = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentPokemonId: Int? = null

    fun load(pokemonId: Int) {
        if (currentPokemonId == pokemonId && _uiState.value.pokemon != null) return
        currentPokemonId = pokemonId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                getPokemonByIdUseCase(pokemonId)
            }.onSuccess { pokemon ->
                _uiState.update { it.copy(pokemon = pokemon, isLoading = false, errorMessage = null) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Errore nel caricamento dettaglio") }
            }
        }
    }
}
