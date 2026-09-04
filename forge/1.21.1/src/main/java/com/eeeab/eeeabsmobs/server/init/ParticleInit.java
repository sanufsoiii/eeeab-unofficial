package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.particle.ParticleDust;
import com.eeeab.eeeabsmobs.client.particle.ParticleOrb;
import com.eeeab.eeeabsmobs.client.particle.ParticleRing;
import com.eeeab.eeeabsmobs.client.particle.lib.data.AdvancedBlockParticleData;
import com.eeeab.eeeabsmobs.client.particle.lib.data.AdvancedParticleData;
import com.eeeab.eeeabsmobs.client.particle.lib.data.RibbonParticleData;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.RegistryObject;

/**
 * ParticleType went from taking a runtime ParticleOptions.Deserializer (removed entirely - ParticleOptions is
 * now just {@code getType()}, see the sources jar) to an abstract class with two abstract methods,
 * {@code MapCodec<T> codec()} and {@code StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec()}. Each
 * custom particle data class (ParticleOrb.OrbData, ParticleDust.DustData, ParticleRing.RingData,
 * AdvancedParticleData, RibbonParticleData, AdvancedBlockParticleData) now exposes static
 * codec(ParticleType)/streamCodec(ParticleType) factories that the anonymous ParticleType subclasses below
 * delegate to, mirroring vanilla's own BlockParticleOption.codec()/streamCodec() pattern.
 */
