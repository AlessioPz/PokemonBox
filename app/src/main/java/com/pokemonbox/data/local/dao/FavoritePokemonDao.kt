package com.pokemonbox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokemonbox.data.local.entity.FavoritePokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePokemonDao {
    @Query("SELECT * FROM favorite_pokemon")
    fun observeAll(): Flow<List<FavoritePokemonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pokemon WHERE pokemonId = :pokemonId)")
    suspend fun isFavorite(pokemonId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FavoritePokemonEntity)

    @Query("DELETE FROM favorite_pokemon WHERE pokemonId = :pokemonId")
    suspend fun deleteByPokemonId(pokemonId: Int)
}
