# EEEAB's Mobs — Unofficial Port

Unofficial port of EEEAB's Mobs to Minecraft 1.21.1 for all three mod loaders (this branch).

Original mod: EEEAB's Mobs (Forge 1.20.1), licensed **LGPL-3.0** — this repository
publishes the port's sources as the license requires.

## Layout

The MC version is the branch name; each loader is a self-contained Gradle project in its own folder:

```
fabric/      — Fabric build
forge/       — Forge build
neoforge/    — NeoForge build
```

Build any one of them independently, e.g.:

```
cd neoforge
./gradlew build
```

Per-loader porting notes are in each project's `PORT.md`.
