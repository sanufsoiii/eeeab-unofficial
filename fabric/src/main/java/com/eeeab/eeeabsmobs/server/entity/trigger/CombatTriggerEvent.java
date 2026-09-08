package com.eeeab.eeeabsmobs.server.entity.trigger;

import com.eeeab.animate.server.animation.Animation;
import com.eeeab.animate.server.animation.AnimationNotification;
import com.eeeab.animate.server.event.AnimationEvent;
import com.eeeab.eeeabsmobs.server.entity.EEEABMobLibrary;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

/**
 * Was @Mod.EventBusSubscriber-shaped, with each handler subscribed via NeoForge's mod-bus @SubscribeEvent -
 * Fabric has no such generic event bus. onAnimationStart is no longer posted as an AnimationEvent.Start (see
 * that class's javadoc); AnimationHandler#updateAnimations now calls it directly instead. onPlayerLogout used
 * NeoForge's PlayerEvent.PlayerLoggedOutEvent; the Fabric-idiomatic replacement is
 * ServerPlayConnectionEvents.DISCONNECT (verified with javap against fabric-networking-api-v1), wired in
 * {@link #register()} below.
 */
public final class CombatTriggerEvent {
    public static final CombatTriggerEvent INSTANCE = new CombatTriggerEvent();

    public static void register() {
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> INSTANCE.onPlayerLogout(handler.player));
    }

    //@SubscribeEvent
    //public void onEntityJoinLevel(EntityJoinLevelEvent event) {
    //    if (!ModConfigHandler.COMMON.others.enableCombatPrompts.get()) return;
    //    if (event.getLevel().isClientSide || event.loadedFromDisk()) return;
    //    if (event.getEntity() instanceof LivingEntity entity) {
    //        ResourceLocation entityId = EntityType.getKey(entity.getType());
    //        if (CombatTriggerHandler.canTriggerEntityType(entityId)) {
    //            List<ServerPlayer> nearbyPlayers = event.getLevel().getEntitiesOfClass(
    //                    ServerPlayer.class,
    //                    entity.getBoundingBox().inflate(32),
    //                    EntitySelector.NO_CREATIVE_OR_SPECTATOR
    //            );
    //            for (ServerPlayer player : nearbyPlayers) {
    //                CombatTriggerHandler.checkAndTrigger(player, entityId, new TriggerContext(entity, true));
    //            }
    //        }
    //    }
    //}

    public void onAnimationStart(AnimationEvent.Start<EEEABMobLibrary> event) {
        if (!ModConfigHandler.COMMON.others.enableCombatPrompts.get()) return;
        EEEABMobLibrary entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!(event.getAnimation() instanceof AnimationNotification)) return;
        ResourceLocation entityId = EntityType.getKey(entity.getType());
        if (CombatTriggerHandler.canTriggerEntityType(entityId)) {
            double range = entity.getAttributeValue(Attributes.FOLLOW_RANGE);
            List<ServerPlayer> nearbyPlayers = entity.level().getEntitiesOfClass(
                    ServerPlayer.class,
                    entity.getBoundingBox().inflate(range),
                    EntitySelector.NO_CREATIVE_OR_SPECTATOR
            );
            Animation animation = entity.getAnimation();
            for (ServerPlayer player : nearbyPlayers) {
                CombatTriggerHandler.checkAndTrigger(player, entityId, new TriggerContext(entity, animation));
            }
        }
    }

    //@SubscribeEvent
    //public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    //    if (!ModConfigHandler.COMMON.others.enableCombatPrompts.get()) return;
    //    if (event.phase != TickEvent.Phase.END) return;
    //    if (event.player.level().isClientSide()) return;
    //    ServerPlayer player = (ServerPlayer) event.player;
    //    if (player.tickCount % 1200 == 0) {
    //        CooldownManager.cleanup();
    //    }
    //}

    public void onPlayerLogout(ServerPlayer player) {
        if (!ModConfigHandler.COMMON.others.enableCombatPrompts.get()) return;
        CombatTriggerHandler.resetPlayerTriggers(player);
    }
}