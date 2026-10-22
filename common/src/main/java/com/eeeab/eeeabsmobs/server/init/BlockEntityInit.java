package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.block.EntityBlockTombstone;
import com.eeeab.eeeabsmobs.server.entity.block.EntitySlidingDoorBlock;
import com.eeeab.mixin.BlockEntityTypeInvoker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Set;

public class BlockEntityInit {
    public static final FabricDeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = FabricDeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, EEEABMobs.MOD_ID);

    public static final FabricDeferredRegister.Entry<BlockEntityType<EntitySlidingDoorBlock>> SLIDING_DOOR_BE = BLOCK_ENTITIES.register("sliding_door_be", () ->
            BlockEntityTypeInvoker.eeeabmobs$create(EntitySlidingDoorBlock::new, Set.of(BlockInit.SLIDING_DOOR.get())));

    public static final FabricDeferredRegister.Entry<BlockEntityType<EntityBlockTombstone>> ENTITY_TOMBSTONE = BLOCK_ENTITIES.register("tombstone", () ->
            BlockEntityTypeInvoker.eeeabmobs$create(EntityBlockTombstone::new, Set.of(BlockInit.TOMBSTONE.get())));

    public static void register(Object bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
