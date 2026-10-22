package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.*;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.*;
import com.eeeab.eeeabsmobs.server.entity.mob.EntityTester;
import com.eeeab.eeeabsmobs.server.entity.mob.corpse.EntityCorpse;
import com.eeeab.eeeabsmobs.server.entity.mob.corpse.EntityCorpseToPlayer;
import com.eeeab.eeeabsmobs.server.entity.mob.corpse.EntityCorpseVillager;
import com.eeeab.eeeabsmobs.server.entity.mob.corpse.EntityCorpseWarlock;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.*;
import com.eeeab.eeeabsmobs.server.entity.mob.relicron.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;

// @Mod.EventBusSubscriber (class-level auto-registration of @SubscribeEvent statics) was removed entirely;
// register(IEventBus) - already called from EEEABMobs's constructor - now also wires up
// setEntityAttributeCreationEvent by hand instead of relying on annotation scanning.
public class EntityInit {
    public static final FabricDeferredRegister<EntityType<?>> ENTITIES = FabricDeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, EEEABMobs.MOD_ID);

// in no released jar -- unfinished work whose registration was committed but whose classes
// were never published, which is why upstream master does not compile as-is. Removed here.

    public static final FabricDeferredRegister.Entry<EntityType<EntityCorpse>> CORPSE =
            ENTITIES.register("corpse",
                    () -> EntityType.Builder.<EntityCorpse>of(EntityCorpse::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f).clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "corpse"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityCorpseVillager>> CORPSE_VILLAGER =
            ENTITIES.register("corpse_villager",
                    () -> EntityType.Builder.<EntityCorpseVillager>of(EntityCorpseVillager::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f).clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "corpse_villager"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityCorpseWarlock>> CORPSE_WARLOCK =
            ENTITIES.register("corpse_warlock",
                    () -> EntityType.Builder.<EntityCorpseWarlock>of(EntityCorpseWarlock::new, MobCategory.MONSTER)
                            .sized(0.7f, 2.2f).fireImmune().clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "corpse_warlock"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityCorpseToPlayer>> CORPSE_TO_PLAYER =
            ENTITIES.register("corpse_to_player",
                    () -> EntityType.Builder.<EntityCorpseToPlayer>of(EntityCorpseToPlayer::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.95f).clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "corpse_to_player"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalSkeleton>> IMMORTAL_SKELETON = registerImmortalSkeletonEntity(EntityImmortalSkeleton::new, "immortal_skeleton");
    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalSkeletonArcher>> IMMORTAL_ARCHER = registerImmortalSkeletonEntity(EntityImmortalSkeletonArcher::new, "immortal_archer");
    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalSkeletonKnight>> IMMORTAL_KNIGHT = registerImmortalSkeletonEntity(EntityImmortalSkeletonKnight::new, "immortal_knight");
    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalSkeletonMage>> IMMORTAL_MAGE = registerImmortalSkeletonEntity(EntityImmortalSkeletonMage::new, "immortal_mage");
    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalSkeletonWarrior>> IMMORTAL_WARRIOR = registerImmortalSkeletonEntity(EntityImmortalSkeletonWarrior::new, "immortal_warrior");

    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalShaman>> IMMORTAL_SHAMAN =
            ENTITIES.register("immortal_shaman",
                    () -> EntityType.Builder.<EntityImmortalShaman>of(EntityImmortalShaman::new, MobCategory.MONSTER)
                            .sized(0.6f, 2.6f).clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "immortal_shaman"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityMagicGolem>> MAGIC_GOLEM =
            ENTITIES.register("immortal_golem",
                    () -> EntityType.Builder.<EntityMagicGolem>of(EntityMagicGolem::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.2f).clientTrackingRange(6)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "immortal_golem"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalExecutioner>> IMMORTAL_EXECUTIONER =
            ENTITIES.register("immortal_executioner",
                    () -> EntityType.Builder.<EntityImmortalExecutioner>of(EntityImmortalExecutioner::new, MobCategory.MONSTER)
                            .sized(0.8f, 2.45f).fireImmune().clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "immortal_executioner"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalBoss>> IMMORTAL_BOSS =
            ENTITIES.register("immortal",
                    () -> EntityType.Builder.<EntityImmortalBoss>of(EntityImmortalBoss::new, MobCategory.MONSTER)
                            .sized(2.8f, 4.45f).fireImmune().clientTrackingRange(10)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "immortal"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityRelicObserver>> RELIC_OBSERVER =
            ENTITIES.register("relic_observer",
                    () -> EntityType.Builder.<EntityRelicObserver>of(EntityRelicObserver::new, MobCategory.MONSTER)
                            .sized(1f, 1.8f).clientTrackingRange(6)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "relic_observer"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityRelicRipper>> RELIC_RIPPER =
            ENTITIES.register("relic_ripper",
                    () -> EntityType.Builder.<EntityRelicRipper>of(EntityRelicRipper::new, MobCategory.MONSTER)
                            .sized(1.45f, 2.4995f).clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "relic_ripper"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityRelicEarthshaker>> RELIC_EARTHSHAKER =
            ENTITIES.register("relic_earthshaker",
                    () -> EntityType.Builder.<EntityRelicEarthshaker>of(EntityRelicEarthshaker::new, MobCategory.MONSTER)
                            .sized(1.95f, 3.45f).clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "relic_earthshaker"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityRelicAnnihilator>> RELIC_ANNIHILATOR =
            ENTITIES.register("relic_annihilator",
                    () -> EntityType.Builder.<EntityRelicAnnihilator>of(EntityRelicAnnihilator::new, MobCategory.MONSTER)
                            .sized(1.3f, 3.45f).fireImmune().clientTrackingRange(8)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "relic_annihilator"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityRealmWarden>> REALM_WARDEN =
            ENTITIES.register("realm_warden",
                    () -> EntityType.Builder.<EntityRealmWarden>of(EntityRealmWarden::new, MobCategory.MONSTER)
                            .sized(2.45f, 4.45f).fireImmune().clientTrackingRange(10)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "realm_warden"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityNamelessGuardian>> NAMELESS_GUARDIAN =
            ENTITIES.register("nameless_guardian",
                    () -> EntityType.Builder.<EntityNamelessGuardian>of(EntityNamelessGuardian::new, MobCategory.MONSTER)
                            .sized(2.245f, 3.74f).fireImmune().clientTrackingRange(10)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "nameless_guardian"))));

    public static final FabricDeferredRegister.Entry<EntityType<EntityTester>> TESTER =
            ENTITIES.register("tester",
                    () -> EntityType.Builder.<EntityTester>of(EntityTester::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.95f).clientTrackingRange(10)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "tester"))));

    //非生物实体
    public static final FabricDeferredRegister.Entry<EntityType<EntityTelegraph>> TELEGRAPH =
            ENTITIES.register("telegraph",
                    () -> EntityType.Builder.<EntityTelegraph>of(EntityTelegraph::new, MobCategory.MISC).sized(1F, 1F)
                            .clientTrackingRange(64).updateInterval(1)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "telegraph"))));

    //爆炸
    public static final FabricDeferredRegister.Entry<EntityType<EntityExplode>> EXPLODE =
            ENTITIES.register("explode",
                    () -> EntityType.Builder.<EntityExplode>of(EntityExplode::new, MobCategory.MISC).sized(0F, 0F)
                            .updateInterval(Integer.MAX_VALUE).build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "explode"))));

    //过载爆炸
    public static final FabricDeferredRegister.Entry<EntityType<EntityOverloadExplode>> OVERLOAD_EXPLODE =
            ENTITIES.register("overload_explode",
                    () -> EntityType.Builder.<EntityOverloadExplode>of(EntityOverloadExplode::new, MobCategory.MISC).sized(0f, 0f)
                            .updateInterval(Integer.MAX_VALUE).build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "overload_explode"))));

    //遣雷战斧(投掷物)
    public static final FabricDeferredRegister.Entry<EntityType<EntityThrownDoomboltAxe>> DOOMBOLT_AXE =
            ENTITIES.register("doombolt_axe",
                    () -> EntityType.Builder.<EntityThrownDoomboltAxe>of(EntityThrownDoomboltAxe::new, MobCategory.MISC).sized(0.5F, 0.5F)
                            .clientTrackingRange(4).updateInterval(20)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "doombolt_axe"))));

    //巫师炸弹
    public static final FabricDeferredRegister.Entry<EntityType<EntityShamanBomb>> SHAMAN_BOMB =
            ENTITIES.register("shaman_bomb",
                    () -> EntityType.Builder.<EntityShamanBomb>of(EntityShamanBomb::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f).clientTrackingRange(4).updateInterval(10)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "shaman_bomb"))));

    //相机摇晃
    public static final FabricDeferredRegister.Entry<EntityType<EntityCameraShake>> CAMERA_SHAKE =
            ENTITIES.register("camera_shake",
                    () -> EntityType.Builder.<EntityCameraShake>of(EntityCameraShake::new, MobCategory.MISC).sized(1.0f, 1.0f)
                            .updateInterval(Integer.MAX_VALUE).build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "camera_shake"))));

    //下落的方块
    public static final FabricDeferredRegister.Entry<EntityType<EntityFallingBlock>> FALLING_BLOCK =
            ENTITIES.register("falling_block",
                    () -> EntityType.Builder.<EntityFallingBlock>of(EntityFallingBlock::new, MobCategory.MISC)
                            .sized(0.99f, 0.99f)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "falling_block"))));

    //守卫者激光
    public static final FabricDeferredRegister.Entry<EntityType<EntityInfraredRay>> INFRARED_RAY = registerCommonBeamEntity(EntityInfraredRay::new, "infrared_ray");
    public static final FabricDeferredRegister.Entry<EntityType<EntityGuardianLaser>> GUARDIAN_LASER = registerCommonBeamEntity(EntityGuardianLaser::new, "guardian_laser");

    //守卫者之刃
    public static final FabricDeferredRegister.Entry<EntityType<EntityGuardianBlade>> GUARDIAN_BLADE =
            ENTITIES.register("guardian_blade",
                    () -> EntityType.Builder.<EntityGuardianBlade>of(EntityGuardianBlade::new, MobCategory.MISC)
                            .sized(0.3f, 3.5f).updateInterval(1)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "guardian_blade"))));

    //结构之眼
    public static final FabricDeferredRegister.Entry<EntityType<EntityEyeOfStructure>> EYE_OF_STRUCTURE =
            ENTITIES.register("eye_of_structure",
                    () -> EntityType.Builder.<EntityEyeOfStructure>of(EntityEyeOfStructure::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(4)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "eye_of_structure"))));

    //毒箭
    public static final FabricDeferredRegister.Entry<EntityType<EntityPoisonArrow>> POISON_ARROW =
            ENTITIES.register("poison_arrow",
                    () -> EntityType.Builder.<EntityPoisonArrow>of(EntityPoisonArrow::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "poison_arrow"))));

    //血球
    public static final FabricDeferredRegister.Entry<EntityType<EntityBloodBall>> BLOOD_BALL =
            ENTITIES.register("blood_ball",
                    () -> EntityType.Builder.<EntityBloodBall>of(EntityBloodBall::new, MobCategory.MISC)
                            .sized(1F, 1F).clientTrackingRange(4).updateInterval(10)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "blood_ball"))));

    //腥红裂缝
    public static final FabricDeferredRegister.Entry<EntityType<EntityCrimsonCrack>> CRIMSON_CRACK =
            ENTITIES.register("crimson_crack",
                    () -> EntityType.Builder.<EntityCrimsonCrack>of(EntityCrimsonCrack::new, MobCategory.MISC)
                            .sized(0.1f, 0.1f)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "crimson_crack"))));

    //腥红射线
    public static final FabricDeferredRegister.Entry<EntityType<EntityCrimsonRay>> CRIMSON_RAY = registerCommonBeamEntity(EntityCrimsonRay::new, "crimson_ray");
    public static final FabricDeferredRegister.Entry<EntityType<EntityCrimsonRay.PreAttack>> CRIMSON_RAY_PRE =
            ENTITIES.register("crimson_ray_pre",
                    () -> EntityType.Builder.<EntityCrimsonRay.PreAttack>of(EntityCrimsonRay.PreAttack::new, MobCategory.MISC)
                            .sized(0.1f, 0.1f)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "crimson_ray_pre"))));

    //脉冲榴弹
    public static final FabricDeferredRegister.Entry<EntityType<EntityPulsedGrenade>> PULSED_GRENADE =
            ENTITIES.register("pulsed_grenade",
                    () -> EntityType.Builder.<EntityPulsedGrenade>of(EntityPulsedGrenade::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "pulsed_grenade"))));

    //电磁脉冲
    public static final FabricDeferredRegister.Entry<EntityType<EntityElectromagnetic>> ELECTROMAGNETIC =
            ENTITIES.register("electromagnetic",
                    () -> EntityType.Builder.<EntityElectromagnetic>of(EntityElectromagnetic::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "electromagnetic"))));

    //歼击者导弹
    public static final FabricDeferredRegister.Entry<EntityType<EntityAnnihilatorMissile>> ANNIHILATOR_MISSILE =
            ENTITIES.register("annihilator_missile",
                    () -> EntityType.Builder.<EntityAnnihilatorMissile>of(EntityAnnihilatorMissile::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "annihilator_missile"))));

    //电涌
    public static final FabricDeferredRegister.Entry<EntityType<EntitySurge>> SURGE =
            ENTITIES.register("surge",
                    () -> EntityType.Builder.<EntitySurge>of(EntitySurge::new, MobCategory.MISC)
                            .sized(0.5F, 4F).updateInterval(Integer.MAX_VALUE)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "surge"))));

    //异界传送门
    //public static final FabricDeferredRegister.Entry<EntityType<EntityAlienPortal>> ALIEN_PORTAL =
    //        ENTITIES.register("alien_portal",
    //                () -> EntityType.Builder.<EntityAlienPortal>of(EntityAlienPortal::new, MobCategory.MISC)
    //                        .sized(0.1F, 0.1F)/*.updateInterval(1)*/
    //                        .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "alien_portal"))));

    //不朽法阵
    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalMagicCircle>> MAGIC_CIRCLE =
            ENTITIES.register("immortal_magic_circle",
                    () -> EntityType.Builder.<EntityImmortalMagicCircle>of(EntityImmortalMagicCircle::new, MobCategory.MISC)
                            .sized(0.1F, 0.1F).updateInterval(2)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "immortal_magic_circle"))));

    //不朽手里剑
    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalShuriken>> IMMORTAL_SHURIKEN =
            ENTITIES.register("immortal_shuriken",
                    () -> EntityType.Builder.<EntityImmortalShuriken>of(EntityImmortalShuriken::new, MobCategory.MISC)
                            .sized(0.7125F, 0.5F).clientTrackingRange(8).updateInterval(1)
                            .build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "immortal_shuriken"))));

    //不朽激光
    public static final FabricDeferredRegister.Entry<EntityType<EntityImmortalLaser>> IMMORTAL_LASER = registerCommonBeamEntity(EntityImmortalLaser::new, "immortal_laser");

    public static void register(Object bus) {
        ENTITIES.register(bus);
        registerAttributes();
    }

    private static <T extends EntityAbsBeam> FabricDeferredRegister.Entry<EntityType<T>> registerCommonBeamEntity(EntityType.EntityFactory<T> entityFactory, String name) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(entityFactory, MobCategory.MISC).sized(0.1F, 0.1F).updateInterval(1).build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, name))));
    }

    private static <T extends EntityAbsImmortalSkeleton> FabricDeferredRegister.Entry<EntityType<T>> registerImmortalSkeletonEntity(EntityType.EntityFactory<T> entityFactory, String key) {
        return ENTITIES.register(key, () -> EntityType.Builder.of(entityFactory, MobCategory.MONSTER).sized(0.6f, 2.2f).clientTrackingRange(8).build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, key))));
    }

    // Fabric has no EntityAttributeCreationEvent (a mod-bus NeoForge event); FabricDefaultAttributeRegistry.register
    // is called directly instead, per-entity, right after the entity types are registered (verified with javap
    // against fabric-object-builder-api-v1-11.1.5).
    private static void registerAttributes() {
        EntityAttributeRegistry.register(EntityInit.TESTER, EntityTester::setAttributes);
        EntityAttributeRegistry.register(EntityInit.CORPSE, EntityCorpse::setAttributes);
        EntityAttributeRegistry.register(EntityInit.CORPSE_VILLAGER, EntityCorpse::setAttributes);
        EntityAttributeRegistry.register(EntityInit.CORPSE_WARLOCK, EntityCorpseWarlock::setAttributes);
        EntityAttributeRegistry.register(EntityInit.CORPSE_TO_PLAYER, EntityCorpseToPlayer::setAttributes);
        EntityAttributeRegistry.register(EntityInit.RELIC_OBSERVER, EntityRelicObserver::setAttributes);
        EntityAttributeRegistry.register(EntityInit.RELIC_EARTHSHAKER, EntityRelicEarthshaker::setAttributes);
        EntityAttributeRegistry.register(EntityInit.RELIC_RIPPER, EntityRelicRipper::setAttributes);
        EntityAttributeRegistry.register(EntityInit.RELIC_ANNIHILATOR, EntityRelicAnnihilator::setAttributes);
        EntityAttributeRegistry.register(EntityInit.REALM_WARDEN, EntityRealmWarden::setAttributes);
        EntityAttributeRegistry.register(EntityInit.NAMELESS_GUARDIAN, EntityNamelessGuardian::setAttributes);
        EntityAttributeRegistry.register(EntityInit.IMMORTAL_SKELETON, () -> EntityAbsImmortalSkeleton.createSkeletonAttributes(5F, 4F, 0F, 0.01F));
        EntityAttributeRegistry.register(EntityInit.IMMORTAL_MAGE, () -> EntityAbsImmortalSkeleton.createSkeletonAttributes(5F, 0F, 2F, 0.03F));
        EntityAttributeRegistry.register(EntityInit.IMMORTAL_ARCHER, () -> EntityAbsImmortalSkeleton.createSkeletonAttributes(10F, 4F, 4F, 0.025F));
        EntityAttributeRegistry.register(EntityInit.IMMORTAL_WARRIOR, () -> EntityAbsImmortalSkeleton.createSkeletonAttributes(15F, 5F, 6F, 0.02F));
        EntityAttributeRegistry.register(EntityInit.IMMORTAL_KNIGHT, () -> EntityAbsImmortalSkeleton.createSkeletonAttributes(25F, 6F, 8F, 0F));
        EntityAttributeRegistry.register(EntityInit.IMMORTAL_SHAMAN, EntityImmortalShaman::setAttributes);
        EntityAttributeRegistry.register(EntityInit.MAGIC_GOLEM, EntityMagicGolem::setAttributes);
        EntityAttributeRegistry.register(EntityInit.IMMORTAL_EXECUTIONER, EntityImmortalExecutioner::setAttributes);
        EntityAttributeRegistry.register(EntityInit.IMMORTAL_BOSS, EntityImmortalBoss::setAttributes);
    }
}
