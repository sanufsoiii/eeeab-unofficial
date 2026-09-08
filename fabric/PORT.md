# EEEAB's Mobs: Forge 1.20.1 → NeoForge 1.21.1

Original: https://github.com/EEEAB/EEEABsMobs, master branch, LGPL-3.0 covering code and assets together.
420 Java files, 48k lines, 163 entities, 54 renderers, 49 models, 287 textures, 181 sounds.

The work was done in isolated branches — platform, entities, client, data — plus mechanical
sweeps and hand analysis of each launch.

## Why nobody had ported this mod

The blocker was believed to be a dependency on Citadel. **There is none.** The block in `mods.toml`
is commented out entirely, there are zero imports across the 420 files, zero references in the gradle
files, and the mod ships its own animation engine (`com.eeeab.animate`). On Modrinth, Citadel only
shows up as a leftover from an old version.

The real reason appears to be different: **the author's published sources do not build.**
- a mob `fallen_envoy` is registered, but its classes exist in no branch and no release;
- a particle type `RADIAL_OPACITY_RING` is used but never registered anywhere;
- a renderer calls a method that does not exist on the entity;
- the config handler is registered twice (Forge tolerated it, NeoForge crashes).

## Mechanics: 2827 → 0

Compile errors: 2827 → 1517 (package renames) → 1024 (entities) → 916 (client)
→ 224 (platform) → 123 (datagen excluded) → 0.

Every name mapping was checked by looking the class up in the real NeoForge artifacts, not from
memory: `SubscribeEvent` → `net.neoforged.bus.api`, `Dist` → `net.neoforged.api.distmarker`,
the `Mod`/`EventBusSubscriber` annotations → `net.neoforged.fml.common` (in `loader-4.0.44.jar`),
`ForgeConfigSpec` → `ModConfigSpec`, `MinecraftForge` → `NeoForge`.

The `ResourceLocation` constructor became private: two arguments → `fromNamespaceAndPath`,
**one argument → `parse`**. The first pass missed that distinction and produced 19 errors.

## Datagen: the decision and its cost

101 of the 224 errors were in the data generator. It never ends up in the jar and only produces files
that already sit in resources, so it was excluded from compilation.

**But its output is missing from the repository.** Comparison against the author's released jar:
- data: 295 files in the jar, 42 in the sources — **254 missing**;
- resources: 949 in the jar, 535 here — **419 missing** (162 block models, 129 item models,
  84 block states, 6 particle textures, the boss health bar, the effect icon);
- translations: 26 keys per language (spawn eggs, tips, config strings).

Without these: almost no recipes, blocks drop nothing, **every block is "missing model"**, and
structures do not generate at all.

**Takeaway: if datagen is disabled, diff the entire tree against the release jar, not just the section
where you noticed a problem.** The data hole was found and fixed first; only afterwards did checking
the resources reveal an even bigger gap.

## Eight breakages invisible to the compiler

Each was found by launching with a fully green build.

1. **Client proxy on the server.** `ClientProxy` is `@OnlyIn(Dist.CLIENT)`, so its name must not appear
   in the main class's bytecode at all. Neither a ternary nor a separate method helps — constructor
   resolution still loads the class. Forge had `DistExecutor` precisely for this lazy binding; NeoForge
   has no equivalent, so reflection was used.
2. **Potion-brewing event on the wrong bus** — not marked as a mod event, so it went to the game bus.
3. **Double config-handler registration** — and, next, NeoForge also treats registering a class with no
   handlers at all as an error.
4. **Advancement conditions** were registered from a static initializer at load completion, when the
   registries are already frozen. Moved to a deferred register.
5. **A worldgen range** was wrapped in an extra `value` object. Field names did not change, so a
   name-based diff cannot catch it — only loading does.
6. **Extra models** require a special identifier kind (`standalone`), which looks for the file from the
   models root, not in the items subfolder.
7. **Three animation packages were not registered.** The platform pass left a TODO for whoever ported
   the animation engine; that work was already done. An unregistered package is not a compile error, so
   it survived into the game: the very first boss crashed the server.
8. **Armor texture.** The original set the path via a Forge hook that no longer exists; the path is now
   built by the armor material, and the `textures/models/armor/` prefix is hard-coded in the game.

