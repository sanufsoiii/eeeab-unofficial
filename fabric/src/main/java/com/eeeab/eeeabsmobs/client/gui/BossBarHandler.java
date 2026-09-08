package com.eeeab.eeeabsmobs.client.gui;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

/**
 * Was driven by NeoForge's CustomizeGuiOverlayEvent.BossEventProgress, fired by NeoForge once per active boss
 * bar with a GuiGraphics/x/y and a cancellable "should I draw the vanilla bar" outcome, plus setIncrement(...)
 * to control the vertical gap before the next bar. No Fabric equivalent event exists (there's no per-boss-bar
 * hook, only a whole-HUD render callback), so this is now driven from a mixin instead:
 * {@link com.eeeab.mixin.client.BossHealthOverlayMixin} injects into BossHealthOverlay's private
 * drawBar(GuiGraphics, int, int, BossEvent) and cancels it for boss bars this mod recognises, calling here to
 * draw the custom textures in its place. One real loss: the mixin only replaces the bar draw itself, not
 * render()'s own per-bar vertical spacing loop, so setIncrement(config.getVerticalIncrement())'s ability to
 * give a custom boss bar extra/less vertical space than vanilla's fixed per-bar gap no longer has anywhere to
 * plug in - multiple simultaneous boss bars now space out at vanilla's fixed spacing regardless of config.
 */
public class BossBarHandler {

    public static void renderBossBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, BossBarConfig config) {
        int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int j = y - 9;
        //基础血条
        net.minecraft.util.profiling.Profiler.get().push("modBossBarBase");
        drawBar(guiGraphics, x + 1, y + config.getBaseOffsetY(), bossEvent, config);
        Component component = bossEvent.getName().copy().withStyle(config.getTextColor());
        net.minecraft.util.profiling.Profiler.get().pop();
        //文字
        int k = Minecraft.getInstance().font.width(component);
        int l = w / 2 - k / 2;
        guiGraphics.drawString(Minecraft.getInstance().font, component, l, j, 16777215);
        //血条外层
        if (config.hasOverlay()) {
            net.minecraft.util.profiling.Profiler.get().push("modBossBarOverlay");
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, config.getOverlayTexture(), x + 1 + config.getOverlayOffsetX(), y + config.getOverlayOffsetY() + config.getBaseOffsetY(),
                    0F, 0F, config.getOverlayWidth(), config.getOverlayHeight(), config.getOverlayWidth(), config.getOverlayHeight());
            net.minecraft.util.profiling.Profiler.get().pop();
        }
    }

    private static void drawBar(GuiGraphics guiGraphics, int x, int y, BossEvent event, BossBarConfig config) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, config.getBaseTexture(), x, y, 0F, 0F, 182, config.getBaseHeight(), 256, config.getBaseTextureHeight());
        int i = (int) (event.getProgress() * 183.0F);
        if (i > 0) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, config.getBaseTexture(), x, y, 0F, (float) config.getBaseHeight(), i, config.getBaseHeight(), 256, config.getBaseTextureHeight());
        }
    }
}