package com.eeeab.eeeabsmobs.client.render.special;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.item.ModelBusterGauntlet;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * 1.21.11 replacement for the old BlockEntityWithoutLevelRenderer (ModItemStackRender) that drew the
 * Buster Gauntlet's 3D {@link ModelBusterGauntlet} in-hand/inventory. Custom item rendering now goes through
 * the ItemStackRenderState/special-model pipeline: this renderer is registered under the id
 * {@code eeeabsmobs:buster_gauntlet} (see SpecialModelRenderersMixin) and referenced from the item model
 * definition via {@code {"type":"minecraft:special","model":{"type":"eeeabsmobs:buster_gauntlet"}}}.
 * Transforms mirror the original renderByItem (translate 0.5,1,0.5; scale -1,-1,1).
 */
public class BusterGauntletSpecialRenderer implements NoDataSpecialModelRenderer {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/item/buster_gauntlet.png");
    private final ModelBusterGauntlet model;

    public BusterGauntletSpecialRenderer(ModelBusterGauntlet model) {
        this.model = model;
    }

    @Override
    public void submit(ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
        float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        this.model.setupAnim(ItemStack.EMPTY, ageInTicks);
        RenderType renderType = RenderTypes.armorCutoutNoCull(TEXTURE);
        poseStack.pushPose();
        poseStack.translate(0.5F, 1F, 0.5F);
        poseStack.scale(-1F, -1F, 1F);
        collector.submitModelPart(this.model.root(), poseStack, renderType, light, overlay, null, false, hasFoil, -1, null, outlineColor);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.5F, 1F, 0.5F);
        poseStack.scale(-1F, -1F, 1F);
        this.model.root().getExtentsForGui(poseStack, consumer);
    }
public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<BusterGauntletSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new BusterGauntletSpecialRenderer.Unbaked());

        @Override
        public MapCodec<BusterGauntletSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
            return new BusterGauntletSpecialRenderer(new ModelBusterGauntlet(context.entityModelSet().bakeLayer(ModModelLayer.BUSTER_GAUNTLET)));
        }
    }
}
