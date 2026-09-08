package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.client.render.layer.LayerOuter;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelImmortalShaman;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.layer.LayerBreath;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityImmortalShaman;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderImmortalShaman extends MobRenderer<EntityImmortalShaman, ModelImmortalShaman> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_shaman.png");
    private static final ResourceLocation GLOW_LAYER = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_shaman_decoration.png");
    private static final ResourceLocation OUTER_LAYER = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_shaman_outer.png");
    public static final ResourceLocation EYES_LAYER = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_shaman_eyes.png");

    public RenderImmortalShaman(EntityRendererProvider.Context context) {
        super(context, new ModelImmortalShaman(context.bakeLayer(ModModelLayer.IMMORTAL_SHAMAN)), 0.45F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new LayerOuter<>(this, OUTER_LAYER));
        this.addLayer(new LayerGlow<>(this, EYES_LAYER));
        this.addLayer(new LayerBreath<>(this, GLOW_LAYER, 1F, LivingEntity::isAlive, 0.18F));
    }

    @Override
    public ResourceLocation getTextureLocation(EntityImmortalShaman entity) {
        return TEXTURE;
    }
}
