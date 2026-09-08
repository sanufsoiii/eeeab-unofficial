# EEEAB's Mobs — Unofficial Port

Unofficial port of EEEAB's Mobs to Minecraft 1.21.11 (this branch).

Original mod: EEEAB's Mobs (Forge 1.20.1), licensed **LGPL-3.0** — this repository
publishes the port's sources as the license requires.

## Layout

The MC version is the branch name; each loader is a self-contained Gradle project in its own folder:

```
fabric/    — Fabric build
```

Build it independently:

```
cd fabric
./gradlew build
```

Per-loader porting notes are in each project's `PORT.md`.
