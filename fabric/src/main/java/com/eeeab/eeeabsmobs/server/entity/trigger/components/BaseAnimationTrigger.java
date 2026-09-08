package com.eeeab.eeeabsmobs.server.entity.trigger.components;

import com.eeeab.animate.server.animation.AnimationNotification;
import com.eeeab.eeeabsmobs.server.entity.trigger.TriggerContext;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.Strings;

public class BaseAnimationTrigger extends BaseCombatTrigger {
    public BaseAnimationTrigger(Identifier entityId, boolean oncePerEncounter) {
        super(null, entityId, 0, oncePerEncounter);
    }

    @Override
    public boolean shouldTrigger(ServerPlayer player, Identifier entityId, TriggerContext context) {
        if (getTriggerKey() == null) {
            if (context.getAnimation() instanceof AnimationNotification animation) {
                String msgId = animation.getMsgId();
                setTriggerKey(Strings.isNotBlank(msgId) ? entityId.withSuffix("_" + msgId) : entityId);
                setMsgLevel(animation.getLevel());
            }
        }
        return getTriggerKey() != null;
    }
}
