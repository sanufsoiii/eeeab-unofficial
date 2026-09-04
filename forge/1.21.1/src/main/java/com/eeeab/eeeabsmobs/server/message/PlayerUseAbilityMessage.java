package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.ability.AbilityHandler;
import com.eeeab.eeeabsmobs.server.capability.AbilityCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.function.BiConsumer;

public record PlayerUseAbilityMessage(int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerUseAbilityMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "player_use_ability"));

    public static final StreamCodec<FriendlyByteBuf, PlayerUseAbilityMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayerUseAbilityMessage::index,
            PlayerUseAbilityMessage::new
    );

    @Override
    public CustomPacketPayload.Type<PlayerUseAbilityMessage> type() {
        return TYPE;
    }

    public static class Handler implements BiConsumer<PlayerUseAbilityMessage, CustomPayloadEvent.Context> {
        @Override
        public void accept(final PlayerUseAbilityMessage message, final CustomPayloadEvent.Context context) {
            final ServerPlayer player = context.getSender();
            context.enqueueWork(() -> {
                AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandler.getCapability(player, CapabilityHandler.ABILITY_CAPABILITY);
                if (abilityCapability != null) {
                    AbilityHandler.INSTANCE.sendAbilityMessage(player, abilityCapability.getAbilityTypeByEntity(player)[message.index()]);
                }
            });
        }
    }
}
