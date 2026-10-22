package com.eeeab.eeeabsmobs.server.entity.trigger.components;

import com.eeeab.eeeabsmobs.server.entity.trigger.TriggerContext;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class FirstEncounterTrigger extends BaseCombatTrigger {
    public FirstEncounterTrigger(Identifier triggerId, Identifier entityId, int promptLevel) {
        super(triggerId, entityId, promptLevel, true);
    }

    @Override
    public boolean shouldTrigger(ServerPlayer player, Identifier entityId, TriggerContext context) {
        return context.isFirstEncounter();
    }
}