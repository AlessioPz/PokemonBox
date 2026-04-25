package com.pokemonbox.presentation

import com.pokemonbox.domain.model.Pokemon

data class MainUiState(
    val pokemons: List<Pokemon> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val isInitialLoading: Boolean = true,
    val canLoadMore: Boolean = true,
    val errorMessage: String? = null,
    val selectedTab: PokemonTab = PokemonTab.ALL,
    val favoriteIds: Set<Int> = emptySet()
)

enum class PokemonTab {
    ALL,
    FAVORITES
}
