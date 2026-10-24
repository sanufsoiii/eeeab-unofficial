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
    @Override
    public boolean shouldApplyEffectTickThisTick(int remainingTicks, int level) {
        if (isInstantenous()) {
            return true;
        }
        return canApplyEffect(remainingTicks, level);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (isInstantenous()) {
            applyInstantenousEffect(null, null, entity, amplifier, 1.0d);
        }
    }

    @Override
    public boolean isInstantenous() {
        return instant;
    }

    protected boolean canApplyEffect(int remainingTicks, int level) {
        return false;
    }

    public MobEffect addAttributeModifier(net.minecraft.world.entity.ai.attributes.Attribute attribute, net.minecraft.resources.ResourceLocation id, double amount, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation operation) {
        return super.addAttributeModifier(attribute, java.util.UUID.nameUUIDFromBytes(id.toString().getBytes()).toString(), amount, operation);
    }
}


