package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.client.render.layer.LayerOuter;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelCorpseWarlock;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.server.entity.mob.corpse.EntityCorpseWarlock;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.Identifier;
public class RenderCorpseWarlock extends EMMobRenderer<EntityCorpseWarlock, ModelCorpseWarlock> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/corpse_warlock.png");
    private static final Identifier GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/corpse_warlock_eyes.png");
    private static final Identifier NECKLACE_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/corpse_warlock_necklace.png");

    public RenderCorpseWarlock(EntityRendererProvider.Context context) {
        super(context, new ModelCorpseWarlock(context.bakeLayer(ModModelLayer.CORPSE_SLAVERY)), 0.3F);
        this.addLayer(new LayerOuter<>(this, NECKLACE_LAYER, true, e -> !e.isInvisible()));
        this.addLayer(new LayerGlow<EntityCorpseWarlock, ModelCorpseWarlock>(this, GLOW_LAYER, e -> !e.isInvisible() && !e.glowControlled.isStop(),
                (entity, partialTicks) -> entity.glowControlled.getAnimationFraction(partialTicks), true) {
            @Override
            protected RenderType getRenderType(EntityCorpseWarlock entity) {
                return RenderTypes.entityTranslucentEmissive(this.location);
            }
        });

    }

    @Override
    public Identifier getTextureLocation(EMRenderState corpse) {
        return TEXTURE;
    }
}
