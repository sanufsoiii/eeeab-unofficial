package com.eeeab.eeeabsmobs.server;

import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalLaser;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.init.AttributeInit;
import com.eeeab.eeeabsmobs.server.message.*;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import com.eeeab.animate.server.message.MessageAnimation;
import com.eeeab.animate.server.message.MessagePlayAnimation;
import com.eeeab.animate.server.message.MessageStopAnimation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ServerProxy {
    public static final String VERSION = "1.0";

    public void initMod(IEventBus bus) {
        // ModLoadingContext#registerConfig(ModConfig.Type, IConfigSpec) was removed; config registration now
        // goes through the ModContainer, which is still reachable off the active ModLoadingContext at mod
        // construction time (see net.neoforged.fml.ModLoadingContext / ModContainer in loader-4.0.44).
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, ModConfigHandler.COMMON_SPEC);
        // NOTE: AttachCapabilitiesEvent / RegisterCapabilitiesEvent no longer exist - data attachments
        // (see CapabilityHandler) register themselves on the mod bus via CapabilityHandler.register(bus),
        // called directly from EEEABMobs's constructor. Nothing to wire up here anymore.
        bus.addListener(this::onEntityAttributeModification);
    }

    public void initForge(IEventBus bus) {
        bus.addListener(this::onCommandRegister);
    }

    public void loadComplete(IEventBus bus) {
    }

    public Object getISTERProperties() {
        return null;
    }

    public Object getASTEProperties() {
        return null;
    }

    public void playImmortalLaserSound(EntityImmortalLaser laser) {
    }

    public void playTickableSound(Entity entity, int id) {
    }

    public void stopTickableSound(Entity entity) {
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();
        //jar时注释这段代码
        //CombatHintHudCommand.register(dispatcher, context);
    }

    @SubscribeEvent
    public void onEntityAttributeModification(final EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, AttributeInit.CRIT_CHANCE);
        }
    }

    /**
     * NeoForge replaced the SimpleChannel/NetworkRegistry.ChannelBuilder API (net.neoforged.neoforge.network.simple,
     * .NetworkRegistry) with a payload-based system: packets are net.minecraft.network.protocol.common.custom.
     * CustomPacketPayload records with their own StreamCodec, registered here through the PayloadRegistrar handed
     * out by RegisterPayloadHandlersEvent (mod-bus event, fired once during loading - see
     * net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent in the sources jar). There is no more
     * manual discriminator id (the old {@code ID++} counter) - the payload's own ResourceLocation type is the id.
     * <p>
     * The animate sub-package's messages (MessageAnimation, MessagePlayAnimation, MessageStopAnimation) are not
     * migrated here: they still extend the removed NetworkEvent API and are out of this pass's scope
     * (com.eeeab.animate/**). They are commented out below rather than silently dropped, so whoever migrates
     * com.eeeab.animate to CustomPacketPayload has a checklist and this file keeps compiling in the meantime.
     */
    public void initNetwork(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToClient(ICapabilityMessage.TYPE, ICapabilityMessage.STREAM_CODEC, new ICapabilityMessage.Handler());
        registrar.playToClient(UseAbilityMessage.TYPE, UseAbilityMessage.STREAM_CODEC, new UseAbilityMessage.Handler());
        registrar.playToServer(PlayerUseAbilityMessage.TYPE, PlayerUseAbilityMessage.STREAM_CODEC, new PlayerUseAbilityMessage.Handler());
        registrar.playToClient(UpdateBossBarMessage.TYPE, UpdateBossBarMessage.STREAM_CODEC, new UpdateBossBarMessage.Handler());
        registrar.playToClient(PopupNotificationMessage.TYPE, PopupNotificationMessage.STREAM_CODEC, PopupNotificationMessage::handle);
        // The animate package's three messages were ported to CustomPacketPayload in parallel with this
        // file, so the placeholder TODO left here was already stale -- and nothing catches that at compile
        // time. Leaving them unregistered crashed the server the moment a boss ticked:
        // "Payload eeeabsmobs:animation may not be sent to the client!".
        registrar.playToClient(MessageAnimation.TYPE, MessageAnimation.STREAM_CODEC, MessageAnimation::handle);
        registrar.playToServer(MessagePlayAnimation.TYPE, MessagePlayAnimation.STREAM_CODEC, MessagePlayAnimation::handle);
        registrar.playToClient(MessageStopAnimation.TYPE, MessageStopAnimation.STREAM_CODEC, MessageStopAnimation::handle);
    }
}
