package com.eeeab.eeeabsmobs.client.model.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ModItemModels extends BlockEntityWithoutLevelRenderer {
    public static final String[] HAND_MODEL_ITEMS = new String[]{"guardian_axe", "immortal_staff", "netherworld_katana"};

    public ModItemModels() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        String item = itemId.getPath();
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        ModelResourceLocation modelInventoryId = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, item));
        ModelResourceLocation modelHandId = com.eeeab.eeeabsmobs.platform.PlatformServices.get().getModelHandId(item);

        BakedModel modelInventory = modelManager.getModel(modelInventoryId);
        boolean useHandModel = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        BakedModel modelToUse = useHandModel ? modelManager.getModel(modelHandId) : modelInventory;

        boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        Minecraft.getInstance().getItemRenderer().render(itemStack, transformType, leftHand, poseStack, bufferSource, packedLight, packedOverlay, modelToUse);
    }
}
