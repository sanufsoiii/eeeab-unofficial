package com.eeeab.eeeabsmobs.server.block.trapImpl;

import com.eeeab.eeeabsmobs.server.block.BlockSignalTarp;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityPoisonArrow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTomeArrowsTrap extends BlockSignalTarp {
    public BlockTomeArrowsTrap(Properties properties) {
        super(properties, 20);
    }

    @Override
    protected void active(BlockState state, ServerLevel level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        // NeoForge removed BlockSourceImpl; DispenserBlock.getDispensePosition now requires a BlockSource tied to
        // a real DispenserBlockEntity, which this trap block doesn't have. Replicated its 0.7-block offset math
        // directly instead (see DispenserBlock#getDispensePosition in vanilla source).
        Position position = pos.getCenter().add(
                0.7 * direction.getStepX(),
                0.7 * direction.getStepY(),
                0.7 * direction.getStepZ()
        );
        EntityPoisonArrow arrow = new EntityPoisonArrow(level, position);
        arrow.shoot(direction.getStepX(), direction.getStepY() + 0.1D, direction.getStepZ(), 1.5F, 1);
        level.addFreshEntity(arrow);
        level.setBlock(pos, state.setValue(LIT, true), 2);
        level.scheduleTick(pos, this, this.interval);
        level.levelEvent(1002, pos, 0);
        level.levelEvent(2000, pos, direction.get3DDataValue());
    }
}
