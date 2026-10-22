package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.client.model.armor.ModelGhostWarriorArmor;
import com.eeeab.eeeabsmobs.client.model.block.SlidingDoorModel;
import com.eeeab.eeeabsmobs.client.model.entity.*;
import com.eeeab.eeeabsmobs.client.model.entity.effect.*;
import com.eeeab.eeeabsmobs.client.model.item.ModelBusterGauntlet;
import com.eeeab.eeeabsmobs.client.model.item.ModelGuardianBattleaxe;
import com.eeeab.eeeabsmobs.client.model.item.ModelTheNetherworldKatana;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.particle.*;
import com.eeeab.eeeabsmobs.client.particle.lib.AdvancedBlockParticle;
import com.eeeab.eeeabsmobs.client.particle.lib.AdvancedParticleBase;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRibbon;
import com.eeeab.eeeabsmobs.client.render.EmptyRender;
import com.eeeab.eeeabsmobs.client.render.block.RenderSlidingDoor;
import com.eeeab.eeeabsmobs.client.render.entity.*;
import com.eeeab.eeeabsmobs.client.render.entity.effect.*;
import com.eeeab.eeeabsmobs.platform.PlatformServices;
import com.eeeab.eeeabsmobs.server.init.BlockEntityInit;
import com.eeeab.eeeabsmobs.server.init.EntityInit;
import com.eeeab.eeeabsmobs.server.init.ParticleInit;
import com.eeeab.eeeabsmobs.server.integration.curios.model.ModelSoulSummoningNecklace;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class RegisterHandler {

    public static void registerAll() {
        registerLayer();
        registerRenderers();
        registerParticleProviders();
        registerModels();
    }

    public static void registerLayer() {
        EntityModelLayerRegistry.register(ModModelLayer.UNKNOWN, ModelUnKnown::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.TESTER, ModelTester::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.CORPSE, ModelCorpse::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.CORPSE_VILLAGER, ModelCorpse::createVillagerBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.CORPSE_SLAVERY, ModelCorpseWarlock::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.BLOOD_BALL, ModelBloodBall::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.RELIC_OBSERVER, ModelRelicObserver::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.RELIC_RIPPER, ModelRelicRipper::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.RELIC_EARTHSHAKER, ModelRelicEarthshaker::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.PULSED_GRENADE, ModelPulsedGrenade::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.RELIC_ANNIHILATOR, ModelRelicAnnihilator::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.REALM_WARDEN, ModelRealmWarden::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.NAMELESS_GUARDIAN, ModelNamelessGuardian::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.GUARDIAN_BLADE, ModelGuardianBlade::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.MAGIC_GOLEM, ModelMagicGolem::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.IMMORTAL_SKELETON, ModelAbsImmortalSkeleton::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.IMMORTAL_SHAMAN, ModelImmortalShaman::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.IMMORTAL_EXECUTIONER, ModelImmortalExecutioner::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.IMMORTAL, ModelImmortal::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.IMMORTAL_SHURIKEN, ModelImmortalShuriken::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.ANNIHILATOR_MISSILE, ModelAnnihilatorMissile::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.DOOMBOLT_AXE, ModelDoomboltAxe::createBodyLayer);

        EntityModelLayerRegistry.register(ModModelLayer.GHOST_WARRIOR_ARMOR, () -> ModelGhostWarriorArmor.createBodyLayer(new CubeDeformation(0.5F)));
        EntityModelLayerRegistry.register(ModModelLayer.GHOST_WARRIOR_ARMOR_LEGS, () -> ModelGhostWarriorArmor.createBodyLayer(new CubeDeformation(0.2F)));
        EntityModelLayerRegistry.register(ModModelLayer.SOUL_SUMMONING_NECKLACE, ModelSoulSummoningNecklace::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.THE_NETHERWORLD_KATANA, ModelTheNetherworldKatana::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.THE_GUARDIAN_BATTLEAXE, ModelGuardianBattleaxe::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayer.BUSTER_GAUNTLET, ModelBusterGauntlet::createBodyLayer);

        EntityModelLayerRegistry.register(ModModelLayer.SLIDING_DOOR, SlidingDoorModel::createBodyLayer);
    }

    public static void registerRenderers() {
        EntityRendererRegistry.register(EntityInit.TESTER, RenderTester::new);
        EntityRendererRegistry.register(EntityInit.CORPSE, RenderCorpse::new);
        EntityRendererRegistry.register(EntityInit.CORPSE_VILLAGER, RenderCorpseVillager::new);
        EntityRendererRegistry.register(EntityInit.CORPSE_WARLOCK, RenderCorpseWarlock::new);
        EntityRendererRegistry.register(EntityInit.CORPSE_TO_PLAYER, RenderCorpse::new);
        EntityRendererRegistry.register(EntityInit.RELIC_OBSERVER, RenderRelicObserver::new);
        EntityRendererRegistry.register(EntityInit.RELIC_RIPPER, RenderRelicRipper::new);
        EntityRendererRegistry.register(EntityInit.RELIC_EARTHSHAKER, RenderRelicEarthshaker::new);
        EntityRendererRegistry.register(EntityInit.RELIC_ANNIHILATOR, RenderRelicAnnihilator::new);
        EntityRendererRegistry.register(EntityInit.REALM_WARDEN, RenderRealmWarden::new);
        EntityRendererRegistry.register(EntityInit.NAMELESS_GUARDIAN, RenderNamelessGuardian::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_SKELETON, RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_ARCHER, RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_KNIGHT, RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_MAGE, RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_WARRIOR, RenderAbsImmortalSkeleton::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_SHAMAN, RenderImmortalShaman::new);
        EntityRendererRegistry.register(EntityInit.MAGIC_GOLEM, RenderMagicGolem::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_EXECUTIONER, RenderImmortalExecutioner::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_BOSS, RenderImmortal::new);

        EntityRendererRegistry.register(EntityInit.TELEGRAPH, RendererTelegraph::new);
        EntityRendererRegistry.register(EntityInit.GUARDIAN_BLADE, RenderGuardianBlade::new);
        EntityRendererRegistry.register(EntityInit.SHAMAN_BOMB, RenderShamanBomb::new);
        EntityRendererRegistry.register(EntityInit.CAMERA_SHAKE, EmptyRender::new);
        EntityRendererRegistry.register(EntityInit.EXPLODE, EmptyRender::new);
        EntityRendererRegistry.register(EntityInit.OVERLOAD_EXPLODE, EmptyRender::new);
        EntityRendererRegistry.register(EntityInit.FALLING_BLOCK, RenderFallingBlock::new);
        EntityRendererRegistry.register(EntityInit.DOOMBOLT_AXE, RenderDoomboltAxe::new);
        EntityRendererRegistry.register(EntityInit.INFRARED_RAY, RenderInfraredRay::new);
        EntityRendererRegistry.register(EntityInit.GUARDIAN_LASER, RenderGuardianLaser::new);
        EntityRendererRegistry.register(EntityInit.EYE_OF_STRUCTURE, (context) -> new ThrownItemRenderer<>(context, 1.5F, true));
        EntityRendererRegistry.register(EntityInit.POISON_ARROW, RenderPoisonArrow::new);
        EntityRendererRegistry.register(EntityInit.BLOOD_BALL, RendererBloodBall::new);
        EntityRendererRegistry.register(EntityInit.CRIMSON_CRACK, RenderCrimsonCrack::new);
        EntityRendererRegistry.register(EntityInit.CRIMSON_RAY, RenderCrimsonRay::new);
        EntityRendererRegistry.register(EntityInit.CRIMSON_RAY_PRE, RenderCrimsonRay.RenderPreAttack::new);
        EntityRendererRegistry.register(EntityInit.PULSED_GRENADE, RenderPulsedGrenade::new);
        EntityRendererRegistry.register(EntityInit.ELECTROMAGNETIC, EmptyRender::new);
        EntityRendererRegistry.register(EntityInit.ANNIHILATOR_MISSILE, RenderAnnihilatorMissile::new);
        EntityRendererRegistry.register(EntityInit.SURGE, EmptyRender::new);
        //EntityRendererRegistry.register(EntityInit.ALIEN_PORTAL, RenderAlienPortal::new);
        EntityRendererRegistry.register(EntityInit.MAGIC_CIRCLE, RenderImmortalMagicCircle::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_SHURIKEN, RenderImmortalShuriken::new);
        EntityRendererRegistry.register(EntityInit.IMMORTAL_LASER, RenderImmortalLaser::new);

        BlockEntityRendererRegistry.register(BlockEntityInit.SLIDING_DOOR_BE.get(), RenderSlidingDoor::new);
    }

    public static void registerParticleProviders() {
        ParticleProviderRegistry.register(ParticleInit.SPELL_CASTING.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.ADV_ORB.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.GLOW.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.ADV_RING.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.ADV_RING2.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.ADV_RING3.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.CRIMSON.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.CRIMSON_EYE.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.STRIP_SOUL_FIRE.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.CROSS_FLASH.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.VORTEX.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.CRIT_RING.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.THUMP_RING.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.PUNCTURED_AIR_RING.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.SURGE.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.SPARK.get(), AdvancedParticleBase.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.FLAT_RIBBON.get(), ParticleRibbon.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.BLOCK.get(), AdvancedBlockParticle.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.DUST.get(), ParticleDust.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.ORB.get(), ParticleOrb.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.GUARDIAN_SPARK.get(), ParticleGuardianSpark.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.HIT_CUT.get(), ParticleHitCut.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.POISON.get(), ParticlePoison.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.WARLOCK_HEAL.get(), FlameParticle.Provider::new);
        ParticleProviderRegistry.register(ParticleInit.IMMORTAL_EXPLOSION.get(), ParticleImmortalExplosion.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.BLAZE_EXPLOSION.get(), ParticleLastingExplosion.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.VOLT_EXPLOSION.get(), ParticleLastingExplosion.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.RING.get(), ParticleRing.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.BIG_RING.get(), ParticleRing.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.RADIAL_OPACITY_RING.get(), ParticleRing.Factory::new);
        ParticleProviderRegistry.register(ParticleInit.BLAZE_EXPLOSION_EMITTER.get(), new ParticleBlazeExplosionSeed.Factory());
        ParticleProviderRegistry.register(ParticleInit.VOLT_EXPLOSION_EMITTER.get(), new ParticleVoltExplosionSeed.Factory());
    }

    public static void registerModels() {
        PlatformServices.get().registerSpecialModels();
    }
}
