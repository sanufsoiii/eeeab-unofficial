package com.eeeab.eeeabsmobs.server.entity.trigger.components;

import com.eeeab.eeeabsmobs.server.entity.trigger.CombatTrigger;
import com.eeeab.eeeabsmobs.server.message.PopupNotificationMessage;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public abstract class BaseCombatTrigger implements CombatTrigger {
    protected final Identifier entityId;
    protected final boolean oncePerEncounter;
    protected Identifier triggerId;
    protected int level;

    public BaseCombatTrigger(Identifier triggerId, Identifier entityId, int level, boolean oncePerEncounter) {
        this.triggerId = triggerId;
        this.entityId = entityId;
        this.level = level;
        this.oncePerEncounter = oncePerEncounter;
    }

    @Override
    public void trigger(ServerPlayer player) {
        ServerPlayNetworking.send(player, new PopupNotificationMessage(getTriggerKey(), level));
        //if (!oncePerEncounter) CooldownManager.setCooldown(player.getUUID(), getTriggerKey());
    }

    @Override
    public Identifier getTriggerKey() {
        return triggerId;
    }

    @Override
    public Identifier getEntityKey() {
        return entityId;
    }

    @Override
    public boolean isOncePerEncounter() {
        return oncePerEncounter;
    }

    public void setTriggerKey(Identifier triggerId) {
        this.triggerId = triggerId;
    }

    public void setMsgLevel(int promptLevel) {
        this.level = promptLevel;
    }
}