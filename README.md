# SeedProtector

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Platform](https://img.shields.io/badge/Paper-26.2-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

**SeedProtector** is a lightweight, high-performance security plugin for Minecraft Paper servers that prevents **seed cracking** by randomizing all structure and feature seeds.

Developed by **[UsefulOrb](https://usefulorb.me)**.

---

## Why Is This Needed?

In standard Minecraft, Spigot, and Paper servers, structures (villages, desert temples, ocean monuments, slime chunks, ancient cities, trial chambers, nether fortresses, etc.) generate using predictable, hardcoded mathematical salt seeds.

Using client-side seed-cracking tools (such as *SeedcrackerX*), malicious players can observe the coordinates and rotation of just a few structures to reverse-engineer the world seed. Once the world seed is cracked:
- Cheaters can run a local copy of your world to locate every ore vein, spawner, ancient city, and dungeon.
- Traditional Anti-Xray protections (like Orebfuscator or Paper's packet hider) are completely bypassed because the player already knows the exact block coordinates offline.

**SeedProtector** eliminates this vulnerability by decoupling structure generation from the world seed and randomizing all structure seeds.

---

## Features

- **Automated Seed Scrambling**: Randomizes 21 distinct structure seeds in `spigot.yml` with cryptographically secure random values.
- **Paper Feature Seed Randomization**: Automatically configures `generate-random-seeds-for-all: true` in `config/paper-world-defaults.yml`.
- **Status Auditing**: Check your server's protection level anytime with `/seedprotector status`.
- **Admin Join Notifications**: Alerts server operators upon login if the server is still running with default seeds.
- **Lightweight & Dependency-Free**: Pure Java with zero runtime dependencies.
- **Backward Compatible**: Supports `/seedshield` as a command alias.

---

## Commands & Permissions

| Command | Permission | Default | Description |
| :--- | :--- | :--- | :--- |
| `/seedprotector status` | `seedprotector.status` | OP | Check whether seeds are randomized or vulnerable. |
| `/seedprotector scramble` | `seedprotector.scramble` | OP | Scramble all structure seeds in `spigot.yml` and Paper config. |

> **Note**: Both commands also accept the `/seedshield` alias (e.g. `/seedshield scramble`).

---

## Quick Start / How to Use

> [!IMPORTANT]
> Structure seeds **only apply to newly generated chunks**. Changing seeds will not affect chunks that have already been generated.

1. Download the latest `SeedProtector-1.0.0.jar` from [Releases](https://github.com/UsefulOrb/SeedProtector/releases) or [Modrinth](https://modrinth.com).
2. Place the JAR into your server's `plugins/` directory.
3. Start the server to generate default configurations.
4. Run `/seedprotector scramble` in the console or as an OP in-game.
5. Follow the prompted instructions:
   1. Stop the server (`stop`).
   2. Delete existing world folders (`world`, `world_nether`, `world_the_end`).
   3. Start the server again to generate a fresh, protected world.
6. Verify protection with `/seedprotector status`.

---

## Monitored Structure Seeds

SeedProtector inspects and randomizes the seeds for all major structures:

- **Overworld**: Village, Desert Pyramid, Igloo, Jungle Temple, Swamp Hut, Ocean Monument, Shipwreck, Ocean Ruins, Pillager Outpost, Fossil, Ruined Portal, Ancient City, Trail Ruins, Trial Chambers, Buried Treasure, Slime Chunks, Mineshaft, Stronghold.
- **The Nether**: Nether Fortress / Nether Complex.
- **The End**: End City, Woodland Mansion.

---

## Building from Source

### Prerequisites
- JDK 25 or higher

### Build
Clone the repository and run:

```bash
# Windows
.\gradlew.bat clean build

# Linux / macOS
./gradlew clean build
```

The compiled plugin will be in `build/libs/SeedProtector-1.0.0.jar`.

---

## Author & License

- **Author**: UsefulOrb ([usefulorb.me](https://usefulorb.me))
- **License**: [MIT License](LICENSE)
