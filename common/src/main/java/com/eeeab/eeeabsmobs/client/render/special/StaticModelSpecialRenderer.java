package com.eeeab.eeeabsmobs.client.render.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

import java.util.Set;

public class StaticModelSpecialRenderer implements NoDataSpecialModelRenderer {
    private final ModelPart root;
    private final ResourceLocation texture;

    public StaticModelSpecialRenderer(ModelPart root, ResourceLocation texture) {
        this.root = root;
        this.texture = texture;
    }

    private static void applyTransform(PoseStack poseStack) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.5F, -0.5F, -0.5F);
    }

    @Override
    public void render(ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, boolean hasFoil) {
        RenderType renderType = RenderType.armorCutoutNoCull(this.texture);
        poseStack.pushPose();
        applyTransform(poseStack);
        this.root.render(poseStack, buffer.getBuffer(renderType), light, overlay);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> output) {
        PoseStack poseStack = new PoseStack();
        applyTransform(poseStack);
        this.root.getExtentsForGui(poseStack, output);
    }

    public record Unbaked(ModelLayerLocation layer, ResourceLocation texture) implements SpecialModelRenderer.Unbaked {
        public MapCodec<StaticModelSpecialRenderer.Unbaked> codec() {
            return MapCodec.unit(this);
        }

        @Override
        public MapCodec<StaticModelSpecialRenderer.Unbaked> type() {
            return codec();
        }

        @Override
        public SpecialModelRenderer<?> bake(net.minecraft.client.model.geom.EntityModelSet entityModelSet) {
            return new StaticModelSpecialRenderer(entityModelSet.bakeLayer(this.layer), this.texture);
        }
    }
}
