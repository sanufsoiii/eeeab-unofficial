package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.capability.AbilityCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public record UseAbilityMessage(int entityID, int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UseAbilityMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "use_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UseAbilityMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, UseAbilityMessage::entityID,
            ByteBufCodecs.VAR_INT, UseAbilityMessage::index,
            UseAbilityMessage::new
    );

    public UseAbilityMessage(LivingEntity entity, int index) {
        this(entity.getId(), index);
    }

    @Override
    public CustomPacketPayload.Type<UseAbilityMessage> type() {
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
    public static void handle(final UseAbilityMessage message, final net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) {
            context.client().execute(() -> {
                Level level = context.client().level;
                if (level != null) {
                    Entity entity = level.getEntity(message.entityID());
                    if (entity instanceof LivingEntity livingEntity) {
                        AbilityCapability.IAbilityCapability livingCapability = CapabilityHandler.getCapability(livingEntity, CapabilityHandler.ABILITY_CAPABILITY);
                        if (livingCapability != null) {
                            livingCapability.onActive(livingEntity, livingCapability.getAbilityTypeByEntity(livingEntity)[message.index()]);
                        }
                    }
                }
            });
        }
    }
}
