package com.eeeab.mixin;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.special.BusterGauntletSpecialRenderer;
import com.eeeab.eeeabsmobs.client.render.special.StaticModelSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.11 has no data-driven registration for third-party {@code minecraft:special} item renderers; the codec
 * table {@code SpecialModelRenderers.ID_MAPPER} is populated only in the vanilla {@code bootstrap()}. Inject at
 * its tail to register the mod's special renderers so item model definitions can reference them by id.
 */
@Mixin(SpecialModelRenderers.class)
public class SpecialModelRenderersMixin {
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void eeeabsmobs$registerSpecialRenderers(CallbackInfo ci) {
        var idMapper = SpecialModelRenderersAccessor.eeeabmobs$getIdMapper();
        idMapper.put(
                Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "buster_gauntlet"),
                BusterGauntletSpecialRenderer.Unbaked.MAP_CODEC);
        idMapper.put(
                Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "guardian_axe"),
                new StaticModelSpecialRenderer.Unbaked(ModModelLayer.THE_GUARDIAN_BATTLEAXE,
                        Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/nameless_guardian.png")).codec());
        idMapper.put(
                Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "netherworld_katana"),
                new StaticModelSpecialRenderer.Unbaked(ModModelLayer.THE_NETHERWORLD_KATANA,
                        Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_netherworld_katana.png")).codec());
    }
}
