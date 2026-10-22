package com.eeeab.eeeabsmobs.client.particle;

import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
public class ParticleDust extends TextureSheetParticle {
    private final float scale, airDiffusionSpeed, maxAlpha;
    private final boolean emissive;
    private final EnumDustBehavior behavior;

    public enum EnumDustBehavior {
        SHRINK,
        GROW,
        CONSTANT
    }

    public ParticleDust(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, double scale, int duration, EnumDustBehavior behavior, double airDiffusionSpeed, boolean emissive) {
        super(world, x, y, z);
        float randomFactor = 0.6F + 0.4F * this.random.nextFloat();
        this.scale = (float) scale * 0.05F * randomFactor;
        this.maxAlpha = 0.25F + this.random.nextFloat() * 0.25F;
        this.lifetime = duration + this.random.nextInt(3) + (int) ((this.maxAlpha - 0.25F) * 50);
        this.xd = vx * 0.5;
        this.yd = vy * 0.5;
        this.zd = vz * 0.5;
        this.behavior = behavior;
        this.roll = this.oRoll = (float) (this.random.nextInt(4) * Math.PI / 2);
        this.airDiffusionSpeed = (float) airDiffusionSpeed;
        this.emissive = emissive;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModRenderType.PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
    }

    @Override
    public void tick() {
        super.tick();
        xd *= airDiffusionSpeed;
        yd *= airDiffusionSpeed;
        zd *= airDiffusionSpeed;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return this.emissive ? 240 : super.getLightColor(partialTick);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float var = (age + partialTicks) / (float) lifetime;
        var = Mth.clamp(var, 0.0F, 1.0F);
        float fadeStart = 0.1F;
        float alpha;
        if (var <= fadeStart) {
            alpha = maxAlpha;
        } else {
            float t = (var - fadeStart) / (1 - fadeStart);
            alpha = maxAlpha * (float) (1 - Math.pow(t, 3));
        }
        if (alpha < 0.01F) alpha = 0.01F;
        this.alpha = alpha;
        if (behavior == EnumDustBehavior.SHRINK) {
            this.quadSize = scale * ((1 - 0.7f * var) + 0.3f);
        } else if (behavior == EnumDustBehavior.GROW) {
            this.quadSize = scale * ((0.7f * var) + 0.3f);
        } else {
            this.quadSize = scale;
        }
        super.render(buffer, renderInfo, partialTicks);
    }


    public static final class Factory implements ParticleProvider<DustData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle createParticle(DustData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleDust particleDust = new ParticleDust(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getScale(), typeIn.getDuration(), typeIn.getBehavior(), typeIn.getAirDiffusionSpeed(), typeIn.getEmissive());
            particleDust.setSpriteFromAge(spriteSet);
            float randomFactor = (particleDust.random.nextFloat() - particleDust.random.nextFloat()) * 0.1F;
            particleDust.setColor(clampColor(typeIn.getR() + randomFactor), clampColor(typeIn.getG() + randomFactor), clampColor(typeIn.getB() + randomFactor));
            return particleDust;
        }

        private static float clampColor(float color) {
            return Mth.clamp(color, 0F, 1F);
        }
    }

    public static class DustData implements ParticleOptions {
        private final ParticleType<DustData> type;

        private final float r;
        private final float g;
        private final float b;
        private final float scale;
        private final int duration;
        private final EnumDustBehavior behavior;
        private final float airDiffusionSpeed;
        private final boolean emissive;

        public DustData(ParticleType<DustData> type, float r, float g, float b, float scale, int duration, EnumDustBehavior behavior, float airDiffusionSpeed, boolean emissive) {
            this.type = type;
            this.r = r;
            this.g = g;
            this.b = b;
            this.scale = scale;
            this.behavior = behavior;
            this.airDiffusionSpeed = airDiffusionSpeed;
            this.duration = duration;
            this.emissive = emissive;
        }

        public DustData(ParticleType<DustData> type, float r, float g, float b, float scale, int duration, EnumDustBehavior behavior, float airDiffusionSpeed) {
            this(type, r, g, b, scale, duration, behavior, airDiffusionSpeed, false);
        }

        public DustData(ParticleType<DustData> type, float scale, int duration, EnumDustBehavior behavior, float airDiffusionSpeed) {
            this(type, 0.22F, 0.22F, 0.3F, scale, duration, behavior, airDiffusionSpeed, true);
        }

        @Override
        public ParticleType<DustData> getType() {
            return type;
        }

        public float getR() {
            return this.r;
        }

        public float getG() {
            return this.g;
        }

        public float getB() {
            return this.b;
        }

        public float getScale() {
            return this.scale;
        }

        public EnumDustBehavior getBehavior() {
            return this.behavior;
        }

        public int getDuration() {
            return this.duration;
        }

        public float getAirDiffusionSpeed() {
            return this.airDiffusionSpeed;
        }

        public boolean getEmissive() {
            return this.emissive;
        }

        // See ParticleOrb.OrbData for why writeToNetwork/writeToString/Deserializer are gone and codec()/
        // streamCodec() replace them (ParticleOptions/ParticleType redesign in 1.21).
        public static MapCodec<DustData> codec(ParticleType<DustData> particleType) {
            return RecordCodecBuilder.mapCodec((codecBuilder) -> codecBuilder.group(
                            Codec.FLOAT.fieldOf("r").forGetter(DustData::getR),
                            Codec.FLOAT.fieldOf("g").forGetter(DustData::getG),
                            Codec.FLOAT.fieldOf("b").forGetter(DustData::getB),
                            Codec.FLOAT.fieldOf("scale").forGetter(DustData::getScale),
                            Codec.STRING.fieldOf("behavior").forGetter((dustData) -> dustData.getBehavior().toString()),
                            Codec.INT.fieldOf("duration").forGetter(DustData::getDuration),
                            Codec.FLOAT.fieldOf("airDiffusionSpeed").forGetter(DustData::getAirDiffusionSpeed),
                            Codec.BOOL.fieldOf("emissive").forGetter(DustData::getEmissive)
                    ).apply(codecBuilder, (r, g, b, scale, behavior, duration, airDiffusionSpeed, emissive) ->
                            new DustData(particleType, r, g, b, scale, duration, EnumDustBehavior.valueOf(behavior), airDiffusionSpeed, emissive))
            );
        }

        // StreamCodec.composite tops out at 6 fields; the trailing three (duration, airDiffusionSpeed,
        // emissive) are grouped into one sub-codec first to stay within that limit.
        private record Tail(int duration, float airDiffusionSpeed, boolean emissive) {
            static final StreamCodec<ByteBuf, Tail> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, Tail::duration,
                    ByteBufCodecs.FLOAT, Tail::airDiffusionSpeed,
                    ByteBufCodecs.BOOL, Tail::emissive,
                    Tail::new
            );
        }

        public static StreamCodec<RegistryFriendlyByteBuf, DustData> streamCodec(ParticleType<DustData> particleType) {
            return StreamCodec.composite(
                    ByteBufCodecs.FLOAT, DustData::getR,
                    ByteBufCodecs.FLOAT, DustData::getG,
                    ByteBufCodecs.FLOAT, DustData::getB,
                    ByteBufCodecs.FLOAT, DustData::getScale,
                    Tail.STREAM_CODEC, dustData -> new Tail(dustData.getDuration(), dustData.getAirDiffusionSpeed(), dustData.getEmissive()),
                    (r, g, b, scale, tail) ->
                            new DustData(particleType, r, g, b, scale, tail.duration(), EnumDustBehavior.CONSTANT, tail.airDiffusionSpeed(), tail.emissive())
            );
        }
    }
}
