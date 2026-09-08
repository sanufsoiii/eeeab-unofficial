package com.eeeab.eeeabsmobs.server.world.level.feature;

import com.eeeab.eeeabsmobs.server.init.WorldGenInit;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class EmptyFoliagePlacer extends FoliagePlacer {
    // RecordCodecBuilder.create(...) -> Codec was replaced by .mapCodec(...) -> MapCodec for feature-type
    // codecs in 1.21 (vanilla change, not NeoForge); FoliagePlacerType now stores a MapCodec<P>.
    public static final MapCodec<EmptyFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> foliagePlacerParts(instance).apply(instance, (intProvider, intProvider2) -> new EmptyFoliagePlacer()));

    public EmptyFoliagePlacer() {
        super(ConstantInt.ZERO, ConstantInt.ZERO);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return WorldGenInit.EMPTY_LEAVE.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter blockSetter, RandomSource random, TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return 0;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        return true;
    }
}
