package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.block.EntityBlockTombstone;
import com.eeeab.eeeabsmobs.server.entity.block.EntitySlidingDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;

public class BlockEntityInit {
    public static final FabricDeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = FabricDeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, EEEABMobs.MOD_ID);

    public static final FabricDeferredRegister.Entry<BlockEntityType<EntitySlidingDoorBlock>> SLIDING_DOOR_BE = BLOCK_ENTITIES.register("sliding_door_be", () ->
            net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.<EntitySlidingDoorBlock>create(EntitySlidingDoorBlock::new, BlockInit.SLIDING_DOOR.get()).build());

    public static final FabricDeferredRegister.Entry<BlockEntityType<EntityBlockTombstone>> ENTITY_TOMBSTONE = BLOCK_ENTITIES.register("tombstone", () ->
            net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.<EntityBlockTombstone>create(EntityBlockTombstone::new, BlockInit.TOMBSTONE.get()).build());

    public static void register(Object bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
