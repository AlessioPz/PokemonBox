package com.pokemonbox.ui

object SharedTransition {
    object PokemonImage {
        private const val PREFIX = "pokemon_image_"
        fun transitionNameFor(pokemonId: Int): String = "$PREFIX$pokemonId"
    }
}
