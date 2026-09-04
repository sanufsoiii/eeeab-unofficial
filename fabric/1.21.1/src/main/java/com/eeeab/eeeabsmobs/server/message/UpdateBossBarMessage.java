package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.ClientProxy;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record UpdateBossBarMessage(UUID bossID, @Nullable ResourceLocation registryName,
                                    boolean remove) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateBossBarMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "update_boss_bar"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBossBarMessage> STREAM_CODEC = StreamCodec.composite(
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

    /**
     * Client-only handler, kept in a nested class on purpose. The payload record itself is touched on
     * the server when its type is registered, and Fabric refuses to load a class that references
     * client-only types there ("Cannot load class ... in environment type SERVER"). A nested class is a
     * separate class file, so it is only loaded when the client actually references it.
     */
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static final class Client {
    public static void handle(final UpdateBossBarMessage message, final net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) {
            context.client().execute(() -> {
                if (message.registryName() == null) ClientProxy.BOSS_BAR_REGISTRY_NAMES.remove(message.bossID());
                else ClientProxy.BOSS_BAR_REGISTRY_NAMES.put(message.bossID(), message.registryName());
            });
        }
    }
}
