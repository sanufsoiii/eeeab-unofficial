package com.eeeab.eeeabsmobs.client.particle.lib.data;

import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRotation;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Locale;
import net.minecraft.world.phys.Vec3;
//参考自: https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/util/AdvancedParticleData.java
public class AdvancedParticleData implements ParticleOptions {
    protected final ParticleType<? extends AdvancedParticleData> type;
    protected final float airDiffusionSpeed;
    protected final float red, green, blue, alpha;
    protected final ParticleRotation rotation;
    protected final float scale;
    protected final boolean emissive;
    protected final float duration;
    protected final boolean canCollide;
    protected final boolean isAnimation;

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

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat((float) this.scale);
        buffer.writeFloat((float) this.red);
        buffer.writeFloat((float) this.green);
        buffer.writeFloat((float) this.blue);
        buffer.writeFloat((float) this.alpha);
        buffer.writeFloat((float) this.airDiffusionSpeed);
        buffer.writeFloat((float) this.duration);
        buffer.writeBoolean(this.emissive);
        buffer.writeBoolean(this.canCollide);
        buffer.writeBoolean(this.isAnimation);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f %b %b %b",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.scale, this.red, this.green, this.blue, this.alpha,
                this.airDiffusionSpeed, this.duration, this.emissive, this.canCollide, this.isAnimation);
    }

    public static final ParticleOptions.Deserializer<AdvancedParticleData> DESERIALIZER = new ParticleOptions.Deserializer<AdvancedParticleData>() {
        @Override
        public AdvancedParticleData fromCommand(ParticleType<AdvancedParticleData> type, StringReader reader) throws CommandSyntaxException {
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
            return new AdvancedParticleData(type, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, airDiffusionSpeed, duration, emissive, canCollide, new ParticleComponent[]{}, isAnimation);
        }

        @Override
        public AdvancedParticleData fromNetwork(ParticleType<AdvancedParticleData> type, FriendlyByteBuf buffer) {
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
            return new AdvancedParticleData(type, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, airDiffusionSpeed, duration, emissive, canCollide, new ParticleComponent[]{}, isAnimation);
        }
    };

    public static Codec<AdvancedParticleData> codec(ParticleType<AdvancedParticleData> particleType) {
        return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
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
}
