package com.eeeab.eeeabsmobs.server.entity.trigger;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public interface CombatTrigger {
    /**
     * 是否应该触发提示
     */
    boolean shouldTrigger(ServerPlayer player, Identifier entityId, TriggerContext context);
    
    /**
     * 触发提示
     */
    void trigger(ServerPlayer player);

    /**
     * @return 返回触发Key
     */
    Identifier getTriggerKey();

    /**
     * @return 返回实体Key
     */
    Identifier getEntityKey();
    
    /**
     * 是否为单次游戏中只触发一次
     */
    default boolean isOncePerEncounter() {
        return true;
    }
}
