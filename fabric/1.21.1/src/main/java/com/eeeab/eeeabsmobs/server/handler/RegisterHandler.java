package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.armor.ModelGhostWarriorArmor;
import com.eeeab.eeeabsmobs.client.model.block.SlidingDoorModel;
import com.eeeab.eeeabsmobs.client.model.entity.*;
import com.eeeab.eeeabsmobs.client.model.entity.effect.*;
import com.eeeab.eeeabsmobs.client.model.item.ModelBusterGauntlet;
import com.eeeab.eeeabsmobs.client.model.item.ModelGuardianBattleaxe;
import com.eeeab.eeeabsmobs.client.model.item.ModelTheNetherworldKatana;
import com.eeeab.eeeabsmobs.client.model.util.ModItemModels;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.particle.*;
import com.eeeab.eeeabsmobs.client.particle.lib.AdvancedBlockParticle;
import com.eeeab.eeeabsmobs.client.particle.lib.AdvancedParticleBase;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRibbon;
import com.eeeab.eeeabsmobs.client.render.EmptyRender;
import com.eeeab.eeeabsmobs.client.render.block.RenderSlidingDoor;
import com.eeeab.eeeabsmobs.client.render.entity.*;
import com.eeeab.eeeabsmobs.client.render.entity.effect.*;
import com.eeeab.eeeabsmobs.server.init.BlockEntityInit;
import com.eeeab.eeeabsmobs.server.init.EntityInit;
import com.eeeab.eeeabsmobs.server.init.ParticleInit;
import com.eeeab.eeeabsmobs.server.integration.curios.model.ModelSoulSummoningNecklace;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/**
 * Fabric has no mod event bus, so there is nothing to {@code @SubscribeEvent}/register on a bus for. Every
 * NeoForge {@code EntityRenderersEvent}/{@code RegisterParticleProvidersEvent}/{@code ModelEvent} used here has
 * a direct, eager Fabric API counterpart (all in fabric-rendering-v1 / fabric-particles-v1 / fabric-model-loading-
 * api-v1, verified with {@code javap -p} against the nested jars inside fabric-api-0.116.15+1.21.1.jar) that is
 * called straight from client init instead of from an event callback. This class is therefore no longer a
 * handler that gets registered - it is called directly (see {@link #registerAll()}) from the client entrypoint
 * (ClientProxy, out of this port's scope) during {@code onInitializeClient}.
 */
@Environment(EnvType.CLIENT)
public class RegisterHandler {

    public static void registerAll() {
        registerLayer();
        registerRenderers();
        registerParticleProviders();
        registerModels();
    }

