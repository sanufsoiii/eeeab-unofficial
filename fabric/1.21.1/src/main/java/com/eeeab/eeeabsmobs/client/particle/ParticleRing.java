package com.eeeab.eeeabsmobs.client.particle;

import com.eeeab.eeeabsmobs.server.init.ParticleInit;
import com.eeeab.eeeabsmobs.server.util.ModMathUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class ParticleRing extends TextureSheetParticle {
    public float red, green, blue, opacity;
    public boolean facesCamera;
    public float yRot, xRot;
    public float scale;

    private final EnumRingBehavior behavior;

    public enum EnumRingBehavior {
        SHRINK,
        GROW,
        CONSTANT,
        GROW_THEN_SHRINK
    }

    public ParticleRing(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, float yRot, float xRot, int duration, float red, float green, float blue, float opacity, float scale, boolean facesCamera, EnumRingBehavior behavior) {
        super(world, x, y, z);
        super.alpha = 1;
        setSize(1, 1);
        this.scale = scale * 0.1f;
        lifetime = duration;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.opacity = opacity;
        this.yRot = yRot;
        this.xRot = xRot;
        this.facesCamera = facesCamera;
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.behavior = behavior;
    }

    @Override
    public int getLightColor(float delta) {
        return 240 | super.getLightColor(delta) & 0xFF0000;
    }

    @Override
    public void tick() {
        super.tick();
        if (age >= lifetime) {
            remove();
        }
    }

    @Override
    //参考自: net.minecraft.client.particle.SingleQuadParticle.render
    public void render(VertexConsumer vertexConsumer, Camera renderInfo, float partialTicks) {
        float var = (age + partialTicks) / lifetime;
        var = Mth.clamp(var, 0.0F, 1.0F);
        if (behavior == EnumRingBehavior.GROW) {
            quadSize = scale * var;
        } else if (behavior == EnumRingBehavior.SHRINK) {
            quadSize = scale * (1 - var);
        } else if (behavior == EnumRingBehavior.GROW_THEN_SHRINK) {
            quadSize = (float) (scale * (1 - var - Math.pow(2000, -var)));
        } else {
            quadSize = scale;
        }
        float fadeStart = 0.1F;
        float alpha;
        if (var <= fadeStart) {
            alpha = opacity;
        } else {
            float t = (var - fadeStart) / (1 - fadeStart);
            alpha = opacity * (1 - t);
        }
        this.alpha = Math.max(0.01F, alpha);
        rCol = red;
        gCol = green;
        bCol = blue;
        Vec3 vec3 = renderInfo.getPosition();
        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternion = new Quaternionf();
        if (facesCamera) {
            if (this.roll == 0.0F) {
                quaternion = renderInfo.rotation();
            } else {
                quaternion = new Quaternionf(renderInfo.rotation());
                float f3 = Mth.lerp(partialTicks, this.oRoll, this.roll);
                quaternion.mul(Axis.ZP.rotation(f3));
            }
        } else {
            Quaternionf quatX = ModMathUtils.rotationXYZ(xRot, 0, 0, false);
            Quaternionf quatY = ModMathUtils.rotationXYZ(0, yRot, 0, false);
            quaternion.mul(quatY);
            quaternion.mul(quatX);
        }
        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float f3 = this.getQuadSize(partialTicks);
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }
        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(partialTicks);
        vertexConsumer.addVertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).setUv(f7, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        vertexConsumer.addVertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).setUv(f7, f4).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        vertexConsumer.addVertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).setUv(f6, f4).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        vertexConsumer.addVertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).setUv(f6, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        vertexConsumer.addVertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).setUv(f6, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        vertexConsumer.addVertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).setUv(f6, f4).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        vertexConsumer.addVertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).setUv(f7, f4).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        vertexConsumer.addVertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).setUv(f7, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static final class Factory implements ParticleProvider<RingData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle createParticle(RingData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleRing particle = new ParticleRing(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getyRot(), typeIn.getxRot(), typeIn.getDuration(), typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getAlpha(), typeIn.getScale(), typeIn.getFacesCamera(), typeIn.getBehavior());
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }

    public static class RingData implements ParticleOptions {
        private final ParticleType<? extends RingData> type;
        private final float yRot;
        private final float xRot;
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;
        private final float scale;
        private final int duration;
        private final boolean facesCamera;
        private final EnumRingBehavior behavior;

        public RingData(float yRot, float pitch, int duration, float red, float g, float blue, float alpha, float scale, boolean facesCamera, EnumRingBehavior behavior) {
            this(ParticleInit.RING.get(), yRot, pitch, duration, red, g, blue, alpha, scale, facesCamera, behavior);
        }

        public RingData(ParticleType<? extends RingData> type, float yRot, float pitch, int duration, float red, float g, float blue, float alpha, float scale, boolean facesCamera, EnumRingBehavior behavior) {
            this.type = type;
            this.yRot = yRot;
            this.xRot = pitch;
            this.red = red;
            this.green = g;
            this.blue = blue;
            this.alpha = alpha;
            this.scale = scale;
            this.duration = duration;
            this.facesCamera = facesCamera;
            this.behavior = behavior;
        }

        @Override
        public ParticleType<? extends RingData> getType() {
            return this.type;
        }

        @Environment(EnvType.CLIENT)
        public float getyRot() {
            return this.yRot;
        }

        @Environment(EnvType.CLIENT)
        public float getxRot() {
            return this.xRot;
        }

        @Environment(EnvType.CLIENT)
        public float getRed() {
            return this.red;
        }

        @Environment(EnvType.CLIENT)
        public float getGreen() {
            return this.green;
        }

        @Environment(EnvType.CLIENT)
        public float getBlue() {
            return this.blue;
        }

        @Environment(EnvType.CLIENT)
        public float getAlpha() {
            return this.alpha;
        }

        @Environment(EnvType.CLIENT)
        public float getScale() {
            return this.scale;
        }

        @Environment(EnvType.CLIENT)
        public int getDuration() {
            return this.duration;
        }

        @Environment(EnvType.CLIENT)
        public boolean getFacesCamera() {
            return this.facesCamera;
        }

        @Environment(EnvType.CLIENT)
        public EnumRingBehavior getBehavior() {
            return this.behavior;
        }

        // See ParticleOrb.OrbData for why writeToNetwork/writeToString/Deserializer are gone and codec()/
        // streamCodec() replace them (ParticleOptions/ParticleType redesign in 1.21).
        public static MapCodec<RingData> codec(ParticleType<RingData> particleType) {
            return RecordCodecBuilder.mapCodec((codecBuilder) -> codecBuilder.group(
                            Codec.FLOAT.fieldOf("yRot").forGetter(RingData::getyRot),
                            Codec.FLOAT.fieldOf("xRot").forGetter(RingData::getxRot),
                            Codec.FLOAT.fieldOf("red").forGetter(RingData::getRed),
                            Codec.FLOAT.fieldOf("green").forGetter(RingData::getGreen),
                            Codec.FLOAT.fieldOf("blue").forGetter(RingData::getBlue),
                            Codec.FLOAT.fieldOf("alpha").forGetter(RingData::getAlpha),
                            Codec.FLOAT.fieldOf("scale").forGetter(RingData::getScale),
                            Codec.INT.fieldOf("duration").forGetter(RingData::getDuration),
                            Codec.BOOL.fieldOf("facesCamera").forGetter(RingData::getFacesCamera),
                            Codec.STRING.fieldOf("behavior").forGetter((ringData) -> ringData.getBehavior().toString())
                    ).apply(codecBuilder, (yRot, xRot, red, green, blue, alpha, scale, duration, facesCamera, behavior) ->
                            new RingData(particleType, yRot, xRot, duration, red, green, blue, alpha, scale, facesCamera, EnumRingBehavior.valueOf(behavior)))
            );
        }

        // StreamCodec.composite tops out at 6 fields; RingData has 9, so the trailing 5 (alpha, scale,
        // duration, facesCamera, behavior) are grouped into one sub-codec first.
        private record Tail(float alpha, float scale, int duration, boolean facesCamera, EnumRingBehavior behavior) {
            static final StreamCodec<ByteBuf, Tail> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.FLOAT, Tail::alpha,
                    ByteBufCodecs.FLOAT, Tail::scale,
                    ByteBufCodecs.VAR_INT, Tail::duration,
                    ByteBufCodecs.BOOL, Tail::facesCamera,
                    ByteBufCodecs.STRING_UTF8.map(EnumRingBehavior::valueOf, EnumRingBehavior::toString), Tail::behavior,
                    Tail::new
            );
        }

        public static StreamCodec<RegistryFriendlyByteBuf, RingData> streamCodec(ParticleType<RingData> particleType) {
            return StreamCodec.composite(
                    ByteBufCodecs.FLOAT, RingData::getyRot,
                    ByteBufCodecs.FLOAT, RingData::getxRot,
                    ByteBufCodecs.FLOAT, RingData::getRed,
                    ByteBufCodecs.FLOAT, RingData::getGreen,
                    ByteBufCodecs.FLOAT, RingData::getBlue,
                    Tail.STREAM_CODEC, ringData -> new Tail(ringData.getAlpha(), ringData.getScale(), ringData.getDuration(), ringData.getFacesCamera(), ringData.getBehavior()),
                    (yRot, xRot, red, green, blue, tail) ->
                            new RingData(particleType, yRot, xRot, tail.duration(), red, green, blue, tail.alpha(), tail.scale(), tail.facesCamera(), tail.behavior())
            );
        }
    }
}
