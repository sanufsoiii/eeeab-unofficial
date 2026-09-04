package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.ClientProxy;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public record UpdateBossBarMessage(UUID bossID, @Nullable ResourceLocation registryName,
                                    boolean remove) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateBossBarMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "update_boss_bar"));

    public static final StreamCodec<FriendlyByteBuf, UpdateBossBarMessage> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, UpdateBossBarMessage::bossID,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), msg -> Optional.ofNullable(msg.registryName()),
            ByteBufCodecs.BOOL, UpdateBossBarMessage::remove,
            (bossID, registryName, remove) -> new UpdateBossBarMessage(bossID, registryName.orElse(null), remove)
    );

    public UpdateBossBarMessage(UUID bossID, @Nullable LivingEntity entity) {
        this(bossID, entity != null ? BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()) : null, entity == null);
    }

    @Override
    public CustomPacketPayload.Type<UpdateBossBarMessage> type() {
        return TYPE;
    }

    public static class Handler implements BiConsumer<UpdateBossBarMessage, CustomPayloadEvent.Context> {
        @Override
        public void accept(final UpdateBossBarMessage message, final CustomPayloadEvent.Context context) {
            context.enqueueWork(() -> {
                if (message.registryName() == null) ClientProxy.BOSS_BAR_REGISTRY_NAMES.remove(message.bossID());
                else ClientProxy.BOSS_BAR_REGISTRY_NAMES.put(message.bossID(), message.registryName());
            });
        }
    }
}
