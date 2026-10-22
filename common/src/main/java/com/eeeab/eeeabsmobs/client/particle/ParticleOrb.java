package com.eeeab.eeeabsmobs.client.particle;

import net.minecraft.client.renderer.state.QuadParticleRenderState;
import com.eeeab.eeeabsmobs.server.init.ParticleInit;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
public class ParticleOrb extends SingleQuadParticle {
    private final double duration;

    public ParticleOrb(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, double scale, int duration) {
        super(world, x, y, z, (net.minecraft.client.renderer.texture.TextureAtlasSprite) null);
        quadSize = (float) scale * 0.1f;
        lifetime = duration;
        xd = vx;
        yd = vy;
        zd = vz;
        this.duration = duration;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightColor(float delta) {
        return 240 | super.getLightColor(delta) & 0xFF0000;
    }

    @Override
    public void tick() {
        alpha = 0.1f;
        xo = x;
        yo = y;
        zo = z;
        super.tick();
        if (age >= lifetime) {
            remove();
        }
        age++;
    }

    @Override
    public void extract(QuadParticleRenderState renderState, Camera renderInfo, float partialTicks) {
        alpha = Math.max(1 - ((float) age + partialTicks) / (float) duration, 0.001f);
        super.extract(renderState, renderInfo, partialTicks);
    }
public static final class Factory implements ParticleProvider<OrbData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle createParticle(OrbData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, net.minecraft.util.RandomSource randomSource) {
            ParticleOrb particle = new ParticleOrb(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getScale(), typeIn.getDuration());
            particle.setSpriteFromAge(spriteSet);
            particle.setColor(typeIn.getR(), typeIn.getG(), typeIn.getB());
            return particle;
        }
    }

    public static class OrbData implements ParticleOptions {
        private final float r;
        private final float g;
        private final float b;
        private final float scale;
        private final int duration;

        public OrbData(float r, float g, float b, float scale, int duration) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.scale = scale;
            this.duration = duration;
        }

        @Override
        public ParticleType<OrbData> getType() {
            return ParticleInit.ORB.get();
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
public int getDuration() {
            return this.duration;
        }

        // ParticleOptions lost writeToNetwork/writeToString/Deserializer entirely in 1.21 (see
        // net.minecraft.core.particles.ParticleOptions in the sources jar - it is now just getType()).
        // Serialization moved onto ParticleType itself via codec()/streamCodec(); these two static factories
        // are what ParticleInit's anonymous ParticleType<OrbData> subclass delegates to.
        public static MapCodec<OrbData> codec(ParticleType<OrbData> particleType) {
            return RecordCodecBuilder.mapCodec((codecBuilder) -> codecBuilder.group(
                    com.mojang.serialization.Codec.FLOAT.fieldOf("r").forGetter(OrbData::getR),
                    com.mojang.serialization.Codec.FLOAT.fieldOf("g").forGetter(OrbData::getG),
                    com.mojang.serialization.Codec.FLOAT.fieldOf("b").forGetter(OrbData::getB),
                    com.mojang.serialization.Codec.FLOAT.fieldOf("scale").forGetter(OrbData::getScale),
                    com.mojang.serialization.Codec.INT.fieldOf("duration").forGetter(OrbData::getDuration)
                    ).apply(codecBuilder, OrbData::new)
            );
        }

        public static StreamCodec<RegistryFriendlyByteBuf, OrbData> streamCodec(ParticleType<OrbData> particleType) {
            return StreamCodec.composite(
                    ByteBufCodecs.FLOAT, OrbData::getR,
                    ByteBufCodecs.FLOAT, OrbData::getG,
                    ByteBufCodecs.FLOAT, OrbData::getB,
                    ByteBufCodecs.FLOAT, OrbData::getScale,
                    ByteBufCodecs.INT, OrbData::getDuration,
                    OrbData::new
            );
        }
    }
}
