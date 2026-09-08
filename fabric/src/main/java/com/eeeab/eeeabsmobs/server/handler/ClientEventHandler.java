package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.ClientProxy;
import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import com.eeeab.eeeabsmobs.client.sound.BossMusicPlayer;
import com.eeeab.eeeabsmobs.server.capability.FrenzyCapability;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

/**
 * Fabric has no mod/event bus; every hook below that has a direct Fabric API callback is wired in
 * {@link #register()} (called once from client init - out of this port's scope, see ClientProxy.java).
 * <p>
 * NOT ported in this pass - each needs a mixin, not a callback (no camera-angle, item-in-hand-render, or
 * boss-bar-progress-render event exists anywhere in fabric-rendering-v1, checked with {@code javap} against
 * every class in that module), and time ran out before they could be verified and written safely:
 * <ul>
 *   <li>{@code onSetupCamera} (camera shake from {@code EntityCameraShake} / stun) - needs a mixin on
 *       {@code GameRenderer} or {@code Camera#setup}.</li>
 *   <li>{@code onRenderHand} (charged Guardian Core first-person render override) - needs a mixin on
 *       {@code ItemInHandRenderer}.</li>
 *   <li>{@code onRenderBossBar} (custom boss bar replacing the vanilla one) - needs a mixin on
 *       {@code BossHealthOverlay}.</li>
 * </ul>
 * These are left broken rather than guessed at; see the port report for the full list.
 */
@Environment(EnvType.CLIENT)
public class ClientEventHandler {
    private static final Identifier FRENZY_OUTLINE_LOCATION = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/misc/frenzy_outline.png");

    public static void register() {
        // 渲染刻: RenderLevelStageEvent.Stage.AFTER_ENTITIES -> WorldRenderEvents.AFTER_ENTITIES is a direct match.
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            PoseStack poseStack = context.matrices();
            poseStack.pushPose();
            Vec3 camera = context.worldState().cameraRenderState.pos;
            poseStack.translate(-camera.x, -camera.y, -camera.z);
            ClientProxy.LIGHTNING_RENDER.render(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false), poseStack, Minecraft.getInstance().renderBuffers().bufferSource());
            poseStack.popPose();
        });

        // RenderGuiEvent.Post -> HudRenderCallback fires after the full vanilla HUD, same "draw on top of
        // everything" guarantee the old handler relied on.
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
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

        // ClientTickEvent.Post -> ClientTickEvents.END_CLIENT_TICK covers both the old onRenderTick (tip
        // notification bookkeeping) and onLevelTick's client-side branch (boss music) - both only ever ran
        // client-side to begin with.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TipNotificationHandler.clientTick();
            BossMusicPlayer.tick();
        });
    }
}
