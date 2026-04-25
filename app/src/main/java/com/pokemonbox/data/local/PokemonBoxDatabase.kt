package com.pokemonbox.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pokemonbox.data.local.dao.FavoritePokemonDao
import com.pokemonbox.data.local.entity.FavoritePokemonEntity

@Database(
    entities = [FavoritePokemonEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonBoxDatabase : RoomDatabase() {
    abstract fun favoritePokemonDao(): FavoritePokemonDao
}
