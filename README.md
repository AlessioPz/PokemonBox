# PokemonBox

Android Kotlin project built to match the technical exercise requirements:

- `MVVM` architecture with clear layers (`presentation`, `domain`, `data`)
- `Use case` types for business logic
- `Repository` for data access
- Asynchronous work with `Kotlin Coroutines`
- Dependency injection with `Koin`
- Networking with `Retrofit` + `Gson`

## Features

- Paginated Pokémon list (20 items per page)
- Auto-loads the next page when scrolling near the end
- Search by name or type among loaded Pokémon
- Favorites with local persistence (`Room`)
- Dedicated `Favorites` tab with quick removal from the list
- Tap a Pokémon to open the detail screen (API by id)
- UI inspired by the provided mockup (title, search bar, list cards)

## Project layout

- `presentation`: `MainActivity`, `MainViewModel`, `MainUiState`, `PokemonAdapter`
- `domain`: `Pokemon`, `PokemonRepository`, list use cases + favorites use cases
- `data`: `PokeApiService`, DTOs, `PokemonRepositoryImpl`
- `di`: Koin modules

## API

Base URL: `https://pokeapi.co/api/v2/`

## Launcher icon

The app uses a `PB` monogram with a yellow/black/red palette (Pikachu-inspired), with adaptive icon (API 26+).
