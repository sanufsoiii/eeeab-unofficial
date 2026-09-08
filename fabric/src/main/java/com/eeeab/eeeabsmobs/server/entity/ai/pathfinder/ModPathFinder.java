package com.eeeab.eeeabsmobs.server.entity.ai.pathfinder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//参考自: https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/server/ai/MMPathFinder.java
public class ModPathFinder extends net.minecraft.world.level.pathfinder.PathFinder {
    public ModPathFinder(NodeEvaluator processor, int maxVisitedNodes) {
        super(processor, maxVisitedNodes);
    }

    @Nullable
    @Override
    public Path findPath(PathNavigationRegion regionIn, Mob mob, Set<BlockPos> targetPositions, float maxRange, int accuracy, float searchDepthMultiplier) {
        // The old PatchedPath subclass offset getEntityPosAtNode by half of floor(width+1) to centre wide
        // mobs on nodes. Vanilla Path.getEntityPosAtNode now applies exactly that offset itself (and Path is
        // final in 1.21.11), so the subclass is redundant — the vanilla path already behaves identically.
        return super.findPath(regionIn, mob, targetPositions, maxRange, accuracy, searchDepthMultiplier);
    }
}
