package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelRelicObserver;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.layer.LayerCrackiness;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.server.entity.mob.CrackinessEntity;
import com.eeeab.eeeabsmobs.server.entity.mob.relicron.EntityRelicObserver;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class RenderRelicObserver extends EMMobRenderer<EntityRelicObserver, ModelRelicObserver> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_observer.png");
    private static final Identifier GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_observer_eyes.png");
    private static final Map<CrackinessEntity.CrackinessType, Identifier> RESOURCE_LOCATION_MAP = ImmutableMap.of(
            CrackinessEntity.CrackinessType.HIGH, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_observer_high.png"),
            CrackinessEntity.CrackinessType.MEDIUM, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_observer_medium.png"),
            CrackinessEntity.CrackinessType.LOW, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_observer_low.png")
    );

    public RenderRelicObserver(EntityRendererProvider.Context context) {
        super(context, new ModelRelicObserver(context.bakeLayer(ModModelLayer.RELIC_OBSERVER)), 0.8F);
        this.addLayer(new LayerGlow<>(this, GLOW_LAYER));
        this.addLayer(new LayerCrackiness<>(this, RESOURCE_LOCATION_MAP));
    }

    @Override
    protected float getFlipDegrees() {
        return 0;//获取死亡翻转角度
    }

    @Override
    public Identifier getTextureLocation(EMRenderState corpse) {
        return TEXTURE;
    }
}
