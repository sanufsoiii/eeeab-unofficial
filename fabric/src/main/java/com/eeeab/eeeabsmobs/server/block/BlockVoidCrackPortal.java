package com.eeeab.eeeabsmobs.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockVoidCrackPortal extends Block {
    public BlockVoidCrackPortal(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.world.level.ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, net.minecraft.util.RandomSource random) {
        if (!level.isClientSide() && direction.getAxis().isHorizontal()) {
            //if (!PortalStructureHelper.isPortalStructureComplete(level, pos)) {
            //    return Blocks.AIR.defaultBlockState();
            //}
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    //@Override
    //public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    //    if (!level.isClientSide() && entity.canChangeDimensions() && EntitySelector.NO_SPECTATORS.test(entity)) {
    //        if (entity.isOnPortalCooldown()) {
    //            entity.setPortalCooldown();
    //        } else {
    //            entity.setPortalCooldown();
    //            ServerLevel serverLevel = (ServerLevel) level;
    //            ResourceKey<Level> destinationKey = level.dimension() == ModResourceKey.VOID_CRACK_LEVEL ? Level.OVERWORLD : ModResourceKey.VOID_CRACK_LEVEL;
    //            ServerLevel destinationLevel = serverLevel.getServer().getLevel(destinationKey);
    //            if (destinationLevel != null) {
    //                entity.changeDimension(destinationLevel, new PortalTeleporter());
    //            }
    //        }
    //    }
    //}

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        //if (random.nextInt(100) == 0) {
        //    world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
        //            SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F,
        //            random.nextFloat() * 0.4F + 0.8F, false);
        //}
        //for (int i = 0; i < 2; i++) {
        //    double dx = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
        //    double dy = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
        //    double dz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
        //    double vx = (random.nextDouble() - 0.5) * 0.1;
        //    double vy = random.nextDouble() * 0.1;
        //    double vz = (random.nextDouble() - 0.5) * 0.1;
        //
        //    world.addParticle(ParticleTypes.PORTAL, dx, dy, dz, vx, vy, vz);
        //}
    }
}
