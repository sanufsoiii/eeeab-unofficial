package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.block.*;
import com.eeeab.eeeabsmobs.server.block.grower.BlightedOakGrower;
import com.eeeab.eeeabsmobs.server.block.properties.SGACharacter;
import com.eeeab.eeeabsmobs.server.block.trapImpl.*;
import com.eeeab.eeeabsmobs.server.item.util.ModBlockEntityItemRender;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.EnumSet;
import java.util.function.Supplier;

public class BlockInit {
    public static final FabricDeferredRegister<Block> BLOCKS = FabricDeferredRegister.create(BuiltInRegistries.BLOCK, EEEABMobs.MOD_ID);

    public static final FabricDeferredRegister.Entry<Block> DUNGEON_BRICK = registryBlock("dungeon_brick", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.BEDROCK).setId(key).noLootTable()), false);

    public static final FabricDeferredRegister.Entry<Block> IRON_GRATE = registryBlock("iron_grate", key -> new BlockIronGrate(BlockBehaviour.Properties
            .of().setId(key).strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion().isValidSpawn(BlockInit::never)
            .isRedstoneConductor(BlockInit::never).isSuffocating(BlockInit::never).isViewBlocking(BlockInit::never)
    ), false);
    public static final FabricDeferredRegister.Entry<Block> BOUNDARY_LAMP = registryBlock("boundary_lamp", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.REDSTONE_LAMP).setId(key).lightLevel((blockState) -> 15)), false);
    public static final FabricDeferredRegister.Entry<Block> ANCIENT_BOUNDARY_STONE = registryBlock("ancient_boundary_stone", key -> new DropExperienceBlock(UniformInt.of(1, 2), BlockBehaviour.Properties
            .ofFullCopy(Blocks.COBBLED_DEEPSLATE).setId(key).strength(4F, 6F).requiresCorrectToolForDrops()), false);

    public static final FabricDeferredRegister.Entry<Block> POLISHED_ROUGH_BOUNDARY_STONE = registryBlock("polished_rough_boundary_stone", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.ANCIENT_BOUNDARY_STONE.get()).setId(key).strength(4F, 6.0F).sound(SoundType.DEEPSLATE_BRICKS)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_ROUGH_BOUNDARY_STONE_STAIRS = registryBlock("polished_rough_boundary_stone_stairs", key -> new StairBlock(POLISHED_ROUGH_BOUNDARY_STONE.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(POLISHED_ROUGH_BOUNDARY_STONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_ROUGH_BOUNDARY_STONE_SLAB = registryBlock("polished_rough_boundary_stone_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(POLISHED_ROUGH_BOUNDARY_STONE.get()).setId(key)), false);

    public static final FabricDeferredRegister.Entry<Block> ROUGH_BOUNDARY_BRICKS = registryBlock("rough_boundary_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.POLISHED_ROUGH_BOUNDARY_STONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> CRACKED_ROUGH_BOUNDARY_BRICKS = registryBlock("cracked_rough_boundary_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.ROUGH_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> CHISELED_ROUGH_BOUNDARY_BRICKS = registryBlock("chiseled_rough_boundary_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.ROUGH_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> ROUGH_BOUNDARY_STONE_PILLAR = registryBlock("rough_boundary_stone_pillar", key -> new BlockRotatedPillar(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.ROUGH_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> ROUGH_BOUNDARY_BRICK_STAIRS = registryBlock("rough_boundary_brick_stairs", key -> new StairBlock(ROUGH_BOUNDARY_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(ROUGH_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> ROUGH_BOUNDARY_BRICK_SLAB = registryBlock("rough_boundary_brick_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(ROUGH_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> ROUGH_BOUNDARY_BRICK_WALL = registryBlock("rough_boundary_brick_wall", key -> new WallBlock(BlockBehaviour.Properties
            .ofFullCopy(ROUGH_BOUNDARY_BRICKS.get()).setId(key)), false);

    public static final FabricDeferredRegister.Entry<Block> POLISHED_BOUNDARY_STONE = registryBlock("polished_boundary_stone", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.ROUGH_BOUNDARY_BRICKS.get()).setId(key).strength(4.0F, 6.0F)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_BOUNDARY_STONE_STAIRS = registryBlock("polished_boundary_stone_stairs", key -> new StairBlock(ROUGH_BOUNDARY_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(POLISHED_BOUNDARY_STONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_BOUNDARY_STONE_SLAB = registryBlock("polished_boundary_stone_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(POLISHED_BOUNDARY_STONE.get()).setId(key)), false);

    public static final FabricDeferredRegister.Entry<Block> POLISHED_BOUNDARY_BRICKS = registryBlock("polished_boundary_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.POLISHED_BOUNDARY_STONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> CRACKED_POLISHED_BOUNDARY_BRICKS = registryBlock("cracked_polished_boundary_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.POLISHED_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> CHISELED_POLISHED_BOUNDARY_BRICKS = registryBlock("chiseled_polished_boundary_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.POLISHED_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_BOUNDARY_STONE_PILLAR = registryBlock("polished_boundary_stone_pillar", key -> new BlockRotatedPillar(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.POLISHED_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_BOUNDARY_BRICK_STAIRS = registryBlock("polished_boundary_brick_stairs", key -> new StairBlock(POLISHED_BOUNDARY_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(POLISHED_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_BOUNDARY_BRICK_SLAB = registryBlock("polished_boundary_brick_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(POLISHED_BOUNDARY_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_BOUNDARY_BRICK_WALL = registryBlock("polished_boundary_brick_wall", key -> new WallBlock(BlockBehaviour.Properties
            .ofFullCopy(POLISHED_BOUNDARY_BRICKS.get()).setId(key)), false);

    public static final FabricDeferredRegister.Entry<Block> REDSTONE_POISON_DART_TRAP = registryBlock("redstone_poison_dart_trap", key -> new BlockTomeArrowsTrap(BlockBehaviour.Properties
            .ofFullCopy(Blocks.DEEPSLATE_BRICKS).setId(key).strength(50.0F, 1200.0F)), false);
    public static final FabricDeferredRegister.Entry<Block> STEPPING_FLAME_TRAP = registryBlock("stepping_flame_trap", key -> new BlockSteppingFlameTrap(BlockBehaviour.Properties
            .ofFullCopy(Blocks.DEEPSLATE_BRICKS).setId(key).strength(50.0F, 1200.0F).randomTicks().lightLevel(state -> state.getValue(BlockStateProperties.OPEN) ? 7 : 0)), false);
    public static final FabricDeferredRegister.Entry<Block> STEPPING_SHOCK_TRAP = registryBlock("stepping_shock_trap", key -> new BlockSteppingShockTrap(BlockBehaviour.Properties
            .ofFullCopy(Blocks.DEEPSLATE_BRICKS).setId(key).strength(50.0F, 1200.0F).randomTicks().lightLevel(state -> state.getValue(BlockStateProperties.OPEN) ? 2 : 0)), false);
    // Block gained an abstract codec() (MapCodec<? extends Block>) in 1.20.5+ for block-state (de)serialization,
    // so every Block subclass - including anonymous ones - must implement it now. This block isn't meant to be
    // data-driven, so the codec just always yields this exact instance regardless of what Properties it's asked
    // to build with, matching how vanilla's own one-off anonymous blocks (e.g. some flower pots) do it.
    public static final FabricDeferredRegister.Entry<Block> UNCARVED_BOUNDARY_STONE = registryBlock("uncarved_boundary_stone", key -> new HorizontalDirectionalBlock(BlockBehaviour.Properties
            .ofFullCopy(Blocks.REINFORCED_DEEPSLATE).setId(key).strength(-1.0F, 3600000.0F).noLootTable().lightLevel(value -> 3).sound(SoundType.STONE)) {
        @Override
        public com.mojang.serialization.MapCodec<? extends HorizontalDirectionalBlock> codec() {
            return simpleCodec(properties -> this);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext pContext) {
            return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
            pBuilder.add(FACING);
        }
    }, false);
    public static final FabricDeferredRegister.Entry<Block> RUNIC_BOUNDARY_STONE = registryBlock("runic_boundary_stone", key -> new BlockGalacticAlphabet(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.UNCARVED_BOUNDARY_STONE.get()).setId(key).noLootTable(), EnumSet.of(SGACharacter.V, SGACharacter.O, SGACharacter.I, SGACharacter.D)), false);
    public static final FabricDeferredRegister.Entry<Block> BOUNDARY_CORE = registryBlock("boundary_core", key -> new BlockVoidCrackPortalLock(BlockBehaviour.Properties.of().setId(key)
            .randomTicks().pushReaction(PushReaction.BLOCK).strength(-1.0F).sound(SoundType.STONE).lightLevel(s -> 3).noLootTable()), false);
    public static final FabricDeferredRegister.Entry<Block> VOID_CRACK_PORTAL = registryBlock("void_crack_portal", key -> new BlockVoidCrackPortal(BlockBehaviour.Properties.of().setId(key)
            .noCollission().randomTicks().pushReaction(PushReaction.BLOCK).strength(-1.0F).isValidSpawn(BlockInit::never).sound(SoundType.GLASS).lightLevel(s -> 11).noLootTable()), false);
    public static final FabricDeferredRegister.Entry<Block> EROSION_DEEPSLATE_BRICKS = registryBlock("erosion_deepslate_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.DEEPSLATE_BRICKS).setId(key).lightLevel(s -> 7).noLootTable()), false);

    public static final FabricDeferredRegister.Entry<Block> SLIDING_DOOR = registryBlock("sliding_door", key -> new BlockSlidingDoor(BlockBehaviour.Properties.of().setId(key)), true);
    public static final FabricDeferredRegister.Entry<Block> TOMBSTONE = registryBlock("tombstone", key -> new BlockTombstone(BlockBehaviour.Properties.of().setId(key)), true);
    public static final FabricDeferredRegister.Entry<Block> EROSION_ROCK_BRICKS = registryBlock("erosion_rock_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.DEEPSLATE_BRICKS).setId(key).strength(10.0F, 1200.0F)), false);
    public static final FabricDeferredRegister.Entry<Block> IMMORTAL_BLOCK = registryBlock("immortal_block", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.IRON_BLOCK).setId(key).sound(SoundType.METAL)), false);
    public static final FabricDeferredRegister.Entry<Block> GHOST_STEEL_BLOCK = registryBlock("ghost_steel_block", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.NETHERITE_BLOCK).setId(key).sound(SoundType.METAL)), false, new Item.Properties().rarity(Rarity.EPIC).fireResistant());
    public static final FabricDeferredRegister.Entry<Block> CUT_GHOST_STEEL_BLOCK = registryBlock("cut_ghost_steel", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(BlockInit.GHOST_STEEL_BLOCK.get()).setId(key)), false, new Item.Properties().rarity(Rarity.EPIC).fireResistant());
    public static final FabricDeferredRegister.Entry<Block> CUT_GHOST_STEEL_BLOCK_STAIRS = registryBlock("cut_ghost_steel_stairs", key -> new StairBlock(CUT_GHOST_STEEL_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(CUT_GHOST_STEEL_BLOCK.get()).setId(key)), false, new Item.Properties().rarity(Rarity.EPIC).fireResistant());
    public static final FabricDeferredRegister.Entry<Block> CUT_GHOST_STEEL_BLOCK_SLAB = registryBlock("cut_ghost_steel_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(CUT_GHOST_STEEL_BLOCK.get()).setId(key)), false, new Item.Properties().rarity(Rarity.EPIC).fireResistant());
    public static final FabricDeferredRegister.Entry<Block> ERODED_SOIL = registryBlock("eroded_soil", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.MUD).setId(key).sound(SoundType.MUD)), false);
    public static final FabricDeferredRegister.Entry<Block> DARKENED_COAL_ORE = registryBlock("darkened_coal_ore", key -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties
            .ofFullCopy(Blocks.COAL_ORE).setId(key)), false);
    // DropExperienceBlock now requires an explicit IntProvider (no more implicit-zero constructor); ConstantInt.of(0)
    // reproduces the old "block exists, drop no XP by default" behaviour verbatim.
    public static final FabricDeferredRegister.Entry<Block> DARKENED_IRON_ORE = registryBlock("darkened_iron_ore", key -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties
            .ofFullCopy(Blocks.IRON_ORE).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_STONE = registryBlock("blighted_stone", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.STONE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_STONE_STAIRS = registryBlock("blighted_stone_stairs", key -> new StairBlock(BLIGHTED_STONE.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_STONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_STONE_SLAB = registryBlock("blighted_stone_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_STONE.get()).setId(key)), false);
    // PressurePlateBlock.Sensitivity was removed; PressurePlateBlock is now (BlockSetType, Properties) only.
    // ButtonBlock dropped its trailing "arrows can press" boolean and is now (BlockSetType, int tickDelay, Properties).
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_STONE_PRESSURE_PLATE = registryBlock("blighted_stone_pressure_plate", key -> new PressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_STONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_STONE_BUTTON = registryBlock("blighted_stone_button", key -> new ButtonBlock(BlockSetType.STONE, 20, BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_STONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_COBBLESTONE = registryBlock("blighted_cobblestone", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.COBBLESTONE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_COBBLESTONE_STAIRS = registryBlock("blighted_cobblestone_stairs", key -> new StairBlock(BLIGHTED_COBBLESTONE.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_COBBLESTONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_COBBLESTONE_SLAB = registryBlock("blighted_cobblestone_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_COBBLESTONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_COBBLESTONE_WALL = registryBlock("blighted_cobblestone_wall", key -> new WallBlock(BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_COBBLESTONE.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> VOIDSHARD = registryBlock("voidshard", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.GRANITE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> VOIDSHARD_STAIRS = registryBlock("voidshard_stairs", key -> new StairBlock(VOIDSHARD.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(VOIDSHARD.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> VOIDSHARD_SLAB = registryBlock("voidshard_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(VOIDSHARD.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> VOIDSHARD_WALL = registryBlock("voidshard_wall", key -> new WallBlock(BlockBehaviour.Properties
            .ofFullCopy(VOIDSHARD.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_VOIDSHARD = registryBlock("polished_voidshard", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.POLISHED_GRANITE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_VOIDSHARD_STAIRS = registryBlock("polished_voidshard_stairs", key -> new StairBlock(POLISHED_VOIDSHARD.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(POLISHED_VOIDSHARD.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_VOIDSHARD_SLAB = registryBlock("polished_voidshard_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(POLISHED_VOIDSHARD.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> DARK_EROSION_ROCK = registryBlock("dark_erosion_rock", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.DEEPSLATE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_DARK_EROSION_ROCK = registryBlock("polished_dark_erosion_rock", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.COBBLED_DEEPSLATE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_DARK_EROSION_ROCK_STAIRS = registryBlock("polished_dark_erosion_rock_stairs", key -> new StairBlock(POLISHED_DARK_EROSION_ROCK.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(POLISHED_DARK_EROSION_ROCK.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_DARK_EROSION_ROCK_SLAB = registryBlock("polished_dark_erosion_rock_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(POLISHED_DARK_EROSION_ROCK.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> POLISHED_DARK_EROSION_ROCK_WALL = registryBlock("polished_dark_erosion_rock_wall", key -> new WallBlock(BlockBehaviour.Properties
            .ofFullCopy(POLISHED_DARK_EROSION_ROCK.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> CHISELED_DARK_EROSION_ROCK = registryBlock("chiseled_dark_erosion_rock", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.COBBLED_DEEPSLATE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> DARK_EROSION_ROCK_BRICKS = registryBlock("dark_erosion_rock_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.COBBLED_DEEPSLATE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> DARK_EROSION_ROCK_PILLAR = registryBlock("dark_erosion_rock_pillar", key -> new BlockRotatedPillar(BlockBehaviour.Properties
            .ofFullCopy(Blocks.COBBLED_DEEPSLATE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> CRACKED_DARK_EROSION_ROCK_BRICKS = registryBlock("cracked_dark_erosion_rock_bricks", key -> new Block(BlockBehaviour.Properties
            .ofFullCopy(Blocks.COBBLED_DEEPSLATE).setId(key).sound(SoundType.STONE)), false);
    public static final FabricDeferredRegister.Entry<Block> DARK_EROSION_ROCK_BRICKS_STAIRS = registryBlock("dark_erosion_rock_bricks_stairs", key -> new StairBlock(DARK_EROSION_ROCK_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(DARK_EROSION_ROCK_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> DARK_EROSION_ROCK_BRICKS_SLAB = registryBlock("dark_erosion_rock_bricks_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(DARK_EROSION_ROCK_BRICKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> DARK_EROSION_ROCK_BRICKS_WALL = registryBlock("dark_erosion_rock_bricks_wall", key -> new WallBlock(BlockBehaviour.Properties
            .ofFullCopy(DARK_EROSION_ROCK_BRICKS.get()).setId(key)), false);

    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_LOG = registryBlock("blighted_oak_log", key -> new BlockRotatedPillar(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> STRIPPED_BLIGHTED_OAK_LOG = registryBlock("stripped_blighted_oak", key -> new BlockRotatedPillar(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).setId(key), 5, 5), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_WOOD = registryBlock("blighted_oak_wood", key -> new BlockRotatedPillar(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> STRIPPED_BLIGHTED_OAK_WOOD = registryBlock("stripped_blighted_oak_wood", key -> new BlockRotatedPillar(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).setId(key), 5, 5), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_SAPLING = registryBlock("blighted_oak_sapling", key -> new SaplingBlock(BlightedOakGrower.BLIGHTED_OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_PLANKS = registryBlock("blighted_oak_planks", key -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_STAIRS = registryBlock("blighted_oak_stairs", key -> new StairBlock(BLIGHTED_OAK_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_OAK_PLANKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_SLAB = registryBlock("blighted_oak_slab", key -> new SlabBlock(BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_OAK_PLANKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_FENCE = registryBlock("blighted_oak_fence", key -> new FenceBlock(BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_OAK_PLANKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_FENCE_GATE = registryBlock("blighted_oak_fence_gate", key -> new FenceGateBlock(WoodType.OAK, BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_OAK_PLANKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_DOOR = registryBlock("blighted_oak_door", key -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_OAK_PLANKS.get()).setId(key).noOcclusion()), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_TRAPDOOR = registryBlock("blighted_oak_trapdoor", key -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_OAK_PLANKS.get()).setId(key).noOcclusion()), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_PRESSURE_PLATE = registryBlock("blighted_oak_pressure_plate", key -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_OAK_PLANKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> BLIGHTED_OAK_BUTTON = registryBlock("blighted_oak_button", key -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties
            .ofFullCopy(BLIGHTED_OAK_PLANKS.get()).setId(key)), false);
    public static final FabricDeferredRegister.Entry<Block> STEPPING_POISON_TRAP = registryBlock("stepping_poison_trap", key -> new BlockSteppingPoisonTrap(BlockBehaviour.Properties
            .ofFullCopy(Blocks.DEEPSLATE_BRICKS).setId(key).randomTicks().strength(50.0F, 1200.0F)), false);
    public static final FabricDeferredRegister.Entry<Block> STEPPING_SKELETON_TRAP = registryBlock("stepping_skeleton_trap", key -> new BlockSteppingSkeletonTrap(BlockBehaviour.Properties
            .ofFullCopy(Blocks.DEEPSLATE_BRICKS).setId(key).randomTicks().strength(50.0F, 1200.0F).lightLevel(state -> state.getValue(BlockStateProperties.OPEN) ? 2 : 0)), false);

    private static boolean always(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    private static boolean always(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entityType) {
        return true;
    }

    private static Boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entityType) {
        return false;
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    private static <T extends Block> FabricDeferredRegister.Entry<T> registryBlock(String name, java.util.function.Function<net.minecraft.resources.ResourceKey<Block>, T> block, boolean isEntity) {
        return registryBlock(name, block, isEntity, new Item.Properties());
    }

    private static <T extends Block> FabricDeferredRegister.Entry<T> registryBlock(String name, java.util.function.Function<net.minecraft.resources.ResourceKey<Block>, T> block, boolean isEntity, Item.Properties properties) {
        net.minecraft.resources.ResourceKey<Block> key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, name));
        FabricDeferredRegister.Entry<T> register = BLOCKS.register(name, () -> block.apply(key));
        registerBlockItem(name, register, properties, isEntity);
        return register;
    }

    private static <T extends Block> void registerBlockItem(String name, FabricDeferredRegister.Entry<T> block, Item.Properties properties, boolean isEntityBlock) {
        net.minecraft.resources.ResourceKey<net.minecraft.world.item.Item> itemKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, name));
        // 1.21.11: BlockItem no longer overrides getDescriptionId(); the key is baked from Item.Properties and
        // defaults to item.<ns>.<name>. Block items must opt into the block.* prefix to match the lang file.
        properties.setId(itemKey).useBlockDescriptionPrefix();
        ItemInit.ITEMS.register(name, () -> isEntityBlock ? new ModBlockEntityItemRender(block.get(), properties) : new BlockItem(block.get(), properties));
    }

    public static void register(Object bus) {
        BLOCKS.register(bus);
        // Was BlockRotatedPillar#getToolModifiedState overriding NeoForge's ItemAbility-based tool-modification
        // hook (getToolModifiedState/ItemAbilities.AXE_STRIP), which has no Fabric equivalent - vanilla's
        // AxeItem instead consults a static stripped-block map that Fabric exposes as StrippableBlockRegistry
        // (verified with javap against fabric-content-registries-v0: register(Block, Block)). Registering here
        // reproduces the same axe-stripping behaviour.
        com.eeeab.eeeabsmobs.platform.PlatformServices.get().registerStrippable(BLIGHTED_OAK_LOG.get(), STRIPPED_BLIGHTED_OAK_LOG.get());
        com.eeeab.eeeabsmobs.platform.PlatformServices.get().registerStrippable(BLIGHTED_OAK_WOOD.get(), STRIPPED_BLIGHTED_OAK_WOOD.get());
    }
}
