# EEEAB's Mobs: Forge 1.20.1 → NeoForge 1.21.1

Original: https://github.com/EEEAB/EEEABsMobs, master branch, LGPL-3.0 covering code and
assets together. 420 java files, 48k lines, 163 entities, 54 renderers, 49 models,
287 textures, 181 sounds.

## Why nobody took this mod on

The assumed blocker was the Citadel dependency. **It doesn't exist.** The block in
`mods.toml` is commented out wholesale, there are zero imports in 420 files, zero in the
gradle files, and the mod has its own animation engine (`com.eeeab.animate`). Citadel on
Modrinth is a leftover from an old version.

The real reason seems to be different: **the author's published sources do not compile.**
- a mob `fallen_envoy` is registered whose classes exist in no branch and no release;
- the particle type `RADIAL_OPACITY_RING` is used but never registered;
- a renderer calls a nonexistent method on the entity;
- the config handler is registered twice (Forge tolerated it, NeoForge crashes).

## Mechanics: 2827 → 0

Compile errors: 2827 → 1517 (package renames) → 1024 (entities) → 916 (client)
→ 224 (platform) → 123 (datagen exclusion) → 0.

Every name mapping was verified by finding the class in real NeoForge artifacts, not from
memory: `SubscribeEvent` → `net.neoforged.bus.api`, `Dist` → `net.neoforged.api.distmarker`,
`Mod`/`EventBusSubscriber` annotations → `net.neoforged.fml.common` (in `loader-4.0.44.jar`),
`ForgeConfigSpec` → `ModConfigSpec`, `MinecraftForge` → `NeoForge`.

The `ResourceLocation` constructor became private: two arguments → `fromNamespaceAndPath`,
**one argument → `parse`**. The first pass missed this distinction and produced 19 errors.

## Datagen: the decision and its price

101 of the 224 errors came from the data generator. It never lands in the jar and only
creates files that already sit in resources, so it was excluded from compilation.

**But its output is absent from the repository.** Comparison against the author's released
jar:
- data: 295 files in the jar vs 42 in sources — **254 missing**;
- assets: 949 in the jar vs 535 here — **419 missing** (162 block models, 129 item models,
  84 blockstates, 6 particle textures, the boss health bar, the effect icon);
- translations: 26 keys per language (spawn eggs, tooltips, settings strings).

Without this: almost no recipes, blocks drop nothing, **every block is "missing model"**,
structures never generate.

**Lesson: if datagen is disabled, compare the ENTIRE tree against the release jar, not just
the section where a problem was noticed.** The data was found and fixed first; resources
were checked only later — and the hole there turned out even bigger.

## Eight breakages invisible to the compiler

Each found by running the game with a fully green build.

1. **Client proxy on the server.** `ClientProxy` is marked `@OnlyIn(Dist.CLIENT)`, so its
   name must not appear in the main class's bytecode at all. Neither a ternary nor a
   separate method helps — constructor resolution loads the class anyway. Forge had
   `DistExecutor` precisely for lazy linking; NeoForge doesn't, so reflection was needed.
2. **Brewing event on the wrong bus** — not marked as a mod event, so it belongs to the
   game bus.
3. **Double registration of the config handler** — and on top of that, NeoForge also
   considers registering a class with zero handlers an error.
4. **Advancement conditions** were registered from static init at load completion, when
   registries are already frozen. Moved to the deferred register.
5. **Range in worldgen** was wrapped in a redundant `value` object. Field names didn't
   change — a name-based diff can't catch this; only loading can.
6. **Additional models** require a special identifier kind (`standalone`), resolved from
   the model root, not from the item subfolder.
7. **Three animation packages unregistered.** A TODO was left for the animation-engine
   pass, which had already rewritten them. An unregistered package is not a compile
   error, so it survived into the game: the very first boss crashed the server.
8. **Armor texture.** The original set the path through a Forge hook that doesn't exist;
   now the armor material builds the path, and the `textures/models/armor/` prefix is
   hardcoded in the game.

## Verified

