# P000 - Conversational Story

- [Jairo Linares](https://github.com/JJairo-16)

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-blue?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green)

P000 - Conversational Story is a Java console adventure game developed as an
academic project for the Multiplatform Application Development degree.

The player controls Captain Bond aboard the PiaXXII, a spacecraft travelling
toward the planet SUMMEM. After a propulsion failure, Bond must explore the ship,
recover key items, avoid Malien, repair the engines, and restart the mission from
the command room.

The game is built around room navigation, inventory management, route-dependent
item placement, contextual room actions, and a ship computer called iHall that
may help or mislead the player as its patience runs out.

## Highlights

- Console-based text adventure with menu-driven navigation.
- Seeded game generation with multiple possible routes for the crew card.
- Room-specific actions for lockers, desks, tools, flooded drains, engines, and crew interaction.
- Inventory system with usable and placeable items.
- Hidden item visibility rules, including flashlight and snorkel-based reveals.
- Malien entity that moves through the ship and can threaten the player.
- iHall conversation system with route-aware hints and escalating impatience.
- Introductory story shown before each game session.
- JUnit 5 route feasibility tests.

## Game Objective

To complete the adventure, the player must:

- Find or obtain a crew card to access locked ship areas.
- Recover the small key needed to open the astronaut suit locker.
- Equip the astronaut suit.
- Find the repair tool in the workshops.
- Inspect and repair the damaged engines.
- Return to the command room and start the engines.

## Main Items

- `Crew card`: unlocks access to the command room.
- `Small key`: opens the astronaut suit locker.
- `Astronaut suit`: required to work outside the ship safely.
- `Repair tool`: required to repair the engines.
- `Flashlight`: reveals hidden objects in dark areas.
- `Snorkel`: allows inspection of flooded areas.
- `Donut`: can be used to distract Malien.

## Repository Layout

- `game`: Maven project for the console game.
- `docs`: architecture diagrams and supporting documentation.
- `game/src/main/java/com/example`: application entry point.
- `game/src/main/java/com/example/controllers`: game loop, room action handling, iHall, and visibility rules.
- `game/src/main/java/com/example/models/actions`: menu and room action definitions.
- `game/src/main/java/com/example/models/entities`: player, crew member, Malien, and entity turn logic.
- `game/src/main/java/com/example/models/game`: mutable game state and progress flags.
- `game/src/main/java/com/example/models/generation`: seeded route generation.
- `game/src/main/java/com/example/models/items`: item catalog, inventory, and item implementations.
- `game/src/main/java/com/example/models/map`: ship rooms, directions, and map graph.
- `game/src/main/java/com/example/utils`: console input and UI helpers.
- `game/src/test/java/com/example`: route feasibility tests.

## Running The Game

Build the project first:

```bash
cd game
mvn clean package
```

Run the console application:

```bash
java -cp target/classes com.example.App
```

## Development

Run the test suite with:

```bash
cd game
mvn clean test
```

The current tests verify that every prefabricated route can reach the engine
startup sequence and that critical item placement constraints remain valid.

## Academic Context

- Project: P000 - Conversational Story
- Program: Multiplatform Application Development
- Module: MP13 - Dual Module
- Center: Escola Pia

## License

This project is licensed under the [MIT License](LICENSE).

Author: [Jairo Linares](https://github.com/JJairo-16).
