# EEEAB → Fabric 1.21.11 port progress

Strategy (user directive 2026-09-05): port TOP-DOWN. Get 1.21.11 fully working, then
backport down to 1.21.10 … 1.21.3, 1.21.1 (each step down = smaller API delta).

Branch: `fabric-1.21.11` off `fabric-1.21.1`. Mojmap mappings (officialMojangMappings).
Reference mojmap jar for javap:
`~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged/1.21.11-loom.mappings.1_21_11.layered+hash.2198-v2/minecraft-merged-1.21.11-loom.mappings.1_21_11.layered+hash.2198-v2.jar`
(copied to /tmp/mc.jar for javap).

Baseline: 1712 compile errors (compileJava, -Xmx6G). Build log: /tmp/c.log

## Major API changes found in 1.21.11 vs 1.21.1
- **Entity render pipeline → RenderState.** `EntityRenderer<T,S extends EntityRenderState>`,
  `createRenderState()/extractRenderState()/render(S,...)`. `EntityModel<S extends EntityRenderState>`,
  `setupAnim(S)`. `LivingEntityRenderer<T,S,M>`. `RenderLayer<S,M>`. (~34 renderers, 48 models, 5 layers)
- **Particle engine rewritten.** `TextureSheetParticle` REMOVED → base is `SingleQuadParticle`.
  `render(VertexConsumer,Camera,float)` REMOVED → `extract(QuadParticleRenderState,Camera,float)` +
  `extractRotatedQuad(...)`. `getRenderType()` → `getGroup()`. `getLayer()` abstract.
  Fields rCol/gCol/bCol/alpha/quadSize/roll/oRoll/sprite on SingleQuadParticle. Custom Mowzie-derived
  engine (AdvancedParticleBase/ParticleRibbon/ParticleComponent) needs porting to extract-model.
- `Level.isClientSide` field private → `isClientSide()` method (~223 uses).
- `MobSpawnType` → `EntitySpawnReason`; `finalizeSpawn` drops trailing CompoundTag (now 4 args).
- `InteractionResultHolder` removed → `InteractionResult` (use()/getUseAnimation UseAnim etc).
- `FastColor` removed → `ARGB` (net.minecraft.util.ARGB).
- `Tier`/`SwordItem`/`ArmorItem` material changes.
- NBT: CompoundTag save/load → `ValueOutput`/`ValueInput` (addAdditionalSaveData/readAdditionalSaveData).
- Entity.getUUID/hasUUID, putUUID; spawnAtLocation(Item) signature; addCooldown(Item,int) signature.

## Order of attack
1. [ ] Mechanical mass fixes: isClientSide(), FastColor→ARGB, MobSpawnType→EntitySpawnReason.
2. [ ] NBT ValueInput/Output.
3. [ ] Item/entity API (Tier, InteractionResult, spawnAtLocation, cooldown, UUID).
4. [ ] Entity RenderState rewrite (base ModHierarchicalModel + renderers + layers).
5. [ ] Particle engine rewrite (SingleQuadParticle/extract).
6. [ ] Build clean, run in Prism 1.21.11 (instance exists), fix runtime.

## PRECISE API MAP (verified via javap on /tmp/mc.jar — the real mojmap-1.21.11 classpath)
NOTE: 1.21.9 reorg renamed/moved MANY classes. Reference jar:
`.../minecraft-merged/1.21.11-loom.mappings.1_21_11.layered+hash.2198-v2/...jar` (cp to /tmp/mc.jar).

