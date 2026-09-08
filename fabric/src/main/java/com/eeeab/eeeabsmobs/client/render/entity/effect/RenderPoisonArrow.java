package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityPoisonArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderPoisonArrow extends ArrowRenderer<EntityPoisonArrow> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/poison_arrow.png");

    public RenderPoisonArrow(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityPoisonArrow entity) {
        return TEXTURE;
    }
}
