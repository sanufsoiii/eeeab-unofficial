package com.eeeab.eeeabsmobs.client.model.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
/**
 * Was NeoForge's ModelEvent.ModifyBakingResult: swap in a different baked model (item/<name>_in_hand.json,
 * registered as an extra model in RegisterHandler#registerModels) for first/third-person hand rendering of
 * the HAND_MODEL_ITEMS, keeping the plain inventory model otherwise.
 * <p>
 * On 1.21.11 the whole baked-BakedModel/ModelResourceLocation/ItemRenderer.render pipeline is gone; items
 * render through the {@link ItemStackRenderState} / {@link ItemModel} pipeline. The equivalent of the old
 * swap is now data-driven: define assets/<ns>/items/&lt;item&gt;.json (client item model definition) with a
 * per-view-context selector pointing at the "item/&lt;item&gt;_in_hand" model. This helper keeps the same
 * hand-vs-inventory choice in code by looking the corresponding ItemModel up from the ModelManager and
 * submitting it - the JSON definitions themselves still need to be authored (human follow-up, see
 * RegisterHandler#registerModels).
 */
public class ModItemModels {
    public static final String[] HAND_MODEL_ITEMS = new String[]{"guardian_axe", "immortal_staff", "netherworld_katana"};

    public ModItemModels() {
    }

    /**
     * Submits an item stack for rendering through the new item-model pipeline, using the item's
     * "item/<name>_in_hand" client item model definition for hand contexts (when defined) and the
     * item's own definition otherwise.
     */
    public static void submitByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay) {
        String item = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
        boolean useHandModel = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        Identifier modelId = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, useHandModel ? "item/" + item + "_in_hand" : item);

        ItemModel itemModel = Minecraft.getInstance().getModelManager().getItemModel(modelId);
        ItemStackRenderState state = new ItemStackRenderState();
        itemModel.update(state, itemStack, Minecraft.getInstance().getItemModelResolver(), transformType, null, null, 0);
        state.submit(poseStack, collector, packedLight, packedOverlay, -1);
    }
}
