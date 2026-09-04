# EEEAB's Mobs — Unofficial Port

Unofficial port of EEEAB's Mobs to Minecraft 1.21.1 for all three mod loaders.

Original mod: EEEAB's Mobs (Forge 1.20.1), licensed **LGPL-3.0** — this repository
publishes the port's sources as the license requires.

## Layout

Each loader has its own self-contained Gradle project under `<loader>/<mc-version>/`:

```
fabric/1.21.1/     — Fabric build
forge/1.21.1/      — Forge build
neoforge/1.21.1/   — NeoForge build
```

Build any one of them independently, e.g.:

```
cd neoforge/1.21.1
./gradlew build
```

Per-loader porting notes are in each project's `PORT.md`.
