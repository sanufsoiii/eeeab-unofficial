package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.block.EntityBlockTombstone;
import com.eeeab.eeeabsmobs.server.entity.block.EntitySlidingDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EEEABMobs.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntitySlidingDoorBlock>> SLIDING_DOOR_BE = BLOCK_ENTITIES.register("sliding_door_be", () ->
            BlockEntityType.Builder.<EntitySlidingDoorBlock>of(EntitySlidingDoorBlock::new, BlockInit.SLIDING_DOOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntityBlockTombstone>> ENTITY_TOMBSTONE = BLOCK_ENTITIES.register("tombstone", () ->
            BlockEntityType.Builder.<EntityBlockTombstone>of(EntityBlockTombstone::new, BlockInit.TOMBSTONE.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
