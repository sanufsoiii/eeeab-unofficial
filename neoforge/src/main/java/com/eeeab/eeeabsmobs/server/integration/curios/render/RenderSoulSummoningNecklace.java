package com.eeeab.eeeabsmobs.server.integration.curios.render;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.integration.curios.model.ModelSoulSummoningNecklace;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class RenderSoulSummoningNecklace implements ICurioRenderer {
    private final ModelSoulSummoningNecklace model;
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/armor/soul_summoning_necklace.png");

    public RenderSoulSummoningNecklace() {
        this.model = new ModelSoulSummoningNecklace(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayer.SOUL_SUMMONING_NECKLACE));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ICurioRenderer.followBodyRotations(slotContext.entity(), this.model);
        // ItemRenderer.getArmorFoilBuffer dropped a boolean parameter (was (source, type, outline, foil), now
        // (source, type, foil)). Model#renderToBuffer's four float RGBA components were also collapsed into a
        // single packed ARGB int; 0xFFFFFFFF is opaque white, matching the old (1,1,1,1).
        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(TEXTURE), stack.hasFoil());
        this.model.renderToBuffer(matrixStack, consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }
}