Dev-environment server: zero errors. Client: mod warnings 3527 → 0.
**Clean NeoForge 21.1.249 server outside the dev environment: `Done (3.251s)`, zero errors.**
In-game verification by the user:
- 100 different mobs and 100 bosses fighting at once — **no frame drops**;
- all dungeons generate and work, all weapons work;
- launch with **Sodium + Iris and the Complementary shader pack** plus JEI — zero crashes,
  zero mod errors. This matters separately: the custom boss render layers (glow, trails,
  beams, void cracks) were rewritten blind from bytecode, and shaders swap the pipeline and
  break exactly that first.

Note: Oculus does not exist for NeoForge 1.21.1; Iris took over its role with builds for
this loader.

Version range narrowed to `[1.21.1,1.21.2)`: the shields port already taught us that a wide
range lets the mod onto versions where it crashes instead of failing honestly.

## Deliberately not done

- Data generator not ported (101 errors); files taken from the release.
- Tags for third-party mods (Alex's Caves, Cataclysm, Ice and Fire) unverified — those mods
  aren't in the pack, nothing to check against.
- 19 blocks are untranslated upstream as well; English names were added on top of the
  original.

## Fabric 1.21.1 branch

The third target on the same game version and **the hardest one**: Fabric is furthest from
the source Forge. Errors: 1833 → 1193 → 1094 → 981 → 652 → 532 → 371 → 137 → 0.

### The decision that saved most of the work

**Official Mojang mappings instead of Yarn.** The game version is the same as the NeoForge
and Forge branches, so with the same names the entire vanilla-facing half of the mod —
163 entities, renderers, models, items — compiles without a single edit. With Yarn, every
call into the game would have to be renamed, i.e. nearly the whole mod rewritten for zero
gain.

### What had to be invented, not translated

- **There is no event bus at all.** Where Fabric has a callback, the callback is used;
  where it doesn't, a mixin was written. The mod already ships its own mixin file.
- **No config system.** 758 lines of Forge declarations replaced with custom JSON reading,
  keeping the same call shape so hundreds of call sites stay untouched.
- **Registries.** A `FabricDeferredRegister` shim preserves the familiar call shape.
  Crucially, there is exactly one for the whole port: if every pass had made its own, the
  merge would have turned into a mess.
- **Entity attachments.** The initial assumption was that Fabric had no equivalent and a
  mixin was prescribed; verification against the jar found the official data attachment
  module instead — and objected. The result is more reliable.
- **Biome modifiers** are a Forge concept; Fabric ignores the Forge files, so structure
  generation was rebuilt through Fabric's biome API.

### Breakages invisible to the compiler

1. **Init order in the config:** a static block triggered loading before the JSON parser
   field was declared. The same error class as the throwing axe on NeoForge.
2. **Client classes on the server.** Fabric strictly separates sides and refuses to load a
   class referencing client types. All six "server → client" packages kept their handler
   inside themselves — the server died on the very first registration line. Handlers were
   moved into nested classes.
3. **Entity renderers never registered.** `RegisterHandler` was fully rewritten for Fabric,
   but nobody called it: one pass left a note "not wiring to avoid guessing", a later one
   finished the file without knowing it had to come back. The game didn't know what to draw
   mobs with — crash on opening the second creative tab page and on any mob spawning.
4. **Armor rendered empty.** On NeoForge the mod substituted only the model, and vanilla
   drove part visibility per slot. Taking over the whole render, the port hid every part
   and returned none. From the outside it looks like a missing texture.

**Six times in this project** the seam between passes produced code that compiles, starts
cleanly, and does nothing. Neither the compiler, nor a server launch, nor a client launch
catches that.

### The false alarm worth remembering

Complaints about washed-out textures, tiny particles and a weak lightning bolt turned out
to be a comparison **of shaders versus no shaders**: the previous run used Sodium, Iris and
a shader pack, while the Fabric client launched clean. The particle and render-layer code
was diffed line by line against the working branch — it hadn't changed.

**Lesson:** before looking for the cause of a visual discrepancy in code, make sure the
conditions being compared are identical.