public class ParticleInit {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, EEEABMobs.MOD_ID);

    public static final RegistryObject<SimpleParticleType> HIT_CUT = PARTICLES.register("hit_cut", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> GUARDIAN_SPARK = PARTICLES.register("guardian_spark", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> POISON = PARTICLES.register("poison", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> WARLOCK_HEAL = PARTICLES.register("warlock_heal", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> IMMORTAL_EXPLOSION = PARTICLES.register("immortal_explosion", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLAZE_EXPLOSION = PARTICLES.register("blaze_explosion", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> VOLT_EXPLOSION = PARTICLES.register("volt_explosion", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLAZE_EXPLOSION_EMITTER = PARTICLES.register("blaze_explosion_emitter", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> VOLT_EXPLOSION_EMITTER = PARTICLES.register("volt_explosion_emitter", () -> new SimpleParticleType(true));
    public static final RegistryObject<ParticleType<ParticleOrb.OrbData>> ORB = PARTICLES.register("orb", () -> new ParticleType<>(false) {
        public MapCodec<ParticleOrb.OrbData> codec() {
            return ParticleOrb.OrbData.codec(this);
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, ParticleOrb.OrbData> streamCodec() {
            return ParticleOrb.OrbData.streamCodec(this);
        }
    });
    public static final RegistryObject<ParticleType<ParticleDust.DustData>> DUST = PARTICLES.register("dust", () -> new ParticleType<>(false) {
        public MapCodec<ParticleDust.DustData> codec() {
            return ParticleDust.DustData.codec(this);
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, ParticleDust.DustData> streamCodec() {
            return ParticleDust.DustData.streamCodec(this);
        }
    });
    public static final RegistryObject<ParticleType<ParticleRing.RingData>> RING = PARTICLES.register("ring", () -> new ParticleType<>(false) {
        public MapCodec<ParticleRing.RingData> codec() {
            return ParticleRing.RingData.codec(this);
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, ParticleRing.RingData> streamCodec() {
            return ParticleRing.RingData.streamCodec(this);
        }
    });
    public static final RegistryObject<ParticleType<ParticleRing.RingData>> BIG_RING = PARTICLES.register("big_ring", () -> new ParticleType<>(false) {
        public MapCodec<ParticleRing.RingData> codec() {
            return ParticleRing.RingData.codec(this);
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, ParticleRing.RingData> streamCodec() {
            return ParticleRing.RingData.streamCodec(this);
        }
    });
    // RADIAL_OPACITY_RING was referenced by RegisterHandler (event.registerSpriteSet) but its registration was
    // missing from this file - added here following the exact same RING/BIG_RING pattern (same RingData codec).
    public static final RegistryObject<ParticleType<ParticleRing.RingData>> RADIAL_OPACITY_RING = PARTICLES.register("radial_opacity_ring", () -> new ParticleType<>(false) {
        public MapCodec<ParticleRing.RingData> codec() {
            return ParticleRing.RingData.codec(this);
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, ParticleRing.RingData> streamCodec() {
            return ParticleRing.RingData.streamCodec(this);
        }
    });
    public static final RegistryObject<ParticleType<BlockParticleOption>> CUSTOM_BLOCK = PARTICLES.register("custom_block", () -> new ParticleType<BlockParticleOption>(false) {
        public MapCodec<BlockParticleOption> codec() {
            return BlockParticleOption.codec(this);
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOption> streamCodec() {
            return BlockParticleOption.streamCodec(this);
        }
    });

    public static final RegistryObject<ParticleType<AdvancedParticleData>> SPELL_CASTING = ParticleInit.registerAdvancedParticle("spell_casting");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ADV_ORB = ParticleInit.registerAdvancedParticle("adv_orb");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> GLOW = ParticleInit.registerAdvancedParticle("glow");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ADV_RING = ParticleInit.registerAdvancedParticle("adv_ring");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ADV_RING2 = ParticleInit.registerAdvancedParticle("adv_ring2");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ADV_RING3 = ParticleInit.registerAdvancedParticle("adv_ring3");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> CRIMSON = ParticleInit.registerAdvancedParticle("crimson");
    public static final RegistryObject<ParticleType<RibbonParticleData>> FLAT_RIBBON = ParticleInit.registerRibbonParticle("flat_ribbon");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> CRIMSON_EYE = ParticleInit.registerAdvancedParticle("crimson_eye");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> STRIP_SOUL_FIRE = ParticleInit.registerAdvancedParticle("strip_soul_fire");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> CROSS_FLASH = ParticleInit.registerAdvancedParticle("cross_flash");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> VORTEX = ParticleInit.registerAdvancedParticle("vortex");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> CRIT_RING = ParticleInit.registerAdvancedParticle("crit_ring");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> THUMP_RING = ParticleInit.registerAdvancedParticle("thump_ring");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> PUNCTURED_AIR_RING = ParticleInit.registerAdvancedParticle("punctured_air_ring");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> SURGE = ParticleInit.registerAdvancedParticle("surge");
    public static final RegistryObject<ParticleType<AdvancedParticleData>> SPARK = ParticleInit.registerAdvancedParticle("spark");
    public static final RegistryObject<ParticleType<AdvancedBlockParticleData>> BLOCK = PARTICLES.register("block", () -> new ParticleType<>(false) {
        public MapCodec<AdvancedBlockParticleData> codec() {
            return AdvancedBlockParticleData.codecBlock();
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, AdvancedBlockParticleData> streamCodec() {
            return AdvancedBlockParticleData.streamCodecBlock();
        }
    });

    private static RegistryObject<ParticleType<AdvancedParticleData>> registerAdvancedParticle(String key) {
        return PARTICLES.register(key, () -> new ParticleType<>(false) {
            public MapCodec<AdvancedParticleData> codec() {
                return AdvancedParticleData.codec(this);
            }

            public StreamCodec<? super RegistryFriendlyByteBuf, AdvancedParticleData> streamCodec() {
                return AdvancedParticleData.streamCodec(this);
            }
        });
    }

    private static RegistryObject<ParticleType<RibbonParticleData>> registerRibbonParticle(String key) {
        return PARTICLES.register(key, () -> new ParticleType<>(false) {
            public MapCodec<RibbonParticleData> codec() {
                return RibbonParticleData.codecRibbon(this);
            }

            public StreamCodec<? super RegistryFriendlyByteBuf, RibbonParticleData> streamCodec() {
                return RibbonParticleData.streamCodecRibbon(this);
            }
        });
    }

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }
}
