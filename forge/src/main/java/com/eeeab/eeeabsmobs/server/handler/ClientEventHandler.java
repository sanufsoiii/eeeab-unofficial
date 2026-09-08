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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

//客户端事件处理器
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private static final ResourceLocation FRENZY_OUTLINE_LOCATION = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/misc/frenzy_outline.png");

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            // RenderLevelStageEvent#getPoseStack() is a deprecated-but-still-present name that now returns a
            // raw org.joml.Matrix4f, not a PoseStack (verified against the real jar with javap - this is a
            // vanilla MC 1.21 rendering change, not a Forge/NeoForge difference), so a PoseStack has to be
            // built from it.
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(event.getPoseStack());
            poseStack.pushPose();
            Vec3 camera = event.getCamera().getPosition();
            poseStack.translate(-camera.x, -camera.y, -camera.z);
            ClientProxy.LIGHTNING_RENDER.render(event.getPartialTick(), poseStack, Minecraft.getInstance().renderBuffers().bufferSource());
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
                if (player.hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) shakeAmplitude = 0.01f;
                event.setPitch((float) (event.getPitch() + shakeAmplitude * Math.cos(ticksExistedDelta * 3 + 2) * 25));
                event.setYaw((float) (event.getYaw() + shakeAmplitude * Math.cos(ticksExistedDelta * 5 + 1) * 25));
                event.setRoll((float) (event.getRoll() + shakeAmplitude * Math.cos(ticksExistedDelta * 4) * 25));
            }
        }
    }

    /**
     * NeoForge's RenderGuiEvent.Post does not exist in Forge (verified against the real jar - Forge's own GUI
     * hook is CustomizeGuiOverlayEvent, one per vanilla overlay, with no "after everything" variant). Forge
     * 1.21.1 draws custom overlays through the LayeredDraw system instead, registered via the mod-bus
     * AddGuiOverlayLayersEvent - see ClientProxy#initMod, which wires this method in as a plain layer callback
     * rather than a @SubscribeEvent (AddGuiOverlayLayersEvent fires on the mod bus, but this class is entirely
     * registered on the Forge game bus, so it cannot carry a mod-bus listener itself).
     */
    public static void renderOverlayLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            FrenzyCapability.IFrenzyCapability capability = CapabilityHandler.getCapability(player, CapabilityHandler.FRENZY_CAPABILITY);
            if (capability != null && capability.flag()) {
                RenderSystem.setShaderTexture(0, FRENZY_OUTLINE_LOCATION);
                Window res = Minecraft.getInstance().getWindow();
                guiGraphics.blit(FRENZY_OUTLINE_LOCATION, 0, 0, 0, 0, res.getGuiScaledWidth(), res.getGuiScaledHeight(), res.getGuiScaledWidth(), res.getGuiScaledHeight());
            }
        }
        TipNotificationHandler.renderOverlay(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    //渲染刻
    // Forge 1.21.1 still has the original TickEvent.ClientTickEvent with a Phase.START/END field (verified
    // against the real jar) - the NeoForge Pre/Post split does not apply here.
    @SubscribeEvent
    public void onRenderTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TipNotificationHandler.clientTick();
        }
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

    // Forge 1.21.1 still has the original TickEvent.LevelTickEvent with side/phase fields (verified against
    // the real jar) - the NeoForge per-Level Pre/Post redesign does not apply here.
    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isClient()) {
            BossMusicPlayer.tick();
        }
    }
}
