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

## Forge 1.21.1 branch

The second target on **the same game version**, so the whole vanilla part carried over
without a single edit — only the loader layer changed. Errors: 1672 → 588 → 117 → 100 → 0.

### What had to be reworked, not renamed

- **`DeferredHolder` doesn't exist in Forge** — back to `RegistryObject`. This is the bulk
  of the edits: registries run through the entire mod, and in dozens of places the value
  has to be obtained differently.
- **Forge has no data attachments.** Moving to NeoForge we replaced capabilities with
  them; now back — but not to the 1.20.1 shape, because capabilities in Forge 1.21.1 also
  differ from the old ones.
- **The network is different again:** Forge kept `SimpleChannel`, and packet records plug
  into it.
- **HUD rendering:** the events NeoForge used don't exist in Forge at all — the health bar
  and tooltips are wired through GUI layers. **A substitution, not a translation.**
- **Curios doesn't exist for Forge 1.21.1** (Forge builds stop at 1.20.6; the project moved
  to NeoForge). The integration is honestly switched off: the mod already asks "is Curios
  loaded" everywhere.

### Build: three incompatibilities in a row

ForgeGradle doesn't work on Gradle 9 → pinned Gradle 8.8. Gradle 8.8 doesn't understand the
system Java 25 → pointed at Java 21. Both constraints apply to this branch only.

### Four breakages invisible to the compiler

1. **`type = "required"` in the manifest.** In Forge the field is called `mandatory`. The
   loader rejected the mod file itself — "invalid mod file", before reaching any code.
2. **Main class constructor.** NeoForge passes the event bus as an argument, Forge doesn't:
   the bytecode shows it looks for a constructor with its own context, then a no-arg one.
3. **Launching from the dev environment.** ForgeGradle serves classes and resources as two
   directories, and FML counts the resources directory as a separate mod: manifest present,
   classes absent. Neither the run-configuration shape nor `copyIdeResources` is relevant —
   the only fix was collecting resources into the same directory as classes. **Invisible on
   a clean server**, where the jar already has them together.
4. **Cancelling an uncancellable event.** NeoForge removed "skip target" from the
   projectile-impact event and replaced it with cancellation; on the way back to Forge, the
   NeoForge form survived along with the comment explaining NeoForge logic. In Forge the
   old way never went anywhere, and the event is uncancellable — crash on the first hit.

**Lesson for future backports:** comments explaining "why it's done this way on NeoForge"
are more dangerous than the code itself when returning to Forge — they look like
justification and hide that the decision no longer applies.

### Verified

Clean Forge 52.1.16 server outside the dev environment: `Done (3.461s)`, zero errors. The
client launches. **The Forge build has not been battle-tested in game** — the stress test,
dungeons, weapons and shaders were verified on NeoForge. First thing to look at is the boss
health bar: it is wired differently.