### DONE (committed WIP)
- `.isClientSide` field → `.isClientSide()` method.
- `FastColor.ARGB32`/`FastColor.as8BitChannel` → `net.minecraft.util.ARGB` (same method names).
- `MobSpawnType` → `EntitySpawnReason` (finalizeSpawn already 4-arg, OK).
- `ResourceLocation` → `net.minecraft.resources.Identifier` (SAME method names: fromNamespaceAndPath/parse/tryParse/withDefaultNamespace).
- Package moves applied (see /tmp/moves.sed): RenderType→client.renderer.rendertype.RenderType;
  advancements.critereon→criterion; animal.IronGolem→animal.golem.*; animal.Sheep→animal.sheep.*;
  monster.{Zombie,ZombifiedPiglin}→monster.zombie.*; monster.{AbstractIllager,SpellcasterIllager}→monster.illager.*;
  monster.AbstractSkeleton→monster.skeleton.*; npc.{Villager,AbstractVillager,VillagerData}→npc.villager.*;
  projectile.{Arrow,AbstractArrow}→projectile.arrow.*; projectile.AbstractHurtingProjectile→projectile.hurtingprojectile.*;
  item.ArmorMaterial→item.equipment.ArmorMaterial; item.armortrim.*→item.equipment.trim.*; level.GameRules→level.gamerules.GameRules.
- NBT save/load param types: addAdditionalSaveData(ValueOutput), readAdditionalSaveData(ValueInput) (28 files) — BODIES NOT YET converted.
- EntityInit `.build(id.toString())` → `.build(ResourceKey.create(Registries.ENTITY_TYPE, id))`.

### STILL TODO — verified signatures for next steps
- **NBT bodies**: ValueOutput has putInt/putBoolean/putFloat/putDouble/putString/putLong (same). ValueInput:
  getInt(k)→getIntOr(k,def); getBoolean→getBooleanOr; getFloat→getFloatOr; getDouble→getDoubleOr;
  getString→getStringOr; getCompound(k)→childOrEmpty(k)/child(k):Optional; contains(k)→getX(k).isPresent().
  UUID: putUUID(k,u)→store(k,net.minecraft.core.UUIDUtil.CODEC,u); getUUID(k)→read(k,UUIDUtil.CODEC).orElse(..);
  hasUUID(k)→read(k,UUIDUtil.CODEC).isPresent().
- **Items** (SwordItem/AxeItem/DiggerItem/PickaxeItem/ArmorItem/Tier REMOVED):
  `Tier`→record `net.minecraft.world.item.ToolMaterial(TagKey<Block> incorrectForDrops, int durability,
  float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems)`.
  Item.Properties helpers: `.sword(ToolMaterial,dmg,speed)`, `.axe(...)`, `.pickaxe(...)`, `.tool(...)`,
  `.humanoidArmor(ArmorMaterial, ArmorType)`. Weapon subclasses must `extends Item` (behavior kept),
  construction passes .sword()/.axe(). `ArmorItem.Type`→`net.minecraft.world.item.equipment.ArmorType`.
  ModToolsTier: repairItems is TagKey<Item> now (need repair tags or reuse vanilla). AxeItem.createAttributes gone.
- **Entity render → RenderState** (EntityModel<S extends EntityRenderState>, setupAnim(S);
  EntityRenderer<T,S>: createRenderState()/extractRenderState(T,S,float)/render moved to submit(S,...);
  LivingEntityRenderer<T,S,M>; RenderLayer<S,M>). Model.renderToBuffer(pose,vc,light,overlay,color) still final.
  Plan: custom `EMRenderState extends LivingEntityRenderState { public <entity> entity; float partialTick; }`
  carrying the live entity so ModHierarchicalModel animation code (reads entity.getAnimation()) survives.
- **Particles**: TextureSheetParticle REMOVED → base `SingleQuadParticle`. `render(VertexConsumer,Camera,float)`
  REMOVED → override `extract(QuadParticleRenderState,Camera,float)` + extractRotatedQuad. getRenderType()→getGroup().
  abstract getLayer():SingleQuadParticle.Layer. Fields rCol/gCol/bCol/alpha/quadSize/roll/oRoll/sprite present.
  ParticleProvider.createParticle now takes trailing RandomSource param.
- **Registration**: EntityRendererRegistry.register + EntityType build use ResourceKey; check EntityModelLayer.
- ModelResourceLocation moved (2 files: ModItemModels, RegisterHandler); BEWLR/BuiltinItemRendererRegistry removed.
- appendHoverText(Item) signature changed; SpawnEggItem ctor changed; getUseAnimation→UseAnim; use()→InteractionResult.

