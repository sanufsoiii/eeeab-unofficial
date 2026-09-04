package com.eeeab.mixin;

import com.eeeab.eeeabsmobs.client.ClientProxy;
import com.eeeab.eeeabsmobs.client.gui.BossBarConfig;
import com.eeeab.eeeabsmobs.client.gui.BossBarHandler;
import com.eeeab.eeeabsmobs.client.gui.BossBarRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * See {@link com.eeeab.eeeabsmobs.client.gui.BossBarHandler}'s class javadoc for why this exists: NeoForge's
 * CustomizeGuiOverlayEvent.BossEventProgress (fired once per active boss bar, cancellable, with a mutable
 * vertical increment) has no Fabric equivalent - there is no per-boss-bar HUD hook at all, only a whole-HUD
 * render callback that would be too coarse to swap in a single bar's texture. Injecting directly into
 * BossHealthOverlay's own private per-bar draw method and cancelling it for recognised entities is the honest
 * replacement; the method signature below was confirmed to exist with javap -p against the Loom-mapped jar
 * before use.
 */
@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

    @Inject(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"), cancellable = true)
    private void eeeabmobs$drawBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, CallbackInfo ci) {
        ResourceLocation entityId = ClientProxy.BOSS_BAR_REGISTRY_NAMES.get(bossEvent.getId());
        if (entityId != null) {
            BossBarConfig config = BossBarRegistry.getBarForEntity(entityId);
            BossBarHandler.renderBossBar(guiGraphics, x, y, bossEvent, config);
            ci.cancel();
        }
    }
}
