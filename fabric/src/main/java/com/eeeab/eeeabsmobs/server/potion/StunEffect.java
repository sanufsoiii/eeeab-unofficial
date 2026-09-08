package com.eeeab.eeeabsmobs.server.potion;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

//眩晕
public class StunEffect extends ModEffect {
    public StunEffect() {
        super(MobEffectCategory.HARMFUL, 13434675, false);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "stun_movement_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
