package com.pokemonbox.data.repository

import com.pokemonbox.data.local.dao.FavoritePokemonDao
import com.pokemonbox.data.local.entity.FavoritePokemonEntity
import com.pokemonbox.data.remote.PokeApiService
import com.pokemonbox.domain.model.Pokemon
import com.pokemonbox.domain.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepositoryImpl(
    private val api: PokeApiService,
    private val favoritePokemonDao: FavoritePokemonDao
) : PokemonRepository {

    private companion object {
        /** PokeAPI returns height in decimeters and weight in hectograms; divide by 10 for m and kg. */
        private const val POKEMON_API_STAT_DIVISOR = 10.0
    }

    override suspend fun getPokemonPage(offset: Int, limit: Int): List<Pokemon> = coroutineScope {
        val pageResponse = api.getPokemonPage(offset = offset, limit = limit)
        pageResponse.results.map { item ->
            async {
                val detail = api.getPokemonDetail(item.name)
                val species = api.getPokemonSpecies(detail.id)

                val description = species.flavorTextEntries
                    .firstOrNull { it.language.name == "en" }
                    ?.flavorText
                    ?.replace("\n", " ")
                    ?.replace("\u000c", " ")
                    ?.trim()
                    .orEmpty()

                Pokemon(
                    id = detail.id,
                    name = detail.name.replaceFirstChar { it.uppercase() },
                    imageUrl = detail.sprites.other?.officialArtwork?.frontDefault ?: detail.sprites.frontDefault,
                    types = detail.types.sortedBy { it.slot }.map { slot ->
                        slot.type.name.replaceFirstChar { it.uppercase() }
                    },
                    description = description
                )
            }
        }.awaitAll().sortedBy { it.id }
    }

    override fun observeFavoriteIds(): Flow<Set<Int>> {
        return favoritePokemonDao.observeAll().map { entities ->
            entities.map { it.pokemonId }.toSet()
        }
    }

    override suspend fun getPokemonById(id: Int): Pokemon {
        val detail = api.getPokemonDetail(id.toString())
        val species = api.getPokemonSpecies(detail.id)
        val description = species.flavorTextEntries
            .firstOrNull { it.language.name == "en" }
            ?.flavorText
            ?.replace("\n", " ")
            ?.replace("\u000c", " ")
            ?.trim()
            .orEmpty()
        val typeDetails = detail.types
            .sortedBy { it.slot }
            .map { slot -> api.getTypeDetail(slot.type.name) }
        val weaknesses = typeDetails
            .flatMap { type -> type.damageRelations.doubleDamageFrom }
            .map { type -> type.name.replaceFirstChar { it.uppercase() } }
            .distinct()
        val resistances = typeDetails
            .flatMap { type -> type.damageRelations.halfDamageFrom + type.damageRelations.noDamageFrom }
            .map { type -> type.name.replaceFirstChar { it.uppercase() } }
            .filterNot { weakness -> weaknesses.contains(weakness) }
            .distinct()
        val hp = detail.stats.firstOrNull { it.stat.name == "hp" }?.baseStat
        val heightMeters = detail.height / POKEMON_API_STAT_DIVISOR
        val weightKg = detail.weight / POKEMON_API_STAT_DIVISOR
        val abilities = detail.abilities.map { abilitySlot ->
            abilitySlot.ability.name.replaceFirstChar { it.uppercase() }
        }

        return Pokemon(
            id = detail.id,
            name = detail.name.replaceFirstChar { it.uppercase() },
            imageUrl = detail.sprites.other?.officialArtwork?.frontDefault ?: detail.sprites.frontDefault,
            types = detail.types.sortedBy { it.slot }.map { slot ->
                slot.type.name.replaceFirstChar { it.uppercase() }
            },
            description = description,
            hp = hp,
            heightMeters = heightMeters,
            weightKg = weightKg,
            abilities = abilities,
            weaknesses = weaknesses,
            resistances = resistances
        )
    }

    override suspend fun toggleFavorite(pokemonId: Int) {
        if (favoritePokemonDao.isFavorite(pokemonId)) {
            favoritePokemonDao.deleteByPokemonId(pokemonId)
        } else {
            favoritePokemonDao.insert(FavoritePokemonEntity(pokemonId = pokemonId))
        }
    }

    override suspend fun removeFavorite(pokemonId: Int) {
        favoritePokemonDao.deleteByPokemonId(pokemonId)
    }
}
