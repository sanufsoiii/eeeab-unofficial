package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.client.render.state.EMFallingBlockRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityFallingBlock;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class RenderFallingBlock extends EntityRenderer<EntityFallingBlock, EMFallingBlockRenderState> {

    public RenderFallingBlock(EntityRendererProvider.Context p_174112_) {
        super(p_174112_);
    }

    @Override
    public EMFallingBlockRenderState createRenderState() {
        return new EMFallingBlockRenderState();
    }

    @Override
    public void extractRenderState(EntityFallingBlock entity, EMFallingBlockRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.partialTick = partialTick;
        state.prevAnimY = entity.prevAnimY;
        state.animY = entity.animY;
        state.overallMove = entity.getMode() == EntityFallingBlock.FallingMoveType.OVERALL_MOVE;
        state.applyRuptureRotation = entity.getMode() == EntityFallingBlock.FallingMoveType.SIMULATE_RUPTURE;
        state.ruptureRotation = entity.getQuaternionf();
        BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        // NOTE: mod entity has no start-pos equivalent; keep vanilla default (BlockPos.ZERO)
        // state.movingBlockRenderState.randomSeedPos = entity.getStartPos();
        state.movingBlockRenderState.blockPos = blockPos;
        state.movingBlockRenderState.blockState = entity.getBlockState();
        state.movingBlockRenderState.biome = entity.level().getBiome(blockPos);
        state.movingBlockRenderState.level = entity.level();
    }

    @Override
    public void submit(EMFallingBlockRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (ModConfigHandler.CLIENT.enableFallingBlockRender.get()) {
            poseStack.pushPose();
            if (state.overallMove) {
                poseStack.translate(-0.5f, 0, -0.5f);
            } else {
                poseStack.translate(0, 0.5f, 0);
                poseStack.translate(0, Mth.lerp(state.partialTick, state.prevAnimY, state.animY), 0);
                if (state.applyRuptureRotation && state.ruptureRotation != null) {
                    poseStack.mulPose(state.ruptureRotation);
                }
                poseStack.translate(0, -1, 0);
                poseStack.translate(-0.5f, -0.5f, -0.5f);
            }
            collector.submitMovingBlock(poseStack, state.movingBlockRenderState);
            poseStack.popPose();
        }
        super.submit(state, poseStack, collector, cameraRenderState);
    }
}
