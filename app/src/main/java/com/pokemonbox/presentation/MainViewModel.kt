package com.pokemonbox.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemonbox.domain.model.Pokemon
import com.pokemonbox.domain.usecase.GetPokemonPageUseCase
import com.pokemonbox.domain.usecase.ObserveFavoriteIdsUseCase
import com.pokemonbox.domain.usecase.RemoveFavoriteUseCase
import com.pokemonbox.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(
    private val getPokemonPageUseCase: GetPokemonPageUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val loadedPokemons = mutableListOf<Pokemon>()
    private var offset = 0
    private val pageSize = DEFAULT_PAGE_SIZE

    init {
        observeFavorites()
        loadNextPage()
    }

    fun onTabSelected(tab: PokemonTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        applyFilter()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        applyFilter()
    }

    fun retry() {
        _uiState.update { it.copy(errorMessage = null) }
        loadNextPage()
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading ||
            !currentState.canLoadMore ||
            currentState.selectedTab == PokemonTab.FAVORITES
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                getPokemonPageUseCase(offset = offset, limit = pageSize)
            }.onSuccess { newItems ->
                if (newItems.isEmpty()) {
                    _uiState.update { it.copy(canLoadMore = false) }
                } else {
                    loadedPokemons += newItems
                    offset += newItems.size
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isInitialLoading = false
                    )
                }
                applyFilter()
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isInitialLoading = false,
                        errorMessage = "Failed to load data"
                    )
                }
            }
        }
    }

    fun onFavoriteClicked(pokemonId: Int) {
        viewModelScope.launch {
            if (_uiState.value.selectedTab == PokemonTab.FAVORITES) {
                removeFavoriteUseCase(pokemonId)
            } else {
                toggleFavoriteUseCase(pokemonId)
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { ids ->
                _uiState.update { it.copy(favoriteIds = ids) }
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val state = _uiState.value
        val query = state.query.trim().lowercase(Locale.ROOT)
        val favorites = state.favoriteIds
        val source = if (state.selectedTab == PokemonTab.FAVORITES) {
            loadedPokemons.filter { it.id in favorites }
        } else {
            loadedPokemons
        }

        val filtered = if (query.isBlank()) {
            source
        } else {
            source.filter { pokemon ->
                pokemon.name.lowercase(Locale.ROOT).contains(query) ||
                    pokemon.types.any { it.lowercase(Locale.ROOT).contains(query) }
            }
        }.map { pokemon -> pokemon.copy(isFavorite = pokemon.id in favorites) }

        _uiState.update { it.copy(pokemons = filtered) }
    }
}
