package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.ClientProxy;
import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import com.eeeab.eeeabsmobs.client.sound.BossMusicPlayer;
import com.eeeab.eeeabsmobs.server.capability.FrenzyCapability;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

public class ClientEventHandler {
    private static final ResourceLocation FRENZY_OUTLINE_LOCATION = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/misc/frenzy_outline.png");

    public static void register() {
        ClientGuiEvent.RENDER_HUD.register((guiGraphics, deltaTracker) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                FrenzyCapability.IFrenzyCapability capability = CapabilityHandler.getCapability(player, CapabilityHandler.FRENZY_CAPABILITY);
                if (capability != null && capability.flag()) {
                    Window res = Minecraft.getInstance().getWindow();
                    guiGraphics.blit(FRENZY_OUTLINE_LOCATION, 0, 0, 0, 0, res.getGuiScaledWidth(), res.getGuiScaledHeight(), res.getGuiScaledWidth(), res.getGuiScaledHeight());
                }
            }
            TipNotificationHandler.renderOverlay(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
        });

        ClientTickEvent.CLIENT_POST.register(client -> {
            TipNotificationHandler.clientTick();
            BossMusicPlayer.tick();
        });
    }

    public static void renderWorld(float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        ClientProxy.LIGHTNING_RENDER.render(partialTick, poseStack, bufferSource);
    }
}
