package com.eeeab.eeeabsmobs.client.render.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Generic special item renderer for the mod's static (non-animated) in-hand 3D models — the guardian
 * battleaxe and netherworld katana. Mirrors the old ModItemStackRender: translate(0.5,0.5,0.5) then
 * scale(0.5,-0.5,-0.5), rendered with armorCutoutNoCull(texture). Registered per item in
 * SpecialModelRenderersMixin; referenced from the item model definition by {@code minecraft:special}.
 */
@Environment(EnvType.CLIENT)
public class StaticModelSpecialRenderer implements NoDataSpecialModelRenderer {
    private final ModelPart root;
    private final Identifier texture;

    public StaticModelSpecialRenderer(ModelPart root, Identifier texture) {
        this.root = root;
        this.texture = texture;
    }

    private static void applyTransform(PoseStack poseStack) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.5F, -0.5F, -0.5F);
    }

    @Override
    public void submit(ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
        RenderType renderType = RenderTypes.armorCutoutNoCull(this.texture);
        poseStack.pushPose();
        applyTransform(poseStack);
        collector.submitModelPart(this.root, poseStack, renderType, light, overlay, null, false, hasFoil, -1, null, outlineColor);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        PoseStack poseStack = new PoseStack();
        applyTransform(poseStack);
        this.root.getExtentsForGui(poseStack, consumer);
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked(ModelLayerLocation layer, Identifier texture) implements SpecialModelRenderer.Unbaked {
        public MapCodec<StaticModelSpecialRenderer.Unbaked> codec() {
            return MapCodec.unit(this);
        }

        @Override
        public MapCodec<StaticModelSpecialRenderer.Unbaked> type() {
            return codec();
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
            // These models' render root is the "root" child of the baked layer (super(root.getChild("root"))).
            return new StaticModelSpecialRenderer(context.entityModelSet().bakeLayer(this.layer).getChild("root"), this.texture);
        }
    }
}
