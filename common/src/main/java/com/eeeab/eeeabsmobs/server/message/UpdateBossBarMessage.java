package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.client.ClientProxy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record UpdateBossBarMessage(UUID bossID, @Nullable ResourceLocation registryName,
                                    boolean remove) {

    public UpdateBossBarMessage(UUID bossID, @Nullable LivingEntity entity) {
        this(bossID, entity != null ? BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()) : null, entity == null);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.bossID);
        buffer.writeBoolean(this.registryName != null);
        if (this.registryName != null) {
            buffer.writeResourceLocation(this.registryName);
        }
        buffer.writeBoolean(this.remove);
    }

    public static UpdateBossBarMessage decode(FriendlyByteBuf buffer) {
        UUID bossID = buffer.readUUID();
        ResourceLocation registryName = buffer.readBoolean() ? buffer.readResourceLocation() : null;
        boolean remove = buffer.readBoolean();
        return new UpdateBossBarMessage(bossID, registryName, remove);
    }

    public static void handleClient(UpdateBossBarMessage message) {
        if (message.registryName() == null) ClientProxy.BOSS_BAR_REGISTRY_NAMES.remove(message.bossID());
        else ClientProxy.BOSS_BAR_REGISTRY_NAMES.put(message.bossID(), message.registryName());
    }
}
