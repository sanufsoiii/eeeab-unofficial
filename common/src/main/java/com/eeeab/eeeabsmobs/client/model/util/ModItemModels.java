package com.eeeab.eeeabsmobs.client.model.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ModItemModels {
    public static final String[] HAND_MODEL_ITEMS = new String[]{"guardian_axe", "immortal_staff", "netherworld_katana"};

    public ModItemModels() {
    }

    public static void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        String item = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
        boolean useHandModel = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, useHandModel ? "item/" + item + "_in_hand" : item);

        ItemModel itemModel = Minecraft.getInstance().getModelManager().getItemModel(modelId);
        ItemStackRenderState state = new ItemStackRenderState();
        itemModel.update(state, itemStack, Minecraft.getInstance().getItemModelResolver(), transformType, null, null, 0);
        state.render(poseStack, bufferSource, packedLight, packedOverlay);
    }
}
