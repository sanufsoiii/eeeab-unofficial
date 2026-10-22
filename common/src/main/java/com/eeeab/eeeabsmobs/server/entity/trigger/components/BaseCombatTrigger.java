package com.eeeab.eeeabsmobs.server.entity.trigger.components;

import com.eeeab.eeeabsmobs.server.entity.trigger.CombatTrigger;
import com.eeeab.eeeabsmobs.server.message.ModNetwork;
import com.eeeab.eeeabsmobs.server.message.PopupNotificationMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public abstract class BaseCombatTrigger implements CombatTrigger {
    protected final ResourceLocation entityId;
    protected final boolean oncePerEncounter;
    protected ResourceLocation triggerId;
    protected int level;

    public BaseCombatTrigger(ResourceLocation triggerId, ResourceLocation entityId, int level, boolean oncePerEncounter) {
        this.triggerId = triggerId;
        this.entityId = entityId;
        this.level = level;
        this.oncePerEncounter = oncePerEncounter;
    }

    @Override
    public void trigger(ServerPlayer player) {
        ModNetwork.sendToPlayer(player, new PopupNotificationMessage(getTriggerKey(), level));
        //if (!oncePerEncounter) CooldownManager.setCooldown(player.getUUID(), getTriggerKey());
    }

    @Override
    public ResourceLocation getTriggerKey() {
        return triggerId;
    }

    @Override
    public ResourceLocation getEntityKey() {
        return entityId;
    }

    @Override
    public boolean isOncePerEncounter() {
        return oncePerEncounter;
    }

    public void setTriggerKey(ResourceLocation triggerId) {
        this.triggerId = triggerId;
    }

    public void setMsgLevel(int promptLevel) {
        this.level = promptLevel;
    }
}