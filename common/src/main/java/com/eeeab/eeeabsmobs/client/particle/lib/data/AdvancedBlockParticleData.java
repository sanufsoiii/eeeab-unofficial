package com.eeeab.eeeabsmobs.client.particle.lib.data;

import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRotation;
import com.eeeab.eeeabsmobs.server.init.ParticleInit;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;

public class AdvancedBlockParticleData extends AdvancedParticleData {
    private final BlockState state;
    private final BlockPos pos;

    public AdvancedBlockParticleData(double scale, double r, double g, double b, double a,
                                     double airDiffusionSpeed, double duration, boolean emissive, boolean canCollide,
                                     ParticleComponent[] components, BlockState state, BlockPos pos) {
        super(ParticleInit.BLOCK.get(), new ParticleRotation.FaceCamera((float) 0), scale, r, g, b, a, airDiffusionSpeed, duration,
                emissive, canCollide, components, false);
        this.state = state;
        this.pos = pos;
    }

    public BlockState getState() {
        return state;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        super.writeToNetwork(buffer);
        buffer.writeId(Block.BLOCK_STATE_REGISTRY, this.state);
        buffer.writeBlockPos(this.pos);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %s %s %s %s %s %s %s %s %s %s %s %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.scale, this.red, this.green, this.blue, this.alpha,
                this.airDiffusionSpeed, this.duration, this.emissive, this.canCollide, this.isAnimation,
                BlockStateParser.serialize(this.state), this.pos.asLong());
    }

    public static final ParticleOptions.Deserializer<AdvancedBlockParticleData> DESERIALIZER = new ParticleOptions.Deserializer<AdvancedBlockParticleData>() {
        @Override
        public AdvancedBlockParticleData fromCommand(ParticleType<AdvancedBlockParticleData> type, StringReader reader) throws CommandSyntaxException {
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
            BlockStateParser.BlockResult blockResult = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), reader, false);
            reader.expect(' ');
            long posLong = reader.readLong();
            return new AdvancedBlockParticleData(scale, r, g, b, a, airDiffusionSpeed, duration, emissive, canCollide, new ParticleComponent[]{}, blockResult.blockState(), BlockPos.of(posLong));
        }

        @Override
        public AdvancedBlockParticleData fromNetwork(ParticleType<AdvancedBlockParticleData> type, FriendlyByteBuf buffer) {
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
            BlockState state = buffer.readById(Block.BLOCK_STATE_REGISTRY);
            BlockPos pos = buffer.readBlockPos();
            return new AdvancedBlockParticleData(scale, r, g, b, a, airDiffusionSpeed, duration, emissive, canCollide, new ParticleComponent[]{}, state, pos);
        }
    };

    public static Codec<AdvancedBlockParticleData> codecBlock(ParticleType<AdvancedBlockParticleData> particleType) {
        return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                Codec.DOUBLE.fieldOf("scale").forGetter(AdvancedBlockParticleData::getScale),
                Codec.DOUBLE.fieldOf("r").forGetter(AdvancedBlockParticleData::getRed),
                Codec.DOUBLE.fieldOf("g").forGetter(AdvancedBlockParticleData::getGreen),
                Codec.DOUBLE.fieldOf("b").forGetter(AdvancedBlockParticleData::getBlue),
                Codec.DOUBLE.fieldOf("a").forGetter(AdvancedBlockParticleData::getAlpha),
                Codec.DOUBLE.fieldOf("airDiffusionSpeed").forGetter(AdvancedBlockParticleData::getAirDiffusionSpeed),
                Codec.DOUBLE.fieldOf("duration").forGetter(AdvancedBlockParticleData::getDuration),
                Codec.BOOL.fieldOf("emissive").forGetter(AdvancedBlockParticleData::isEmissive),
                Codec.BOOL.fieldOf("canCollide").forGetter(AdvancedParticleData::getCanCollide),
                BlockState.CODEC.fieldOf("getState").forGetter(AdvancedBlockParticleData::getState),
                BlockPos.CODEC.fieldOf("getPos").forGetter(AdvancedBlockParticleData::getPos)
        ).apply(codecBuilder, (scale, r, g, b, a, drag, duration, emissive, canCollide, state, pos) ->
                new AdvancedBlockParticleData(scale, r, g, b, a, drag, duration, emissive, canCollide, new ParticleComponent[]{}, state, pos))
        );
    }
}