## Verified

Dev-environment server: zero errors. Client: mod warnings 3527 → 0.
**Clean NeoForge 21.1.249 server outside the dev environment: `Done (3.251s)`, zero errors.**
In-game check by the user:
- 100 different mobs and 100 bosses fighting at once — **no frame drops**;
- all dungeons generate and work, every weapon works;
- launched with **Sodium + Iris and the Complementary shaderpack** plus JEI — zero crashes, zero mod
  errors. This matters on its own: the mod's custom boss render layers (glow, trails, beams, void
  cracks) were rewritten blind from bytecode, and shaders replace the pipeline and break exactly that
  kind of thing first.

Note: Oculus does not exist for NeoForge 1.21.1; Iris took its place and now has builds for this loader.

The version range is narrowed to `[1.21.1,1.21.2)`: with shields we already got burned by a wide range
letting the mod load on versions where it crashes instead of failing honestly.

## Deliberately not done

- The data generator is not ported (101 errors); files were taken from the release.
- Tags for third-party mods (Alex's Caves, Cataclysm, Ice and Fire) are unchecked — those mods are not
  in the pack, so there is nothing to check against.
- 19 blocks are untranslated even upstream; English names were added here beyond the original.

## Fabric 1.21.1 branch

The third target on the same game version, and **the hardest**: Fabric is furthest from the source Forge.
Errors: 1833 → 1193 → 1094 → 981 → 652 → 532 → 371 → 137 → 0.

### The decision that saved most of the work

**Official Mojang mappings were used instead of Yarn.** The game version is the same as the NeoForge and
Forge branches, so with the same names the entire vanilla half of the mod — 163 entities, renderers,
models, items — compiles without a single change. With Yarn, every call into the game would have to be
renamed, i.e. rewriting almost the whole mod for zero benefit.

### What had to be invented rather than translated

- **There is no event bus at all.** Where Fabric has a callback, it was used; where it does not, a mixin
  was written. The mod already ships its own mixin file.
- **There is no config system.** 758 lines of Forge spec were replaced with custom JSON reading that keeps
  the previous call shape, to avoid rewriting hundreds of call sites.
- **Registries.** A `FabricDeferredRegister` shim was written to preserve the familiar call shape. It is
  deliberately a single shim for the whole port: a separate one per branch would have turned merging into
  a mess.
- **Entity render states.** The initial task description was wrong here — it claimed Fabric has no such
  thing and called for a mixin. Verification against the jar found the official data-attachments module
  instead, which is more robust.
- **Biome modifiers** are a Forge concept; Fabric ignores the Forge files, so structure generation was
  rebuilt through Fabric's biome API.

### Breakages invisible to the compiler

1. **Init order in the config:** a static block triggered loading before the JSON-parser field was
   declared. Same class of bug as the throwing axe on NeoForge.
2. **Client classes on the server.** Fabric strictly separates sides and refuses to load a class that
   mentions client types. All six "server → client" packages kept their handler inside themselves — the
   server crashed on the first registration line. Handlers were moved into nested classes.
3. **Entity renderers were not registered.** `RegisterHandler` was fully rewritten for Fabric, but nothing
   called it: one pass left a note "not wiring this up, to avoid guessing", and a later pass filled in the
   file without knowing it had to come back. The game did not know how to render the mobs — a crash on
   opening the second creative-tab page and on any mob spawning.
4. **Armor rendered empty.** On NeoForge the mod swapped only the model, and vanilla decided which parts
   are visible per slot. Taking over rendering entirely, the port hid every part and turned none back on.
   Externally this looks like a missing texture.

**Six times across the project**, the seam between passes produced code that compiles, boots cleanly, and
does nothing. Neither the compiler, nor the server launch, nor the client launch catches this.

### A false alarm worth remembering

Complaints about washed-out textures, tiny particles and weak lightning turned out to be a comparison
**with shaders vs. without**: the previous run used Sodium, Iris and a shaderpack, while the Fabric client
launched vanilla. The particle and render-layer code was diffed line by line against the working branch —
it had not changed.

**Takeaway:** before hunting for a visual discrepancy in the code, make sure you are comparing identical
conditions.
