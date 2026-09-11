# SeedProtector

A lightweight Paper plugin that randomizes structure seeds to prevent players from cracking your world seed.

Built for Paper / Purpur 26.2 (Java 25).

---

### The Problem

On standard Minecraft, Spigot, and Paper servers, structures (villages, desert temples, ocean monuments, etc.) generate using hardcoded salt constants.

Modded clients like **SeedcrackerX** can reverse-engineer your world seed in minutes just by looking at the coordinates and rotations of 2–3 structures. Once someone has your world seed, anti-xray won't save you—they can just load the seed in singleplayer or an online map viewer and know where every ore, spawner, and structure is.

Paper and Spigot already have settings in `spigot.yml` and `paper-world-defaults.yml` to change these structure seeds, but manually generating random numbers for 20+ different structures and editing YAML files by hand is annoying.

SeedProtector checks your server on startup, warns you if you're still using default seeds, and randomizes all of them with a single command.

---

### Features

- Scrambles 21 structure seeds in `spigot.yml` with unique random values
- Automatically turns on `generate-random-seeds-for-all: true` in `config/paper-world-defaults.yml`
- `/seedprotector audit` command to check if any structures are still using default seeds
- Warns online admins if default seeds are detected on join
- Zero external runtime dependencies, native Adventure text

---

### How to Use

> **Note:** Minecraft only uses structure seeds when **generating new chunks**. If your world has already generated chunks with default seeds, changing the config won't fix those existing chunks.

1. Drop `SeedProtector.jar` into your `plugins/` folder.
2. Start the server once so Spigot and Paper generate their default config files.
3. Run `/seedprotector randomize` (from console or in-game with OP).
4. Stop the server (`stop`).
5. Delete your world folders (`world`, `world_nether`, `world_the_end`).
6. Start the server again to generate a fresh, protected world.
7. Run `/seedprotector audit` to confirm everything is randomized.

---

### Commands & Permissions

| Command | Subcommands / Alias | Permission | Default | Description |
|---|---|---|---|---|
| `/seedprotector audit` | `/sp audit` (or `check`, `status`) | `seedprotector.audit` | OP | Checks all structure seeds and reports any defaults |
| `/seedprotector randomize` | `/sp randomize` (or `shuffle`, `scramble`) | `seedprotector.randomize` | OP | Writes randomized seeds to `spigot.yml` and Paper config |
| `/seedprotector help` | `/sp help` | None | Everyone | Shows command usage |

Primary command: `/seedprotector`  
Alias: `/sp`

---

### Structures Protected

Village, Desert Pyramid, Igloo, Jungle Temple, Swamp Hut, Ocean Monument, Shipwreck, Ocean Ruin, Pillager Outpost, End City, Slime Chunks, Nether Complex, Woodland Mansion, Fossil, Ruined Portal, Ancient City, Trail Ruins, Trial Chambers, Buried Treasure, Mineshaft, and Stronghold.

---

### Building from Source

Requires JDK 25.

```bash
# Windows
.\gradlew.bat clean build

# Linux / macOS
./gradlew clean build
```

The output jar will be in `build/libs/SeedProtector-1.0.0.jar`.

---

### License

[MIT](LICENSE) — Made by [UsefulOrb](https://usefulorb.me)
