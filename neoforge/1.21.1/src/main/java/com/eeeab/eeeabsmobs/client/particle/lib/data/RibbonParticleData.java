package com.eeeab.eeeabsmobs.client.particle.lib.data;

import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRotation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

//参考自: https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/util/RibbonParticleData.java
public class RibbonParticleData extends AdvancedParticleData {
    private final int length;

    public RibbonParticleData(ParticleType<? extends RibbonParticleData> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, int length, boolean isAnimation) {
        this(type, rotation, scale, r, g, b, a, drag, duration, emissive, length, new ParticleComponent[]{}, isAnimation);
    }

    public RibbonParticleData(ParticleType<? extends RibbonParticleData> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, int length, ParticleComponent[] components, boolean isAnimation) {
        super(type, rotation, scale, r, g, b, a, drag, duration, emissive, false, components, isAnimation);
        this.length = length;
    }

    @OnlyIn(Dist.CLIENT)
    public int getLength() {
        return this.length;
    }

    // See AdvancedParticleData for why writeToNetwork/writeToString/Deserializer are gone.
    public static MapCodec<RibbonParticleData> codecRibbon(ParticleType<RibbonParticleData> particleType) {
        return RecordCodecBuilder.mapCodec((codecBuilder) -> codecBuilder.group(
                        Codec.DOUBLE.fieldOf("scale").forGetter(RibbonParticleData::getScale),
                        Codec.DOUBLE.fieldOf("r").forGetter(RibbonParticleData::getRed),
                        Codec.DOUBLE.fieldOf("g").forGetter(RibbonParticleData::getGreen),
                        Codec.DOUBLE.fieldOf("b").forGetter(RibbonParticleData::getBlue),
                        Codec.DOUBLE.fieldOf("a").forGetter(RibbonParticleData::getAlpha),
                        Codec.DOUBLE.fieldOf("airDiffusionSpeed").forGetter(RibbonParticleData::getAirDiffusionSpeed),
                        Codec.DOUBLE.fieldOf("duration").forGetter(RibbonParticleData::getDuration),
                        Codec.BOOL.fieldOf("emissive").forGetter(RibbonParticleData::isEmissive),
                        Codec.INT.fieldOf("length").forGetter(RibbonParticleData::getLength),
                        Codec.BOOL.fieldOf("isAnimation").forGetter(AdvancedParticleData::isAnimation)
                ).apply(codecBuilder, (scale, r, g, b, a, drag, duration, emissive, length, isAnimation) ->
                        new RibbonParticleData(particleType, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, drag, duration, emissive, length, new ParticleComponent[]{}, isAnimation))
        );
    }

    // StreamCodec.composite tops out at 6 fields; grouped into Head (5 color/scale floats) then combined with
    // the remaining 4 fields (airDiffusionSpeed, duration, emissive, length) = 5 items total.
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

    public static StreamCodec<RegistryFriendlyByteBuf, RibbonParticleData> streamCodecRibbon(ParticleType<RibbonParticleData> particleType) {
        return StreamCodec.composite(
                Head.STREAM_CODEC, data -> new Head((float) data.getScale(), (float) data.getRed(), (float) data.getGreen(), (float) data.getBlue(), (float) data.getAlpha()),
                ByteBufCodecs.FLOAT, data -> (float) data.getAirDiffusionSpeed(),
                ByteBufCodecs.FLOAT, data -> (float) data.getDuration(),
                ByteBufCodecs.BOOL, RibbonParticleData::isEmissive,
                ByteBufCodecs.VAR_INT, RibbonParticleData::getLength,
                (head, airDiffusionSpeed, duration, emissive, length) -> new RibbonParticleData(particleType, new ParticleRotation.FaceCamera(0),
                        head.scale(), head.red(), head.green(), head.blue(), head.alpha(), airDiffusionSpeed, duration, emissive, length, new ParticleComponent[]{}, false)
        );
    }
}
