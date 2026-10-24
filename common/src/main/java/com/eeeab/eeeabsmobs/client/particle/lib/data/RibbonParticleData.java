package com.eeeab.eeeabsmobs.client.particle.lib.data;

import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRotation;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

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

    public int getLength() {
        return this.length;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        super.writeToNetwork(buffer);
        buffer.writeVarInt(this.length);
    }

    @Override
    public String writeToString() {
        return super.writeToString() + " " + this.length;
    }

    public static final ParticleOptions.Deserializer<RibbonParticleData> DESERIALIZER = new ParticleOptions.Deserializer<RibbonParticleData>() {
        @Override
        public RibbonParticleData fromCommand(ParticleType<RibbonParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float a = reader.readFloat();
            reader.expect(' ');
            float airDiffusionSpeed = reader.readFloat();
            reader.expect(' ');
            float duration = reader.readFloat();
            reader.expect(' ');
            boolean emissive = reader.readBoolean();
            reader.expect(' ');
            boolean canCollide = reader.readBoolean();
            reader.expect(' ');
            boolean isAnimation = reader.readBoolean();
            reader.expect(' ');
            int length = reader.readInt();
            return new RibbonParticleData(type, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, airDiffusionSpeed, duration, emissive, length, new ParticleComponent[]{}, isAnimation);
        }

        @Override
        public RibbonParticleData fromNetwork(ParticleType<RibbonParticleData> type, FriendlyByteBuf buffer) {
            float scale = buffer.readFloat();
            float r = buffer.readFloat();
            float g = buffer.readFloat();
            float b = buffer.readFloat();
            float a = buffer.readFloat();
            float airDiffusionSpeed = buffer.readFloat();
            float duration = buffer.readFloat();
            boolean emissive = buffer.readBoolean();
            boolean canCollide = buffer.readBoolean();
            boolean isAnimation = buffer.readBoolean();
            int length = buffer.readVarInt();
            return new RibbonParticleData(type, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, airDiffusionSpeed, duration, emissive, length, new ParticleComponent[]{}, isAnimation);
        }
    };

    public static Codec<RibbonParticleData> codecRibbon(ParticleType<RibbonParticleData> particleType) {
        return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
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
}
