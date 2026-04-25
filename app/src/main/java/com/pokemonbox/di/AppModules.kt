package com.pokemonbox.di

import androidx.room.Room
import com.pokemonbox.data.local.PokemonBoxDatabase
import com.pokemonbox.data.remote.PokeApiService
import com.pokemonbox.data.repository.PokemonRepositoryImpl
import com.pokemonbox.domain.repository.PokemonRepository
import com.pokemonbox.domain.usecase.GetPokemonByIdUseCase
import com.pokemonbox.domain.usecase.GetPokemonPageUseCase
import com.pokemonbox.domain.usecase.ObserveFavoriteIdsUseCase
import com.pokemonbox.domain.usecase.RemoveFavoriteUseCase
import com.pokemonbox.domain.usecase.ToggleFavoriteUseCase
import com.pokemonbox.presentation.MainViewModel
import com.pokemonbox.presentation.detail.DetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://pokeapi.co/api/v2/"

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<PokeApiService> {
        get<Retrofit>().create(PokeApiService::class.java)
    }
}

val dataModule = module {
    single {
        Room.databaseBuilder(
            get(),
            PokemonBoxDatabase::class.java,
            "pokemon_box.db"
        ).build()
    }

    single { get<PokemonBoxDatabase>().favoritePokemonDao() }

    single<PokemonRepository> { PokemonRepositoryImpl(get(), get()) }
}

val domainModule = module {
    factory { GetPokemonPageUseCase(get()) }
    factory { GetPokemonByIdUseCase(get()) }
    factory { ObserveFavoriteIdsUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { RemoveFavoriteUseCase(get()) }
}

val presentationModule = module {
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { DetailViewModel(get()) }
}
