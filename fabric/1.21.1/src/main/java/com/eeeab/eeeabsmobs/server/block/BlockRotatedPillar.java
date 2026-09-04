package com.eeeab.eeeabsmobs.server.block;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.RotatedPillarBlock;

/**
 * Was: isFlammable/getFlammability/getFireSpreadSpeed overriding NeoForge's per-block IBlockExtension flammability
 * hooks, and getToolModifiedState overriding NeoForge's ItemAbility-based axe-stripping hook. Neither exists on
 * vanilla Block/BlockBehaviour (confirmed with javap against the Loom-mapped jar) - vanilla instead drives both
 * through static registries (FireBlock's flammable-block map, AxeItem's stripped-block map) which Fabric exposes
 * as FlammableBlockRegistry and StrippableBlockRegistry (verified with javap against fabric-content-registries-v0).
 * Flammability self-registers below since the instance already knows its own values; axe-stripping needs both
 * the source and stripped block instances so it is wired at init time in BlockInit#register instead.
 */
public class BlockRotatedPillar extends RotatedPillarBlock {
    public BlockRotatedPillar(Properties properties) {
        super(properties);
    }

    //可燃方块使用此构造器
    public BlockRotatedPillar(Properties properties, int flammability, int fireSpreadSpeed) {
        super(properties);
        FlammableBlockRegistry.getDefaultInstance().add(this, flammability, fireSpreadSpeed);
    }
}
