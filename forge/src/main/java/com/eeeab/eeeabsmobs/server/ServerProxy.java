package com.eeeab.eeeabsmobs.server;

import com.eeeab.animate.server.message.MessageAnimation;
import com.eeeab.animate.server.message.MessagePlayAnimation;
import com.eeeab.animate.server.message.MessageStopAnimation;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalLaser;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.init.AttributeInit;
import com.eeeab.eeeabsmobs.server.message.*;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

/**
 * NeoForge's payload-registry networking (RegisterPayloadHandlersEvent / PayloadRegistrar) does not exist in
 * Forge. Forge 1.21.1 still has the classic SimpleChannel / ChannelBuilder API (verified against the real
 * jar with javap: net.minecraftforge.network.ChannelBuilder, .SimpleChannel and SimpleChannel$MessageBuilder
 * all still exist, and MessageBuilder.codec(StreamCodec) accepts the vanilla StreamCodec the message records
 * already carry, so the CustomPacketPayload/TYPE/STREAM_CODEC shape of the message classes did not need to
 * change - only how they get registered and how their handlers are invoked). Only
 * net.minecraftforge.network.NetworkRegistry.ChannelBuilder became the top-level net.minecraftforge.network.
 * ChannelBuilder in this Forge version.
 */
public class ServerProxy {
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public void initMod(IEventBus bus) {
        // ModLoadingContext#registerConfig(ModConfig.Type, IConfigSpec) still exists directly in Forge
        // (verified against the real jar) - the NeoForge redesign that moved this onto ModContainer does not
        // apply here.
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigHandler.COMMON_SPEC);
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
            // EntityAttributeModificationEvent#add takes Holder<Attribute>, not the bare Attribute that
            // RegistryObject<Attribute>#get() returns.
            event.add(type, AttributeInit.CRIT_CHANCE.getHolder().orElseThrow());
        }
    }

    /**
     * Builds the mod's SimpleChannel and registers every message on it. There is no more manual discriminator
     * id requirement in principle (the messages carry their own CustomPacketPayload.Type ResourceLocation),
     * but SimpleChannel#messageBuilder still takes a numeric id as part of its wire format, so the {@code ID++}
     * counter from the Forge 1.20.1 original is kept.
     * <p>
     * Unlike NeoForge's RegisterPayloadHandlersEvent, nothing calls this for you - it must be invoked
     * directly (see EEEABMobs's constructor).
     */
    public void initNetwork() {
        EEEABMobs.NETWORK = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "net"))
                .networkProtocolVersion(1)
                .acceptedVersions(Channel.VersionTest.exact(1))
                .simpleChannel();
        register(ICapabilityMessage.class, ICapabilityMessage.STREAM_CODEC, new ICapabilityMessage.Handler());
        register(UseAbilityMessage.class, UseAbilityMessage.STREAM_CODEC, new UseAbilityMessage.Handler());
        register(PlayerUseAbilityMessage.class, PlayerUseAbilityMessage.STREAM_CODEC, new PlayerUseAbilityMessage.Handler());
        register(UpdateBossBarMessage.class, UpdateBossBarMessage.STREAM_CODEC, new UpdateBossBarMessage.Handler());
        register(PopupNotificationMessage.class, PopupNotificationMessage.STREAM_CODEC, PopupNotificationMessage::handle);
        // The animate sub-package's three messages (com.eeeab.animate/**, not this pass's scope) already use
        // the same CustomPacketPayload/StreamCodec shape, so they register the same way.
        register(MessageAnimation.class, MessageAnimation.STREAM_CODEC, MessageAnimation::handle);
        register(MessagePlayAnimation.class, MessagePlayAnimation.STREAM_CODEC, MessagePlayAnimation::handle);
        register(MessageStopAnimation.class, MessageStopAnimation.STREAM_CODEC, MessageStopAnimation::handle);
    }

    // None of this mod's messages actually need registry-aware decoding, so every STREAM_CODEC (including the
    // animate sub-package's) is declared over plain FriendlyByteBuf - which is also SimpleChannel's own
    // default buffer type (messageBuilder(Class, int) with no explicit NetworkProtocol), so one register()
    // shape covers all of them.
    private static <MSG> void register(Class<MSG> type,
                                        net.minecraft.network.codec.StreamCodec<net.minecraft.network.FriendlyByteBuf, MSG> codec,
                                        java.util.function.BiConsumer<MSG, net.minecraftforge.event.network.CustomPayloadEvent.Context> handler) {
        EEEABMobs.NETWORK.messageBuilder(type, ID++)
                .codec(codec)
                .consumerMainThread(handler)
                .add();
    }
}
