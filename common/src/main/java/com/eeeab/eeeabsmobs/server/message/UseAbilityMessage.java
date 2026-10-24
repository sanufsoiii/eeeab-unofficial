package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.server.capability.AbilityCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public record UseAbilityMessage(int entityID, int index) {

    public UseAbilityMessage(LivingEntity entity, int index) {
        this(entity.getId(), index);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityID);
        buffer.writeVarInt(this.index);
    }

    public static UseAbilityMessage decode(FriendlyByteBuf buffer) {
        return new UseAbilityMessage(buffer.readVarInt(), buffer.readVarInt());
    }

    public static void handleClient(UseAbilityMessage message, Level level) {
        if (level != null) {
            Entity entity = level.getEntity(message.entityID());
            if (entity instanceof LivingEntity livingEntity) {
                AbilityCapability.IAbilityCapability livingCapability = CapabilityHandler.getCapability(livingEntity, CapabilityHandler.ABILITY_CAPABILITY);
                if (livingCapability != null) {
                    livingCapability.onActive(livingEntity, livingCapability.getAbilityTypeByEntity(livingEntity)[message.index()]);
                }
            }
        }
    }
}
