package com.eeeab.eeeabsmobs.client.render.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

public class ModItemStackRender extends BlockEntityWithoutLevelRenderer {
    private static boolean init;
    private static boolean THE_NETHERWORLD_KATANA_modelBaked;
    private static boolean THE_GUARDIAN_BATTLEAXE_modelBaked;
    private static boolean BUSTER_GAUNTLET_modelBaked;
    private static com.eeeab.eeeabsmobs.client.model.item.ModelBusterGauntlet BUSTER_GAUNTLET;
    private static com.eeeab.eeeabsmobs.client.model.item.ModelGuardianBattleaxe THE_GUARDIAN_BATTLEAXE;
    private static com.eeeab.eeeabsmobs.client.model.item.ModelTheNetherworldKatana THE_NETHERWORLD_KATANA;
    private static final ResourceLocation BUSTER_GAUNTLET_TEX = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/item/buster_gauntlet.png");
    private static final ResourceLocation THE_GUARDIAN_BATTLEAXE_TEX = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/nameless_guardian.png");
    private static final ResourceLocation THE_NETHERWORLD_KATANA_TEX = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_netherworld_katana.png");

    public ModItemStackRender() {
        super(null, null);
    }

    public static void initStaticModel() {
        Minecraft minecraft = Minecraft.getInstance();
        THE_NETHERWORLD_KATANA = new com.eeeab.eeeabsmobs.client.model.item.ModelTheNetherworldKatana(minecraft.getEntityModels().bakeLayer(com.eeeab.eeeabsmobs.client.model.util.ModModelLayer.THE_NETHERWORLD_KATANA));
        THE_GUARDIAN_BATTLEAXE = new com.eeeab.eeeabsmobs.client.model.item.ModelGuardianBattleaxe(minecraft.getEntityModels().bakeLayer(com.eeeab.eeeabsmobs.client.model.util.ModModelLayer.THE_GUARDIAN_BATTLEAXE));
        BUSTER_GAUNTLET = new com.eeeab.eeeabsmobs.client.model.item.ModelBusterGauntlet(minecraft.getEntityModels().bakeLayer(com.eeeab.eeeabsmobs.client.model.util.ModModelLayer.BUSTER_GAUNTLET));
        THE_NETHERWORLD_KATANA_modelBaked = true;
        THE_GUARDIAN_BATTLEAXE_modelBaked = true;
        BUSTER_GAUNTLET_modelBaked = true;
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (!init) {
            initStaticModel();
            init = true;
        }
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        if (itemStackIn.is(com.eeeab.eeeabsmobs.server.init.ItemInit.THE_NETHERWORLD_KATANA.get())) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(0.5F, -0.5F, -0.5F);
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(THE_NETHERWORLD_KATANA_TEX), true, itemStackIn.hasFoil());
            THE_NETHERWORLD_KATANA.renderToBuffer(matrixStackIn, vertexConsumer, combinedLightIn, combinedOverlayIn, -1);
            matrixStackIn.popPose();
        } else if (itemStackIn.is(com.eeeab.eeeabsmobs.server.init.ItemInit.GUARDIAN_AXE.get())) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(0.5F, -0.5F, -0.5F);
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(THE_GUARDIAN_BATTLEAXE_TEX), true, itemStackIn.hasFoil());
            THE_GUARDIAN_BATTLEAXE.renderToBuffer(matrixStackIn, vertexConsumer, combinedLightIn, combinedOverlayIn, -1);
            matrixStackIn.popPose();
        } else if (itemStackIn.is(com.eeeab.eeeabsmobs.server.init.ItemInit.BUSTER_GAUNTLET.get())) {
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1F, 0.5F);
            matrixStackIn.scale(-1F, -1F, 1F);
            BUSTER_GAUNTLET.setupAnim(itemStackIn, ageInTicks);
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(BUSTER_GAUNTLET_TEX), true, itemStackIn.hasFoil());
            BUSTER_GAUNTLET.renderToBuffer(matrixStackIn, vertexConsumer, combinedLightIn, combinedOverlayIn, -1);
            matrixStackIn.popPose();
        }
    }
}