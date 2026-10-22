package com.eeeab.eeeabsmobs.server.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ModEffect extends MobEffect {
    private final boolean instant;

    public ModEffect(MobEffectCategory type, int color, boolean isInstant) {
        super(type, color);
        this.instant = isInstant;
    }

    //1.21.1中isDurationEffectTick被重命名为shouldApplyEffectTickThisTick，
    //applyEffectTick从void变为返回boolean(是否继续生效)，逻辑保持不变。
    //1.21.1中applyInstantenousEffect签名增加前两个参数：(ServerLevel, Entity)，
    //因当前上下文无ServerLevel/来源Entity可用，传null占位（原行为）。
    @Override
    public boolean shouldApplyEffectTickThisTick(int remainingTicks, int level) {
        if (isInstantenous()) {
            return true;
        }
        return canApplyEffect(remainingTicks, level);
    }

    @Override
    public boolean applyEffectTick(net.minecraft.server.level.ServerLevel level, LivingEntity entity, int amplifier) {
        if (isInstantenous()) {
            // 1.21.11: applyInstantenousEffect(ServerLevel, Entity, Entity, LivingEntity, int, double)
            applyInstantenousEffect(level, entity, entity, entity, amplifier, 1.0d);
        }
        return true;
    }

    @Override
    public boolean isInstantenous() {
        return instant;
    }

    protected boolean canApplyEffect(int remainingTicks, int level) {
        return false;
    }
}
