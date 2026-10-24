package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.particle.ParticleDust;
import com.eeeab.eeeabsmobs.client.particle.ParticleOrb;
import com.eeeab.eeeabsmobs.client.particle.ParticleRing;
import com.eeeab.eeeabsmobs.client.particle.lib.data.AdvancedBlockParticleData;
import com.eeeab.eeeabsmobs.client.particle.lib.data.AdvancedParticleData;
import com.eeeab.eeeabsmobs.client.particle.lib.data.RibbonParticleData;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ParticleInit {
    public static final FabricDeferredRegister<ParticleType<?>> PARTICLES = FabricDeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, EEEABMobs.MOD_ID);

    public static final FabricDeferredRegister.Entry<SimpleParticleType> HIT_CUT = PARTICLES.register("hit_cut", () -> new SimpleParticleType(true));
    public static final FabricDeferredRegister.Entry<SimpleParticleType> GUARDIAN_SPARK = PARTICLES.register("guardian_spark", () -> new SimpleParticleType(true));
    public static final FabricDeferredRegister.Entry<SimpleParticleType> POISON = PARTICLES.register("poison", () -> new SimpleParticleType(false));
    public static final FabricDeferredRegister.Entry<SimpleParticleType> WARLOCK_HEAL = PARTICLES.register("warlock_heal", () -> new SimpleParticleType(false));
    public static final FabricDeferredRegister.Entry<SimpleParticleType> IMMORTAL_EXPLOSION = PARTICLES.register("immortal_explosion", () -> new SimpleParticleType(true));
    public static final FabricDeferredRegister.Entry<SimpleParticleType> BLAZE_EXPLOSION = PARTICLES.register("blaze_explosion", () -> new SimpleParticleType(true));
    public static final FabricDeferredRegister.Entry<SimpleParticleType> VOLT_EXPLOSION = PARTICLES.register("volt_explosion", () -> new SimpleParticleType(true));
    public static final FabricDeferredRegister.Entry<SimpleParticleType> BLAZE_EXPLOSION_EMITTER = PARTICLES.register("blaze_explosion_emitter", () -> new SimpleParticleType(true));
    public static final FabricDeferredRegister.Entry<SimpleParticleType> VOLT_EXPLOSION_EMITTER = PARTICLES.register("volt_explosion_emitter", () -> new SimpleParticleType(true));
    public static final FabricDeferredRegister.Entry<ParticleType<ParticleOrb.OrbData>> ORB = PARTICLES.register("orb", () -> new ParticleType<>(false, ParticleOrb.OrbData.DESERIALIZER) {
        @Override
        public Codec<ParticleOrb.OrbData> codec() {
            return ParticleOrb.OrbData.codec(this);
        }
    });
    public static final FabricDeferredRegister.Entry<ParticleType<ParticleDust.DustData>> DUST = PARTICLES.register("dust", () -> new ParticleType<>(false, ParticleDust.DustData.DESERIALIZER) {
        @Override
        public Codec<ParticleDust.DustData> codec() {
            return ParticleDust.DustData.codec(this);
        }
    });
    public static final FabricDeferredRegister.Entry<ParticleType<ParticleRing.RingData>> RING = PARTICLES.register("ring", () -> new ParticleType<>(false, ParticleRing.RingData.DESERIALIZER) {
        @Override
        public Codec<ParticleRing.RingData> codec() {
            return ParticleRing.RingData.codec(this);
        }
    });
    public static final FabricDeferredRegister.Entry<ParticleType<ParticleRing.RingData>> BIG_RING = PARTICLES.register("big_ring", () -> new ParticleType<>(false, ParticleRing.RingData.DESERIALIZER) {
        @Override
        public Codec<ParticleRing.RingData> codec() {
            return ParticleRing.RingData.codec(this);
        }
    });
    public static final FabricDeferredRegister.Entry<ParticleType<ParticleRing.RingData>> RADIAL_OPACITY_RING = PARTICLES.register("radial_opacity_ring", () -> new ParticleType<>(false, ParticleRing.RingData.DESERIALIZER) {
        @Override
        public Codec<ParticleRing.RingData> codec() {
            return ParticleRing.RingData.codec(this);
        }
    });
    public static final FabricDeferredRegister.Entry<ParticleType<BlockParticleOption>> CUSTOM_BLOCK = PARTICLES.register("custom_block", () -> new ParticleType<BlockParticleOption>(false, BlockParticleOption.DESERIALIZER) {
        @Override
        public Codec<BlockParticleOption> codec() {
            return BlockParticleOption.codec(this);
        }
    });

    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> SPELL_CASTING = ParticleInit.registerAdvancedParticle("spell_casting");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> ADV_ORB = ParticleInit.registerAdvancedParticle("adv_orb");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> GLOW = ParticleInit.registerAdvancedParticle("glow");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> ADV_RING = ParticleInit.registerAdvancedParticle("adv_ring");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> ADV_RING2 = ParticleInit.registerAdvancedParticle("adv_ring2");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> ADV_RING3 = ParticleInit.registerAdvancedParticle("adv_ring3");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> CRIMSON = ParticleInit.registerAdvancedParticle("crimson");
    public static final FabricDeferredRegister.Entry<ParticleType<RibbonParticleData>> FLAT_RIBBON = ParticleInit.registerRibbonParticle("flat_ribbon");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> CRIMSON_EYE = ParticleInit.registerAdvancedParticle("crimson_eye");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> STRIP_SOUL_FIRE = ParticleInit.registerAdvancedParticle("strip_soul_fire");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> CROSS_FLASH = ParticleInit.registerAdvancedParticle("cross_flash");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> VORTEX = ParticleInit.registerAdvancedParticle("vortex");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> CRIT_RING = ParticleInit.registerAdvancedParticle("crit_ring");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> THUMP_RING = ParticleInit.registerAdvancedParticle("thump_ring");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> PUNCTURED_AIR_RING = ParticleInit.registerAdvancedParticle("punctured_air_ring");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> SURGE = ParticleInit.registerAdvancedParticle("surge");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> SPARK = ParticleInit.registerAdvancedParticle("spark");
    public static final FabricDeferredRegister.Entry<ParticleType<AdvancedBlockParticleData>> BLOCK = PARTICLES.register("block", () -> new ParticleType<>(false, AdvancedBlockParticleData.DESERIALIZER) {
        @Override
        public Codec<AdvancedBlockParticleData> codec() {
            return AdvancedBlockParticleData.codecBlock(this);
        }
    });

    private static FabricDeferredRegister.Entry<ParticleType<AdvancedParticleData>> registerAdvancedParticle(String key) {
        return PARTICLES.register(key, () -> new ParticleType<>(false, AdvancedParticleData.DESERIALIZER) {
            @Override
            public Codec<AdvancedParticleData> codec() {
                return AdvancedParticleData.codec(this);
            }
        });
    }

    private static FabricDeferredRegister.Entry<ParticleType<RibbonParticleData>> registerRibbonParticle(String key) {
        return PARTICLES.register(key, () -> new ParticleType<>(false, RibbonParticleData.DESERIALIZER) {
            @Override
            public Codec<RibbonParticleData> codec() {
                return RibbonParticleData.codecRibbon(this);
            }
        });
    }

    public static void register(Object bus) {
        PARTICLES.register(bus);
    }
}
