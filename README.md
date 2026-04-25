# PokemonBox

Progetto Android Kotlin realizzato secondo i requisiti dell'esercizio tecnico:

- Architettura `MVVM` con separazione a layer (`presentation`, `domain`, `data`)
- `Use Case` per la business logic
- `Repository` per l'accesso ai dati
- Chiamate asincrone con `Kotlin Coroutines`
- Dependency Injection con `Koin`
- Networking con `Retrofit` + `Gson`

## Funzionalita

- Lista Pokemon paginata (20 elementi per pagina)
- Caricamento automatico della pagina successiva in fondo alla lista
- Ricerca per nome o tipo sui Pokemon caricati
- Gestione preferiti con persistenza locale (`Room`)
- Tab dedicata `Preferiti` con rimozione rapida dalla lista preferiti
- Tap su un Pokemon per aprire la pagina dettaglio con chiamata API by id
- UI ispirata al mockup fornito (titolo, barra ricerca, card lista)

## Struttura progetto

- `presentation`: `MainActivity`, `MainViewModel`, `MainUiState`, `PokemonAdapter`
- `domain`: `Pokemon`, `PokemonRepository`, use case lista + use case preferiti
- `data`: `PokeApiService`, DTO, `PokemonRepositoryImpl`
- `di`: moduli Koin

## API

Base URL: `https://pokeapi.co/api/v2/`

## Icona launcher

L’app usa un’icona con monogramma `PB` e palette giallo/nera/rossa (ispirata a Pikachu), con adaptive icon (API 26+).