## RENDER PIPELINE — TWO stacked changes (both in 1.21.2→1.21.9)
1. **RenderState extraction**: EntityModel<S extends EntityRenderState>, setupAnim(S);
   EntityRenderer<T,S> createRenderState()/extractRenderState(T,S,float); LivingEntityRenderer<T,S,M>;
   MobRenderer<T,S,M>; getTextureLocation(S); scale(S,PoseStack) (no partialTick); setupRotations(S,PoseStack,float,float).
2. **Submit-node pipeline**: immediate PoseStack+VertexConsumer rendering is GONE from layers/renderers.
   - RenderLayer<S,M>.render(...) REMOVED → `abstract void submit(PoseStack, SubmitNodeCollector, int light, S state, float yRot, float xRot)`.
   - EntityRenderer.render(...) REMOVED → `submit(S, PoseStack, SubmitNodeCollector, CameraRenderState)`.
   - Draw via SubmitNodeCollector (extends OrderedSubmitNodeCollector):
     `submitModel(Model<? super S>, S, PoseStack, RenderType, int light, int overlay, int color, CrumblingOverlay)` (CrumblingOverlay nullable);
     `submitModelPart(ModelPart, PoseStack, RenderType, light, overlay, sprite[, ...])`;
     `submitCustomGeometry(PoseStack, RenderType, CustomGeometryRenderer)` — lambda(pose, VertexConsumer) for fully custom (beams/effects).

## BRIDGE DESIGN (implemented)
- `com.eeeab.animate.client.render.EMRenderState extends LivingEntityRenderState` — carries `Entity entity` + `partialTick`.
- `ModHierarchicalModel<E>` now `extends EntityModel<EMRenderState>`; final setupAnim(EMRenderState) forwards to
  abstract setupAnim(E, walkAnimationPos, walkAnimationSpeed, ageInTicks, yRot, xRot). Constructor takes ModelPart→super.
- `EMMobRenderer<T extends Mob, M> extends MobRenderer<T,EMRenderState,M>` — createRenderState()/extractRenderState stamp entity+pt.

## PROGRESS (committed WIP, does NOT compile yet)
- [x] Mechanical renames/moves, NBT param types, entity registration (ResourceKey).
- [x] Item system → ToolMaterial/ArmorMaterial/ArmorType, spawn eggs, repair tags.
- [x] Model base + 22 ModHierarchicalModel subclasses migrated (super(root), removed final root()/renderToBuffer overrides).
- [x] EMRenderState + EMMobRenderer bridges; 16 mob renderers switched to EMMobRenderer (method sigs still TODO).
- [ ] Custom layers (LayerGlow/LayerOuter/LayerCrackiness/LayerVariantHolder) → RenderLayer<EMRenderState,M>.submit(...).
- [ ] Mob renderer method sigs: getTextureLocation(EMRenderState)/scale(EMRenderState,PoseStack); custom render()→submit or scale/setupRotations.
- [ ] Effect renderers (11, extend EntityRenderer) → EntityRenderer<T,EMRenderState> + submit()/submitCustomGeometry; RenderState per effect.
- [ ] Non-ModHierarchical models: SlidingDoorModel, AnimatedItemModel/item models (super(root), remove root()).
- [ ] Particle engine → SingleQuadParticle + extract(QuadParticleRenderState,...).
- [ ] NBT bodies (getIntOr etc.), appendHoverText(+TooltipDisplay,Consumer), hurtEnemy→void, use→InteractionResult,
      UseAnim→ItemUseAnimation, InteractionResultHolder→InteractionResult, addCooldown/spawnAtLocation sig, create(Level) factories.
- [ ] EntityRendererRegistry.register + model layer registration (ModModelLayer) signatures.
- [ ] Build clean → run Prism 1.21.11 → fix runtime → THEN backport 1.21.10…1.21.1.

## DEEPEST BLOCK — 1.21.9 RenderPipeline system (blocks ModRenderType + particles + effect renderers)
- `RenderStateShard` / `RenderType.CompositeState` DELETED. Custom render types now:
  `RenderType.create(String, RenderSetup)` where RenderSetup is built from a `com.mojang.blaze3d.pipeline.RenderPipeline`
  (see `net.minecraft.client.renderer.RenderPipelines` for vanilla pipelines: entityTranslucent, eyes, beacon beam, etc.).
