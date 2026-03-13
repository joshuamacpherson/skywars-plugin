# SkyWars

A SkyWars minigame plugin prototype built with the Paper API for Minecraft 1.21.4+.

## Features

- Automatic lobby and countdown system, game reset after each match
- 8 players max per game
- Players are teleported to individual islands on game start
- Elimination tracking with spectator mode on death

## Requirements

- Java 21
- Paper 1.21.4+

## Installation

1. Download or build the plugin jar
2. Drop `skywars.jar` into your Paper server's `plugins/` folder
3. Restart the server

## Building

```bash
./gradlew jar
```

The jar will be output to `build/libs/skywars.jar`.

## Map Setup

The map uses a void world with 8 pre-defined island spawn points arranged in a circle. Islands are located at Y=81. The lobby spawn is at `0, 170, 0`.

Plugin built using this map: https://www.planetminecraft.com/project/skywars-monumental/

If not using this map, edit the island spawn locations in src/main/java/com/example/skywars/GameManager.java and the lobby spawn in src/main/resources/config.yml.

## Game Flow

1. Players join the server and are added to the lobby
2. Countdown starts when 2+ players are present
3. Players are teleported to their islands and the game begins
4. Last player alive wins
5. Server resets after a 10 second countdown

## Tech Stack

- Java 21
- Paper API 1.21.4+
- Kyori Adventure (MiniMessage) for chat formatting

## Notes

- World name must be `world` (default Paper world name)
- This is a prototype — single game instance only, no multi-arena support