    public static void registerLayer() {
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.UNKNOWN, ModelUnKnown::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.TESTER, ModelTester::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.CORPSE, ModelCorpse::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.CORPSE_VILLAGER, ModelCorpse::createVillagerBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.CORPSE_SLAVERY, ModelCorpseWarlock::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.BLOOD_BALL, ModelBloodBall::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.RELIC_OBSERVER, ModelRelicObserver::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.RELIC_RIPPER, ModelRelicRipper::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.RELIC_EARTHSHAKER, ModelRelicEarthshaker::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.PULSED_GRENADE, ModelPulsedGrenade::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.RELIC_ANNIHILATOR, ModelRelicAnnihilator::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.REALM_WARDEN, ModelRealmWarden::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.NAMELESS_GUARDIAN, ModelNamelessGuardian::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.GUARDIAN_BLADE, ModelGuardianBlade::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.MAGIC_GOLEM, ModelMagicGolem::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.IMMORTAL_SKELETON, ModelAbsImmortalSkeleton::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.IMMORTAL_SHAMAN, ModelImmortalShaman::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.IMMORTAL_EXECUTIONER, ModelImmortalExecutioner::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.IMMORTAL, ModelImmortal::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.IMMORTAL_SHURIKEN, ModelImmortalShuriken::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.ANNIHILATOR_MISSILE, ModelAnnihilatorMissile::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.DOOMBOLT_AXE, ModelDoomboltAxe::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.GHOST_WARRIOR_ARMOR, () -> ModelGhostWarriorArmor.createBodyLayer(new CubeDeformation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.GHOST_WARRIOR_ARMOR_LEGS, () -> ModelGhostWarriorArmor.createBodyLayer(new CubeDeformation(0.2F)));
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.SOUL_SUMMONING_NECKLACE, ModelSoulSummoningNecklace::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.THE_NETHERWORLD_KATANA, ModelTheNetherworldKatana::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.THE_GUARDIAN_BATTLEAXE, ModelGuardianBattleaxe::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.BUSTER_GAUNTLET, ModelBusterGauntlet::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayer.SLIDING_DOOR, SlidingDoorModel::createBodyLayer);
    }

    public static void registerRenderers() {
        EntityRendererRegistry.register(EntityInit.TESTER.get(), RenderTester::new);
        EntityRendererRegistry.register(EntityInit.CORPSE.get(), RenderCorpse::new);
        EntityRendererRegistry.register(EntityInit.CORPSE_VILLAGER.get(), RenderCorpseVillager::new);
        EntityRendererRegistry.register(EntityInit.CORPSE_WARLOCK.get(), RenderCorpseWarlock::new);
        EntityRendererRegistry.register(EntityInit.CORPSE_TO_PLAYER.get(), RenderCorpse::new);
        EntityRendererRegistry.register(EntityInit.RELIC_OBSERVER.get(), RenderRelicObserver::new);
        EntityRendererRegistry.register(EntityInit.RELIC_RIPPER.get(), RenderRelicRipper::new);
        EntityRendererRegistry.register(EntityInit.RELIC_EARTHSHAKER.get(), RenderRelicEarthshaker::new);
        EntityRendererRegistry.register(EntityInit.RELIC_ANNIHILATOR.get(), RenderRelicAnnihilator::new);
        EntityRendererRegistry.register(EntityInit.REALM_WARDEN.get(), RenderRealmWarden::new);
        EntityRendererRegistry.register(EntityInit.NAMELESS_GUARDIAN.get(), RenderNamelessGuardian::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_SKELETON.get(), RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_ARCHER.get(), RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_KNIGHT.get(), RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_MAGE.get(), RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_WARRIOR.get(), RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_SHAMAN.get(), RenderImmortalShaman::new);
        EntityRendererRegistry.register(EntityInit.MAGIC_GOLEM.get(), RenderMagicGolem::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_EXECUTIONER.get(), RenderImmortalExecutioner::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_BOSS.get(), RenderImmortal::new);

        EntityRendererRegistry.register(EntityInit.TELEGRAPH.get(), RendererTelegraph::new);
        EntityRendererRegistry.register(EntityInit.GUARDIAN_BLADE.get(), RenderGuardianBlade::new);
        EntityRendererRegistry.register(EntityInit.SHAMAN_BOMB.get(), RenderShamanBomb::new);
        EntityRendererRegistry.register(EntityInit.CAMERA_SHAKE.get(), EmptyRender::new);
        EntityRendererRegistry.register(EntityInit.EXPLODE.get(), EmptyRender::new);
        EntityRendererRegistry.register(EntityInit.OVERLOAD_EXPLODE.get(), EmptyRender::new);
        EntityRendererRegistry.register(EntityInit.FALLING_BLOCK.get(), RenderFallingBlock::new);
        EntityRendererRegistry.register(EntityInit.DOOMBOLT_AXE.get(), RenderDoomboltAxe::new);
        EntityRendererRegistry.register(EntityInit.INFRARED_RAY.get(), RenderInfraredRay::new);
        EntityRendererRegistry.register(EntityInit.GUARDIAN_LASER.get(), RenderGuardianLaser::new);
        EntityRendererRegistry.register(EntityInit.EYE_OF_STRUCTURE.get(), (context) -> new ThrownItemRenderer<>(context, 1.5F, true));
        EntityRendererRegistry.register(EntityInit.POISON_ARROW.get(), RenderPoisonArrow::new);
        EntityRendererRegistry.register(EntityInit.BLOOD_BALL.get(), RendererBloodBall::new);
        EntityRendererRegistry.register(EntityInit.CRIMSON_CRACK.get(), RenderCrimsonCrack::new);
        EntityRendererRegistry.register(EntityInit.CRIMSON_RAY.get(), RenderCrimsonRay::new);
        EntityRendererRegistry.register(EntityInit.CRIMSON_RAY_PRE.get(), RenderCrimsonRay.RenderPreAttack::new);
        EntityRendererRegistry.register(EntityInit.PULSED_GRENADE.get(), RenderPulsedGrenade::new);
        EntityRendererRegistry.register(EntityInit.ELECTROMAGNETIC.get(), EmptyRender::new);
        EntityRendererRegistry.register(EntityInit.ANNIHILATOR_MISSILE.get(), RenderAnnihilatorMissile::new);
        EntityRendererRegistry.register(EntityInit.SURGE.get(), EmptyRender::new);
        //EntityRendererRegistry.register(EntityInit.ALIEN_PORTAL.get(), RenderAlienPortal::new);
        EntityRendererRegistry.register(EntityInit.MAGIC_CIRCLE.get(), RenderImmortalMagicCircle::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_SHURIKEN.get(), RenderImmortalShuriken::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_LASER.get(), RenderImmortalLaser::new);

        BlockEntityRendererRegistry.register(BlockEntityInit.SLIDING_DOOR_BE.get(), RenderSlidingDoor::new);
    }

    // NeoForge's RegisterParticleProvidersEvent covered both "sprite set" particles (registerSpriteSet) and
    // "special" (spriteless, self-driving) particles (registerSpecial) with two different methods.
    // ParticleFactoryRegistry unifies both under one register(ParticleType, ParticleFactory) overload - a
    // ParticleProvider's create(...) is handed a SpriteSet regardless of whether the factory actually samples
    // it, so the former "special" particles (the two explosion seed factories) register through the exact same
    // call as everything else.
    public static void registerParticleProviders() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(ParticleInit.SPELL_CASTING.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.ADV_ORB.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.GLOW.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.ADV_RING.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.ADV_RING2.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.ADV_RING3.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.CRIMSON.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.CRIMSON_EYE.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.STRIP_SOUL_FIRE.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.CROSS_FLASH.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.VORTEX.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.CRIT_RING.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.THUMP_RING.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.PUNCTURED_AIR_RING.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.SURGE.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.SPARK.get(), AdvancedParticleBase.Factory::new);
        registry.register(ParticleInit.FLAT_RIBBON.get(), ParticleRibbon.Factory::new);
        registry.register(ParticleInit.BLOCK.get(), AdvancedBlockParticle.Factory::new);
        registry.register(ParticleInit.DUST.get(), ParticleDust.Factory::new);
        registry.register(ParticleInit.ORB.get(), ParticleOrb.Factory::new);
        registry.register(ParticleInit.GUARDIAN_SPARK.get(), ParticleGuardianSpark.Factory::new);
        registry.register(ParticleInit.HIT_CUT.get(), ParticleHitCut.Factory::new);
        registry.register(ParticleInit.POISON.get(), ParticlePoison.Factory::new);
        registry.register(ParticleInit.WARLOCK_HEAL.get(), FlameParticle.Provider::new);
        registry.register(ParticleInit.IMMORTAL_EXPLOSION.get(), ParticleImmortalExplosion.Factory::new);
        registry.register(ParticleInit.BLAZE_EXPLOSION.get(), ParticleLastingExplosion.Factory::new);
        registry.register(ParticleInit.VOLT_EXPLOSION.get(), ParticleLastingExplosion.Factory::new);
        registry.register(ParticleInit.RING.get(), ParticleRing.Factory::new);
        registry.register(ParticleInit.BIG_RING.get(), ParticleRing.Factory::new);
        registry.register(ParticleInit.RADIAL_OPACITY_RING.get(), ParticleRing.Factory::new);
        registry.register(ParticleInit.BLAZE_EXPLOSION_EMITTER.get(), new ParticleBlazeExplosionSeed.Factory());
        registry.register(ParticleInit.VOLT_EXPLOSION_EMITTER.get(), new ParticleVoltExplosionSeed.Factory());
    }

    // ModelEvent.RegisterAdditional took a bare ResourceLocation and wrapped it in ModelResourceLocation.standalone
    // itself; ModelLoadingPlugin.Context#addModels also takes a bare ResourceLocation for the same purpose (load
    // this model outside of any blockstate/item-model JSON reference), so the wrap-in-standalone-MRL step that
    // used to be explicit here is now implicit in Fabric's model loader, not dropped.
    public static void registerModels() {
        ModelLoadingPlugin.register(context -> {
            ResourceLocation[] ids = java.util.Arrays.stream(ModItemModels.HAND_MODEL_ITEMS)
                    .map(item -> ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "item/" + item + "_in_hand"))
                    .toArray(ResourceLocation[]::new);
            context.addModels(ids);
        });
    }
}
