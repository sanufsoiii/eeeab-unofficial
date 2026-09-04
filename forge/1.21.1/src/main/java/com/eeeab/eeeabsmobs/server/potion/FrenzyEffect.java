package com.eeeab.eeeabsmobs.server.potion;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

//狂乱
//1.21.1中addAttributeModifier的id参数从String(UUID)改为ResourceLocation；
//Forge没有NeoForgeMod，是ForgeMod；SWIM_SPEED仍是RegistryObject<Attribute>，需要.getHolder().orElseThrow()
public class FrenzyEffect extends ModEffect {
    public FrenzyEffect() {
        super(MobEffectCategory.NEUTRAL, 16711680, false);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "frenzy_attack_damage"), 1, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "frenzy_armor"), 1, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "frenzy_movement_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(ForgeMod.SWIM_SPEED.getHolder().orElseThrow(), ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "frenzy_swim_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