- `ParticleRenderType` is now a bare record (SINGLE_QUADS/ITEM_PICKUP/ELDER_GUARDIANS/NO_RENDER) — the old
  interface with begin(Tesselator,TextureManager) is GONE. Custom particle "translucent no depth" type must map to a
  `SingleQuadParticle.Layer` (getLayer()) instead. Particles render via QuadParticleRenderState + extract(), not custom begin().
- ModRenderType custom types to re-author: getGlowingEffect (beacon-beam shader, translucent, no-cull),
  getGlowingCutOutEffect (emissive translucent), getGlowingTranslucentEffect, PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH.
  These need custom RenderPipeline objects (blend/shader/depth) — fiddly, wants in-game verification.

## PROGRESS SUMMARY (1712 → ~1100 compile errors; all committed in clean WIP checkpoints)
DONE reliably (mechanical/structural, low runtime risk):
- Renames/moves: ResourceLocation→Identifier, RenderType→rendertype.RenderType, FastColor→ARGB, MobSpawnType→EntitySpawnReason,
  advancements.critereon→criterion, entity/item package reshuffles, GameRules, ArmorMaterial/trim.
- NBT: save/load params → ValueOutput/ValueInput; read bodies → getXOr(...); UUID → store/read(UUIDUtil.CODEC).
- Items: Tier→ToolMaterial, ArmorItem→Item.humanoidArmor(ArmorMaterial,ArmorType), SwordItem/AxeItem→Item+.sword()/.axe(),
  repair item-tags, SpawnEggItem(Properties.spawnEgg), InteractionResultHolder→InteractionResult, UseAnim→ItemUseAnimation,
  hurtEnemy→void, appendHoverText(+TooltipDisplay,Consumer).
- Entities: EntityType.build(ResourceKey), finalizeSpawn(EntitySpawnReason), isClientSide().
- Render (RenderState half): ModHierarchicalModel + 22 models migrated; EMRenderState/EMMobRenderer bridges;
  16 mob renderers → EMMobRenderer with getTextureLocation(EMRenderState)/scale(EMRenderState,PoseStack);
  all 5 custom layers (Glow/Breath/Outer/Crackiness/VariantHolder) → RenderLayer<EMRenderState,M>.submit(SubmitNodeCollector);
  IVariantHolder (vanilla VariantHolder removed).

REMAINING (bespoke rendering, higher runtime risk — recommend doing with the game runnable to verify):
- ModRenderType → RenderPipelines/RenderSetup rewrite (linchpin for below).
- Particle engine: AdvancedParticleBase/ParticleRibbon/all Particle* → SingleQuadParticle + extract(QuadParticleRenderState) + getLayer().
- 6 custom mob renderers (RenderImmortal/RealmWarden/RelicRipper/RelicAnnihilator/RelicEarthshaker/ImmortalExecutioner)
  reimplement render() with alpha/glow → move to extractRenderState(store alpha on EMRenderState) + submit() with custom render types.
- 11 effect renderers (EntityRenderer-based: beams/projectiles/blade/telegraph/falling block) → EntityRenderer<T,EMRenderState>
  + createRenderState + submit()/submitCustomGeometry; per-effect state fields.
- Non-ModHierarchical models: SlidingDoorModel + item models (AnimatedItemModel etc.) super(root)/remove root().
- EntityRendererRegistry.register + ModModelLayer registration signature; BEWLR/BuiltinItemRendererRegistry replacements.
- Misc: create(Level/ServerLevel) entity factories, addCooldown(Item,int)→ItemCooldowns new sig, spawnAtLocation(Item) sig,
  ModelResourceLocation moved (ModItemModels/RegisterHandler), remaining `does not override` in a few files.
- THEN: build clean → run Prism 1.21.11 (instance `1.21.11(1)(1)` exists) → fix runtime (refmap/mixins/registration) →
  THEN backport 1.21.10 … 1.21.1 (each a smaller delta down from the working 1.21.11 tree).

## Status: bulk migration done (1712→~1100). Remainder = bespoke RenderPipeline/particle/effect-renderer work best done with the game runnable.
</content>
</invoke>
