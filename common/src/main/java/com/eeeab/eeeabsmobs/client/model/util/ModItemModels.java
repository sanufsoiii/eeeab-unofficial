package com.eeeab.eeeabsmobs.client.model.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
/**
 * Was NeoForge's ModelEvent.ModifyBakingResult: wrap the plain inventory-baked model in an anonymous BakedModel
 * whose overridden applyTransform(ItemDisplayContext, ...) swapped in a different baked model (loaded from
 * item/&lt;name&gt;_in_hand.json, registered as a "standalone" extra model in RegisterHandler#registerModels)
 * for first/third-person hand rendering. On 1.21.1, BakedModel#applyTransform no longer exists at all - the
 * interface now only exposes getTransforms() (pose-only ItemTransforms, not swappable geometry; verified with
 * javap against the Loom-mapped jar), so a NeoForge-shaped per-context quad swap inside BakedModel simply
 * cannot be reproduced the same way on either NeoForge 1.21.1's replacement or vanilla/Fabric.
 * <p>
 * Instead this now renders through Fabric's BuiltinItemRendererRegistry (the same mechanism this codebase
 * already uses for guardian_axe/netherworld_katana's fully-custom Java models, see ModItemStackRender), fetching
 * whichever baked JSON model is appropriate straight from the ModelManager and handing it to
 * ItemRenderer#render, which does its own context-based pose transform. guardian_axe and netherworld_katana are
 * listed here too but never actually reach {@link #renderByItem} - they're already registered against
 * ModItemStackRender's fully custom-model renderer in ClientProxy, which takes priority - so only immortal_staff
 * (the one HAND_MODEL_ITEMS entry with no custom Java model) is actually affected by this class now.
 */
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
