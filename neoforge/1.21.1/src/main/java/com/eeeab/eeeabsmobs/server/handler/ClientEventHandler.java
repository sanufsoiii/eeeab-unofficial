package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.ClientProxy;
import com.eeeab.eeeabsmobs.client.gui.BossBarConfig;
import com.eeeab.eeeabsmobs.client.gui.BossBarHandler;
import com.eeeab.eeeabsmobs.client.gui.BossBarRegistry;
import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import com.eeeab.eeeabsmobs.client.sound.BossMusicPlayer;
import com.eeeab.eeeabsmobs.server.capability.FrenzyCapability;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityCameraShake;
import com.eeeab.eeeabsmobs.server.init.EffectInit;
import com.eeeab.eeeabsmobs.server.init.ItemInit;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

//客户端事件处理器
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private static final ResourceLocation FRENZY_OUTLINE_LOCATION = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/misc/frenzy_outline.png");

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            Vec3 camera = event.getCamera().getPosition();
            poseStack.translate(-camera.x, -camera.y, -camera.z);
            ClientProxy.LIGHTNING_RENDER.render(event.getPartialTick().getGameTimeDeltaPartialTick(false), poseStack, Minecraft.getInstance().renderBuffers().bufferSource());
            poseStack.popPose();
        }
    }

    //改变玩家相机角度
    @SubscribeEvent
    public void onSetupCamera(ViewportEvent.ComputeCameraAngles event) {
        Player player = Minecraft.getInstance().player;
        float delta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        if (player != null) {
            float ticksExistedDelta = player.tickCount + delta;
            if (ModConfigHandler.CLIENT.enableCameraShake.get() && !Minecraft.getInstance().isPaused()) {
                float shakeAmplitude = 0;
                for (EntityCameraShake cameraShake : player.level().getEntitiesOfClass(EntityCameraShake.class, player.getBoundingBox().inflate(20, 20, 20))) {
                    if (cameraShake.distanceTo(player) < cameraShake.getRadius()) {
                        shakeAmplitude += cameraShake.getShakeAmount(player, delta);
                    }
                }
                if (shakeAmplitude > 1.0f) shakeAmplitude = 1.0f;
                if (player.hasEffect(EffectInit.STUN_EFFECT)) shakeAmplitude = 0.01f;
                event.setPitch((float) (event.getPitch() + shakeAmplitude * Math.cos(ticksExistedDelta * 3 + 2) * 25));
                event.setYaw((float) (event.getYaw() + shakeAmplitude * Math.cos(ticksExistedDelta * 5 + 1) * 25));
                event.setRoll((float) (event.getRoll() + shakeAmplitude * Math.cos(ticksExistedDelta * 4) * 25));
            }
        }
    }

    // NeoForge removed RenderGuiOverlayEvent/VanillaGuiOverlay entirely in favour of the LayeredDraw-based HUD
    // (RenderGuiLayerEvent keyed by VanillaGuiLayers ResourceLocation). Neither FROSTBITE nor PLAYER_LIST has a
    // matching vanilla layer any more (FROSTBITE was never a vanilla layer to begin with, and the closest
    // existing layer to PLAYER_LIST is TAB_LIST, which only renders while the tab-list key is held - not
    // suitable for an always-visible notification). Both are drawn unconditionally after the full HUD instead
    // via RenderGuiEvent.Post, which preserves "draw once per frame, on top of everything else".
    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            FrenzyCapability.IFrenzyCapability capability = CapabilityHandler.getCapability(player, CapabilityHandler.FRENZY_CAPABILITY);
            if (capability != null && capability.flag()) {
                RenderSystem.setShaderTexture(0, FRENZY_OUTLINE_LOCATION);
                Window res = Minecraft.getInstance().getWindow();
                event.getGuiGraphics().blit(FRENZY_OUTLINE_LOCATION, 0, 0, 0, 0, res.getGuiScaledWidth(), res.getGuiScaledHeight(), res.getGuiScaledWidth(), res.getGuiScaledHeight());
            }
        }
        TipNotificationHandler.renderOverlay(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
    }

    //渲染刻
    @SubscribeEvent
    public void onRenderTick(ClientTickEvent.Post event) {
        TipNotificationHandler.clientTick();
    }

    //在渲染实体前触发
    //@SubscribeEvent
    //public void onRenderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
    //    LivingEntity entity = event.getEntity();
    //    StunCapability.IStunCapability capability = CapabilityHandler.getCapability(entity, CapabilityHandler.STUN_CAPABILITY);
    //    if (capability != null && capability.flag()) {
    //        entity.yHeadRot = entity.yHeadRotO = capability.getYawHead();
    //        entity.yBodyRot = entity.yBodyRotO = capability.getCRenderYawOffset();
    //        entity.attackAnim = entity.oAttackAnim = capability.getCSwingProgress();
    //        entity.walkAnimation.speed(capability.getCLimbSwingAmount());
    //        entity.setYRot(entity.yRotO = capability.getYaw());
    //        entity.setXRot(entity.xRotO = capability.getPitch());
    //    }
    //}

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        ItemInHandRenderer itemRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
        MultiBufferSource buffer = event.getMultiBufferSource();
        PoseStack poseStack = event.getPoseStack();
        int packedLight = event.getPackedLight();
        ItemStack useItem = player.getUseItem();
        if (useItem.is(ItemInit.GUARDIAN_CORE.get()) && useItem.is(player.getItemInHand(event.getHand()).getItem())) {
            event.setCanceled(true);
            renderChargedGuardianCore(itemRenderer, poseStack, buffer, packedLight, player, event.getHand());
        }
    }

    private static void renderChargedGuardianCore(ItemInHandRenderer itemRenderer, PoseStack poseStack, MultiBufferSource buffer,
                                                  int combinedLight, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean isMainHand = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean rightHand = arm == HumanoidArm.RIGHT;
        int direction = rightHand ? 1 : -1;
        poseStack.pushPose();
        poseStack.translate(direction * 0.5F, -0.52F, -0.72F);
        poseStack.translate(direction * -0.5F, 0F, 0.1F);
        poseStack.mulPose(Axis.YP.rotationDegrees(45F));
        ItemDisplayContext transformType = isMainHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        itemRenderer.renderItem(player, stack, transformType, !isMainHand, poseStack, buffer, combinedLight);
        poseStack.popPose();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if (!ModConfigHandler.CLIENT.enableCustomBossBars.get()) return;
        ResourceLocation bossRegistryName = ClientProxy.BOSS_BAR_REGISTRY_NAMES.getOrDefault(event.getBossEvent().getId(), null);
        if (bossRegistryName == null) return;
        BossBarConfig config = BossBarRegistry.getBarForEntity(bossRegistryName);
        event.setCanceled(true);
        BossBarHandler.renderBossBar(event, config);
    }

    // TickEvent.LevelTickEvent (with its side/phase fields) was replaced by LevelTickEvent.Pre/Post, which fires
    // per-Level rather than per-side and no longer carries a LogicalSide - check level.isClientSide() instead.
    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            BossMusicPlayer.tick();
        }
    }
}
