package com.eeeab.eeeabsmobs.server.item.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;


public class ModBlockEntityItemRender extends BlockItem {

    public ModBlockEntityItemRender(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    // initializeClient(NeoForge-only client-extension callback) removed - BEWLR rendering for this
    // item is now wired directly in ClientProxy via Fabric API's BuiltinItemRendererRegistry.
}
