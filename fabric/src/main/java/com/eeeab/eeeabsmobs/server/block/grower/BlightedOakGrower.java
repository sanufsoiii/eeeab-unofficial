package com.eeeab.eeeabsmobs.server.block.grower;

import com.eeeab.eeeabsmobs.server.util.ModResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

/**
 * NeoForge/vanilla 1.21.1 made {@link TreeGrower} {@code final} - it is no longer subclassable, growers are
 * plain data instances registered into TreeGrower's internal name -> instance map on construction. This used
 * to be a subclass of AbstractTreeGrower overriding getConfiguredFeature(); now it is just a singleton
 * TreeGrower built from the mod's "blighted_oak" ConfiguredFeature key.
 */
public final class BlightedOakGrower {
    public static final TreeGrower BLIGHTED_OAK = new TreeGrower(
            "blighted_oak",
            Optional.empty(),
            Optional.of(ModResourceKey.BLIGHTED_OAK),
            Optional.empty()
    );

    private BlightedOakGrower() {
    }
}
