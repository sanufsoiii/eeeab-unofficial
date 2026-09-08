package com.eeeab.eeeabsmobs.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class BlockVoidCrackPortalLock extends HorizontalDirectionalBlock {
    // HorizontalDirectionalBlock#codec() became abstract in 1.21 - same pattern as BlockSlidingDoor/BlockTombstone.
    private static final com.mojang.serialization.MapCodec<BlockVoidCrackPortalLock> CODEC = simpleCodec(BlockVoidCrackPortalLock::new);

    @Override
    protected com.mojang.serialization.MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public BlockVoidCrackPortalLock(Properties properties) {
        super(properties);
    }

    // Block#use(BlockState,Level,BlockPos,Player,InteractionHand,BlockHitResult) was split into
    // useWithoutItem(...) (no InteractionHand - the hand-carried ItemStack is handled separately via
    // useItemOn) and useItemOn(ItemStack,...). This block never used the held item, so useWithoutItem covers it.
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        //ItemStack itemStack = player.getItemInHand(hand);
        //if (itemStack.getItem() instanceof ItemGuardianCore && !player.isShiftKeyDown()) {
        //    if (!level.isClientSide()) {
        //        if (PortalStructureHelper.tryActivatePortalFromLock(level, pos)) {
        //            return InteractionResult.SUCCESS;
        //        }
        //    }
        //    return InteractionResult.sidedSuccess(level.isClientSide());
        //}
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        //if (random.nextInt(10) == 0) {
        //    double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
        //    double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
        //    double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
        //    level.addParticle(ParticleTypes.ENCHANT, x, y, z, 0, 0, 0);
        //}
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
}