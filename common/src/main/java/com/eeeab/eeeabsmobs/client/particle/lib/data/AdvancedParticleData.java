package com.eeeab.eeeabsmobs.client.particle.lib.data;

import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRotation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
//参考自: https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/util/AdvancedParticleData.java
public class AdvancedParticleData implements ParticleOptions {
    private final ParticleType<? extends AdvancedParticleData> type;
    private final float airDiffusionSpeed;
    private final float red, green, blue, alpha;
    private final ParticleRotation rotation;
    private final float scale;
    private final boolean emissive;
    private final float duration;
    private final boolean canCollide;
    private final boolean isAnimation;

    private final ParticleComponent[] components;

    public AdvancedParticleData(ParticleType<? extends AdvancedParticleData> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double airDiffusionSpeed, double duration, boolean emissive, boolean canCollide) {
        this(type, rotation, scale, r, g, b, a, airDiffusionSpeed, duration, emissive, canCollide, new ParticleComponent[]{}, false);
    }

    public AdvancedParticleData(ParticleType<? extends AdvancedParticleData> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double airDiffusionSpeed, double duration, boolean emissive, boolean canCollide, ParticleComponent[] components, boolean isAnimation) {
        this.type = type;
        this.rotation = rotation;
        this.scale = (float) scale;
        this.red = (float) r;
        this.green = (float) g;
        this.blue = (float) b;
        this.alpha = (float) a;
        this.emissive = emissive;
        this.airDiffusionSpeed = (float) airDiffusionSpeed;
        this.duration = (float) duration;
        this.canCollide = canCollide;
        this.components = components;
        this.isAnimation = isAnimation;
    }

    @Override
    public ParticleType<? extends AdvancedParticleData> getType() {
        return type;
    }
public double getRed() {
        return this.red;
    }
public double getGreen() {
        return this.green;
    }
public double getBlue() {
        return this.blue;
    }
public double getAlpha() {
        return this.alpha;
    }
public double getAirDiffusionSpeed() {
        return this.airDiffusionSpeed;
    }
public ParticleRotation getRotation() {
        return this.rotation;
    }
public double getScale() {
        return this.scale;
    }
public boolean isEmissive() {
        return this.emissive;
    }
public double getDuration() {
        return this.duration;
    }
public boolean getCanCollide() {
        return canCollide;
    }
public ParticleComponent[] getComponents() {
        return this.components;
    }
public boolean isAnimation() {
        return this.isAnimation;
    }

    // ParticleOptions lost writeToNetwork/writeToString/Deserializer entirely in 1.21 (it is now just
    // getType() - see net.minecraft.core.particles.ParticleOptions in the sources jar). Serialization moved
    // onto ParticleType via codec()/streamCodec(); rotation is not round-tripped (matches the old codec, which
    // never serialized it either - only writeToNetwork did, and only as a face-camera angle).
    public static MapCodec<AdvancedParticleData> codec(ParticleType<AdvancedParticleData> particleType) {
        return RecordCodecBuilder.mapCodec((codecBuilder) -> codecBuilder.group(
                Codec.DOUBLE.fieldOf("scale").forGetter(AdvancedParticleData::getScale),
                Codec.DOUBLE.fieldOf("r").forGetter(AdvancedParticleData::getRed),
                Codec.DOUBLE.fieldOf("g").forGetter(AdvancedParticleData::getGreen),
                Codec.DOUBLE.fieldOf("b").forGetter(AdvancedParticleData::getBlue),
                Codec.DOUBLE.fieldOf("a").forGetter(AdvancedParticleData::getAlpha),
                Codec.DOUBLE.fieldOf("airDiffusionSpeed").forGetter(AdvancedParticleData::getAirDiffusionSpeed),
                Codec.DOUBLE.fieldOf("duration").forGetter(AdvancedParticleData::getDuration),
                Codec.BOOL.fieldOf("emissive").forGetter(AdvancedParticleData::isEmissive),
                Codec.BOOL.fieldOf("canCollide").forGetter(AdvancedParticleData::getCanCollide),
                Codec.BOOL.fieldOf("isAnimation").forGetter(AdvancedParticleData::isAnimation)
                ).apply(codecBuilder, (scale, r, g, b, a, airDiffusionSpeed, duration, emissive, canCollide, isAnimation) ->
                        new AdvancedParticleData(particleType, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, airDiffusionSpeed, duration, emissive, canCollide, new ParticleComponent[]{}, isAnimation))
        );
    }

    // StreamCodec.composite tops out at 6 fields; AdvancedParticleData carries 10, split into two groups of 5.
    private record Head(float scale, float red, float green, float blue, float alpha) {
        static final StreamCodec<ByteBuf, Head> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Head::scale,
                ByteBufCodecs.FLOAT, Head::red,
                ByteBufCodecs.FLOAT, Head::green,
                ByteBufCodecs.FLOAT, Head::blue,
                ByteBufCodecs.FLOAT, Head::alpha,
                Head::new
        );
    }

    private record Tail(float airDiffusionSpeed, float duration, boolean emissive, boolean canCollide, boolean isAnimation) {
        static final StreamCodec<ByteBuf, Tail> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Tail::airDiffusionSpeed,
                ByteBufCodecs.FLOAT, Tail::duration,
                ByteBufCodecs.BOOL, Tail::emissive,
                ByteBufCodecs.BOOL, Tail::canCollide,
                ByteBufCodecs.BOOL, Tail::isAnimation,
                Tail::new
        );
    }

    public static StreamCodec<RegistryFriendlyByteBuf, AdvancedParticleData> streamCodec(ParticleType<AdvancedParticleData> particleType) {
        return StreamCodec.composite(
                Head.STREAM_CODEC, data -> new Head((float) data.getScale(), (float) data.getRed(), (float) data.getGreen(), (float) data.getBlue(), (float) data.getAlpha()),
                Tail.STREAM_CODEC, data -> new Tail((float) data.getAirDiffusionSpeed(), (float) data.getDuration(), data.isEmissive(), data.getCanCollide(), data.isAnimation()),
                (head, tail) -> new AdvancedParticleData(particleType, new ParticleRotation.FaceCamera(0), head.scale(), head.red(), head.green(), head.blue(), head.alpha(),
                        tail.airDiffusionSpeed(), tail.duration(), tail.emissive(), tail.canCollide(), new ParticleComponent[]{}, tail.isAnimation())
        );
    }
}
