package com.eeeab.eeeabsmobs.server.block;

import com.eeeab.eeeabsmobs.platform.PlatformServices;
import net.minecraft.world.level.block.RotatedPillarBlock;

public class BlockRotatedPillar extends RotatedPillarBlock {
    public BlockRotatedPillar(Properties properties) {
        super(properties);
    }

    //可燃方块使用此构造器
    public BlockRotatedPillar(Properties properties, int flammability, int fireSpreadSpeed) {
        super(properties);
        PlatformServices.get().registerFlammable(this, flammability, fireSpreadSpeed);
    }
}
