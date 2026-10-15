# ⚡ PokéShowdown (Native Android Client)

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen.svg)](https://developer.android.com/jetpack/compose)

A feature-rich, high-performance **native Android application** for [Pokémon Showdown](https://pokemonshowdown.com/). Built entirely with modern **Jetpack Compose**, this app brings real-time competitive Pokémon battling to mobile devices with dynamic 2D battle arenas, live Showdown API syncing, custom attack animations, and an interactive visual teambuilder.

---

## 🎨 Vibe Coded Disclaimer

> [!NOTE]
> **This project was mostly vibe coded.** 
> Built rapidly through interactive, prompt-driven AI pair programming.
---

## 🚀 What It's For

PokéShowdown gives Android users a slick, responsive, native client alternative to the web browser interface. It connects directly to the official Pokémon Showdown server cluster (`wss://sim3.psim.us/showdown/websocket`), enabling seamless online matchmaking, direct player challenges, and teambuilding on the go.

---

## ✨ Features

### ⚔️ Real-Time 2D Battle Arena
* **Live WebSocket Protocol Engine**: Parses line-delimited Showdown pipe commands (`|switch|`, `|move|`, `|turn|`, `|-damage|`, `|request|`, etc.) in real time.
* **Animated Sprites**: Rendered with Coil 3 using official Showdown animated GIF/WebP front and back sprites.
* **Dynamic Environment Background**:
  * **Day/Sunset/Night Cycles**: Sky and terrain colors transition smoothly as turn counts progress.
  * **Interactive Environment**: Parallax floating clouds and retro Game Boy-style 3-blade grass tufts.
  * **3D Turf Platforms**: Custom oval battle pedestals for player and opponent Pokémon.
* **Custom Attack Animations**: Type-specific visual overlays for Electric zaps, Fire blasts, Water splashes, Grass leaves, Psychic/Ghost vortices, and Physical slash impacts.
* **HP & Status Bars**: Real-time HP progress bars, percent/numeric formatting, and status pill badges (`BRN`, `PAR`, `PSN`, `SLP`, `FRZ`).

### 🔍 Intelligence & Inspection
* **Hold Move Inspection**: Long-press any battle move to view its Base Power, Accuracy, Category, description, and dynamic Type Effectiveness vs. the target.
* **Hold Pokémon Inspection**: Long-press any Pokémon to view full battle stats (Atk, Def, SpA, SpD, Spe), held Item, Ability, Tera Type, and known moveset.
* **Live Showdown Dex Sync**: On startup, the app dynamically fetches and caches official species data (`pokedex.json`), movesets (`moves.json`), and detailed competitive move descriptions (`text.js` / `text/en.js`).

### 🛠️ Interactive Visual Teambuilder
* **6-Slot Visual Picker**: Build custom teams using an intuitive 2x3 grid of rounded rectangle cards.
* **Searchable Pokémon Grid**: Search through Pokémon species with sprite thumbnails and elemental type badges.
* **Showdown Team Packing**: Converts custom visual team setups directly into Showdown's packed team format string (`TeamPacker`).

### 🔒 Authentication & Lobby
* **Guest & Account Login**: Support for instant guest sessions or registered account login via Showdown's `action.php` assertion token API.
* **Matchmaking Queue & Challenges**: Search for matches in `[Gen 9] Random Battle`, `Gen 9 OU`, `Ubers`, `Monotype`, or send direct challenge requests to specific usernames.
* **Lobby & Battle Chat**: Built-in chat feed for global lobby communication and battle room logs.

---

## 🛠️ Tech Stack

* **Language**: Kotlin
* **UI Framework**: Jetpack Compose (Material 3)
* **Networking**: OkHttp 4 (WebSockets & REST APIs)
* **Image Loading**: Coil 3 (GIF & WebP animated sprite rendering)
* **Serialization**: Kotlinx Serialization (JSON parsing)
* **Architecture**: MVVM with Kotlin Coroutines & `StateFlow`

---

## 🔨 Building & Testing

### Prerequisites
* Android Studio (Ladybug / 2024.2+ recommended)
* JDK 17+
* Android SDK (API 26+)

### Gradle Commands
```bash
# Run Unit Tests
./gradlew testDebugUnitTest

# Compile Debug Build
./gradlew assembleDebug
```

---

## 📄 License

This project is open-source software licensed under the **GNU General Public License v3.0 (GPL-3.0)**. See the [LICENSE](LICENSE) file for details.

```text
PokéShowdown - Native Android Pokémon Showdown Client
Copyright (C) 2026

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
```

---

*Disclaimer: Pokémon and Pokémon character names are trademarks of Nintendo, Game Freak, and The Pokémon Company. PokéShowdown is an unofficial open-source fan project and is not affiliated with or endorsed by Nintendo or Pokémon Showdown.*
