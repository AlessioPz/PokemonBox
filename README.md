# PokemonBox

Android Kotlin app built to match the technical exercise requirements:

- **MVVM** with separate layers: `presentation`, `domain`, `data`
- **Use cases** for list/detail data and favorites
- **Repository** + **Retrofit** (`PokeApiService`) and **Room** (favorites)
- **Kotlin coroutines** / **Flow**
- **Koin** for DI
- **Gson** (Retrofit) for JSON, **KSP** for Room
- **View binding**
- **Coil** for images
- **Detekt** (see `app/build.gradle.kts`)

## Features

- Paginated home list (20 items per request), loads more when scrolling near the end
- Tabs **All** / **Favorites**; favorites stored with Room; quick remove on the Favorites tab
- Search (bottom sheet) by name or type on the loaded set
- **Detail** screen: by-id API, scrollable main card, **faux-TCG** layout, optional **type-based** weakness/resistance hints (from PokeAPI `type` damage relations, not real TCG)
- **Shared element** image transition list → detail; custom transition set (`res/transition/`)
- **Landscape** detail: two columns (art + height/weight on the left; text and stats on the right) in `res/layout-land/`
- Edge-to-edge insets, Material cards / bottom sheet
- `ActivityConstants` for shared screen constants

## Project layout (under `app/src/main/java/com/pokemonbox/`)

| Package / area | Role |
|----------------|------|
| `MainActivity`, `DetailActivity` | UI entry points; `ActivityConstants` (shared KTX-style constants) |
| `di` | Koin: Retrofit, Room, repository, use cases, `MainViewModel`, `DetailViewModel` |
| `presentation` | `MainViewModel` / `MainUiState` / `PokemonAdapter`; `detail/DetailViewModel` / `DetailUiState` |
| `domain` | `Pokemon`, `PokemonRepository`, use cases: page, by-id, favorites observe/toggle/remove |
| `data` | `PokeApiService`, DTOs (`pokemon`, `pokemon-species`, `type`), `PokemonRepositoryImpl` |
| `data/local` | Room: `PokemonBoxDatabase`, `FavoritePokemonDao`, `FavoritePokemonEntity` |
| `ui` | `SharedTransition` (shared element names) |

## Notable `res` files

- `layout/activity_main.xml`, `item_pokemon.xml`, `bottom_sheet_search.xml`
- `layout/activity_detail.xml`, `layout-land/activity_detail.xml`, `include_detail_tcg_attacks.xml`
- `transition/shared_element_pokemon_image.xml` (used from `themes.xml`)

## API

Base URL: `https://pokeapi.co/api/v2/`

## Launcher icon

`PB` monogram, yellow / black / red (Pikachu-inspired), **adaptive icon** (API 26+).
