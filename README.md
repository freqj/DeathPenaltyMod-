# SoulBoundHearts

Forge mod for Minecraft 1.20.1 that keeps your inventory on death while tying your life force to your soul.

## Features
- Automatically enables the `keepInventory` gamerule for every loaded world.
- Halves the player's maximum health after each death, respecting configurable minimum and maximum caps.
- Adds the craftable **Golden Heart** item that restores two HP (one heart) of maximum health up to the configured cap.
- Persists the custom maximum health value across deaths, dimension changes and reconnects.
- Includes a Forge configuration (`config.toml`) to tweak the death health multiplier and min/max caps.

## Building
This project uses the standard ForgeGradle setup. Import it into your IDE as a Gradle project or run the usual Gradle tasks such as `gradle genIntellijRuns` from a local ForgeGradle environment.
