# EEEAB's Mobs — unofficial NeoForge 1.21.1 port

An **unofficial** port of [EEEAB's Mobs](https://github.com/EEEAB/EEEABsMobs) from **Forge 1.20.1**
to **NeoForge 1.21.1**.

> The original mod — all of its code, models, textures, sounds and structures — is by **EEEAB**.
> This port is not affiliated with or endorsed by the original author. If EEEAB releases an
> official 1.21.1 build, this port will be withdrawn.

## Licence and modifications

The original mod is released under **LGPL-3.0**, covering code and assets alike (`LICENSE.txt`).
This port keeps that licence.

**These sources have been modified from the original.** The whole codebase was migrated from
Forge 1.20.1 to NeoForge 1.21.1: registries, events, capabilities (now data attachments),
networking (now payload-based), rendering, the item/entity API changes that came with 1.21, and
the data and asset formats. `PORT.md` documents what changed and why, including several bugs
that were already present in the upstream sources.

## Requirements

- Minecraft **1.21.1**
- **NeoForge 21.1.249** or newer
- Java 21

## Building

```
./gradlew build
```

The jar lands in `build/libs/`.

Note: `src/main/java/com/eeeab/eeeabsmobs/server/world/datagen/**` is excluded from compilation
(see the comment in `build.gradle`). It is a build-time tool whose generated output is committed
under `src/main/resources`; porting it was not worth the effort. Anyone wanting to regenerate
data will need to port it first.

## Status

Verified by launching a real client and a clean NeoForge server, not merely by compiling:
bosses and their multi-phase fights, animations, boss bars, dungeons, blocks, items and armour
all work, including alongside Sodium, Iris with shaders, and JEI.

Compatibility tags aimed at Alex's Caves, Cataclysm and Ice and Fire were carried over unchanged
but could not be verified against those mods' 1.21.1 tag ids.
