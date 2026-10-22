package com.eeeab.eeeabsmobs.client.particle.lib.data;

import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRotation;
import com.eeeab.eeeabsmobs.server.init.ParticleInit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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

    // See AdvancedParticleData for why writeToNetwork/writeToString/Deserializer are gone.
    public static MapCodec<AdvancedBlockParticleData> codecBlock() {
        return RecordCodecBuilder.mapCodec((codecBuilder) -> codecBuilder.group(
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
                        new AdvancedBlockParticleData( scale, r, g, b, a, drag, duration, emissive, canCollide, new ParticleComponent[]{}, state, pos))
        );
    }

    // StreamCodec.composite tops out at 6 fields; grouped into Head (5 color/scale floats), Mid (4 remaining
    // AdvancedParticleData fields), then combined with block state + pos (4 items total, within the limit).
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

    private record Mid(float airDiffusionSpeed, float duration, boolean emissive, boolean canCollide) {
        static final StreamCodec<ByteBuf, Mid> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Mid::airDiffusionSpeed,
                ByteBufCodecs.FLOAT, Mid::duration,
                ByteBufCodecs.BOOL, Mid::emissive,
                ByteBufCodecs.BOOL, Mid::canCollide,
                Mid::new
        );
    }

    public static StreamCodec<RegistryFriendlyByteBuf, AdvancedBlockParticleData> streamCodecBlock() {
        return StreamCodec.composite(
                Head.STREAM_CODEC, data -> new Head((float) data.getScale(), (float) data.getRed(), (float) data.getGreen(), (float) data.getBlue(), (float) data.getAlpha()),
                Mid.STREAM_CODEC, data -> new Mid((float) data.getAirDiffusionSpeed(), (float) data.getDuration(), data.isEmissive(), data.getCanCollide()),
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), AdvancedBlockParticleData::getState,
                BlockPos.STREAM_CODEC, AdvancedBlockParticleData::getPos,
                (head, mid, state, pos) -> new AdvancedBlockParticleData(head.scale(), head.red(), head.green(), head.blue(), head.alpha(),
                        mid.airDiffusionSpeed(), mid.duration(), mid.emissive(), mid.canCollide(), new ParticleComponent[]{}, state, pos)
        );
    }
}