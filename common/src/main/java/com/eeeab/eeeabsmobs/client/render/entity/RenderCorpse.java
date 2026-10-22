package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelCorpse;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.server.entity.mob.corpse.EntityCorpse;
import com.eeeab.eeeabsmobs.client.render.layer.LayerOuter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.Identifier;
public class RenderCorpse extends EMMobRenderer<EntityCorpse, ModelCorpse<EntityCorpse>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/corpse.png");
    private static final Identifier GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/corpse_eyes.png");
    private static final Identifier FRENZY_GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/corpse_frenzy_eyes.png");
    private static final Identifier LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/corpse_plains.png");

    public RenderCorpse(EntityRendererProvider.Context context) {
        super(context, new ModelCorpse<>(context.bakeLayer(ModModelLayer.CORPSE)), 0.3F);
        this.addLayer(new LayerGlow<>(this, FRENZY_GLOW_LAYER, 1F, EntityCorpse::isFrenzy));
        this.addLayer(new LayerGlow<>(this, GLOW_LAYER, 0.8F));
        this.addLayer(new LayerOuter<>(this, LAYER));
    }

    @Override
    public Identifier getTextureLocation(EMRenderState corpse) {
        return TEXTURE;
    }
}
