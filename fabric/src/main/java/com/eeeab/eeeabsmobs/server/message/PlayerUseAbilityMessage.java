package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.ability.AbilityHandler;
import com.eeeab.eeeabsmobs.server.capability.AbilityCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record PlayerUseAbilityMessage(int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerUseAbilityMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "player_use_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerUseAbilityMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayerUseAbilityMessage::index,
            PlayerUseAbilityMessage::new
    );

    @Override
    public CustomPacketPayload.Type<PlayerUseAbilityMessage> type() {
        return TYPE;
    }

    public static void handle(final PlayerUseAbilityMessage message, final ServerPlayNetworking.Context context) {
        final ServerPlayer player = context.player();
        context.server().execute(() -> {
            AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandler.getCapability(player, CapabilityHandler.ABILITY_CAPABILITY);
            if (abilityCapability != null) {
                AbilityHandler.INSTANCE.sendAbilityMessage(player, abilityCapability.getAbilityTypeByEntity(player)[message.index()]);
            }
        });
    }
}
