package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.client.render.state.EMFallingBlockRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityFallingBlock;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class RenderFallingBlock extends EntityRenderer<EntityFallingBlock, EMFallingBlockRenderState> {

    public RenderFallingBlock(EntityRendererProvider.Context context) {
        super(context);
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
        state.startBlockPos = BlockPos.ZERO;
        state.blockPos = blockPos;
        state.blockState = entity.getBlockState();
        state.biome = entity.level().getBiome(blockPos);
        state.level = entity.level();
    }

    @Override
    public void render(EMFallingBlockRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (ModConfigHandler.CLIENT.enableFallingBlockRender.get()) {
            BlockState blockState = state.blockState;
            if (blockState != null && blockState.getRenderShape() == RenderShape.MODEL) {
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
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
        super.render(state, poseStack, buffer, packedLight);
    }
}
