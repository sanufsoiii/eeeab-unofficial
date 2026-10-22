package com.eeeab.eeeabsmobs.client.render.special;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.item.ModelBusterGauntlet;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.Set;

public class BusterGauntletSpecialRenderer implements NoDataSpecialModelRenderer {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/item/buster_gauntlet.png");
    private final ModelBusterGauntlet model;

    public BusterGauntletSpecialRenderer(ModelBusterGauntlet model) {
        this.model = model;
    }

    @Override
    public void render(ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, boolean hasFoil) {
        float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        this.model.setupAnim(ItemStack.EMPTY, ageInTicks);
        RenderType renderType = RenderType.armorCutoutNoCull(TEXTURE);
        poseStack.pushPose();
        poseStack.translate(0.5F, 1F, 0.5F);
        poseStack.scale(-1F, -1F, 1F);
        this.model.root().render(poseStack, buffer.getBuffer(renderType), light, overlay);
        poseStack.popPose();
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<BusterGauntletSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new BusterGauntletSpecialRenderer.Unbaked());

        @Override
        public MapCodec<BusterGauntletSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(net.minecraft.client.model.geom.EntityModelSet entityModelSet) {
            return new BusterGauntletSpecialRenderer(new ModelBusterGauntlet(entityModelSet.bakeLayer(ModModelLayer.BUSTER_GAUNTLET)));
        }
    }
}
