package com.pokemonbox

import android.app.Application
import com.pokemonbox.di.dataModule
import com.pokemonbox.di.domainModule
import com.pokemonbox.di.networkModule
import com.pokemonbox.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PokemonBoxApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PokemonBoxApp)
            modules(
                networkModule,
                dataModule,
                domainModule,
                presentationModule
            )
        }
    }
}
