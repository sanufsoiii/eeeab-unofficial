package com.eeeab.eeeabsmobs.server.block.grower;

import com.eeeab.eeeabsmobs.server.util.ModResourceKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class BlightedOakGrower extends AbstractTreeGrower {
    public static final BlightedOakGrower BLIGHTED_OAK = new BlightedOakGrower();

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean flowers) {
        return ModResourceKey.BLIGHTED_OAK;
    }
}
