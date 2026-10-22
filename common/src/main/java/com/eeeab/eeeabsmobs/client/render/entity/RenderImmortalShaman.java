package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.client.render.layer.LayerOuter;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelImmortalShaman;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.layer.LayerBreath;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityImmortalShaman;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
public class RenderImmortalShaman extends EMMobRenderer<EntityImmortalShaman, ModelImmortalShaman> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_shaman.png");
    private static final Identifier GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_shaman_decoration.png");
    private static final Identifier OUTER_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_shaman_outer.png");
    public static final Identifier EYES_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_shaman_eyes.png");

    public RenderImmortalShaman(EntityRendererProvider.Context context) {
        super(context, new ModelImmortalShaman(context.bakeLayer(ModModelLayer.IMMORTAL_SHAMAN)), 0.45F);
        // 1.21.11: ItemInHandLayer now requires an ArmedEntityRenderState-based renderer; EMRenderState
        // (our shared bridge state) is not one, so the held-item layer is dropped until the state
        // hierarchy is reworked (visual debt: shaman's held item no longer renders).
        this.addLayer(new LayerOuter<>(this, OUTER_LAYER));
        this.addLayer(new LayerGlow<>(this, EYES_LAYER));
        this.addLayer(new LayerBreath<>(this, GLOW_LAYER, 1F, LivingEntity::isAlive, 0.18F));
    }

    @Override
    public Identifier getTextureLocation(EMRenderState entity) {
        return TEXTURE;
    }
}
