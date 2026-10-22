package com.eeeab.eeeabsmobs.client.render.block;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.block.SlidingDoorModel;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.block.BlockSlidingDoor;
import com.eeeab.eeeabsmobs.server.entity.block.EntitySlidingDoorBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;

public class RenderSlidingDoor implements BlockEntityRenderer<EntitySlidingDoorBlock, SlidingDoorRenderState> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/block/sliding_door.png");
    private static final ResourceLocation UNLOCK_TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/block/sliding_door_unlock.png");
    private final SlidingDoorModel model;

    public RenderSlidingDoor(BlockEntityRendererProvider.Context context) {
        model = new SlidingDoorModel(context.bakeLayer(ModModelLayer.SLIDING_DOOR));
    }

    @Override
    public SlidingDoorRenderState createRenderState() {
        return new SlidingDoorRenderState();
    }

    @Override
    public void extractRenderState(EntitySlidingDoorBlock blockEntity, SlidingDoorRenderState state, float partialTick, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.ageInTicks = blockEntity.tickCount + partialTick;
        state.openingAnimationState = blockEntity.openingAnimationState;
        state.closingAnimationState = blockEntity.closingAnimationState;
    }

    @Override
    public void submit(SlidingDoorRenderState state, PoseStack poseStack, SubmitNodeCollector buffer, CameraRenderState cameraRenderState) {
        BlockState blockState = state.blockState;
        if (!(blockState.getBlock() instanceof BlockSlidingDoor)) return;
        if (blockState.getValue(BlockSlidingDoor.PART) == BlockSlidingDoor.Part.CENTER && blockState.getValue(BlockSlidingDoor.Y_OFFSET) == 0) {
            poseStack.pushPose();
            Direction facing = blockState.getValue(BlockSlidingDoor.FACING);
            boolean locked = blockState.getValue(BlockSlidingDoor.LOCKED);
            if (!(facing == Direction.UP || facing == Direction.DOWN)) {
                poseStack.translate(0.5F, 1.501F, 0.5F);
            }
            poseStack.mulPose(facing.getRotation());
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            model.animate(state.ageInTicks, state.openingAnimationState, state.closingAnimationState);
            buffer.submitModelPart(model.root(), poseStack, RenderType.entityCutoutNoCull(locked ? TEXTURE : UNLOCK_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null, false, false, -1, null, 0);
            poseStack.popPose();
        }
    }
}
