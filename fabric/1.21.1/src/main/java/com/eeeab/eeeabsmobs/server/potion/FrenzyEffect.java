package com.eeeab.eeeabsmobs.server.potion;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.eeeab.eeeabsmobs.server.init.AttributeInit;

//狂乱
//1.21.1中addAttributeModifier的id参数从String(UUID)改为ResourceLocation；
//NeoForgeMod.SWIM_SPEED不存在于Fabric（vanilla LivingEntity没有游泳速度attribute钩子，
//NeoForge通过自己的补丁读取该attribute；Fabric/vanilla没有对应实现）。
//改为使用AttributeInit.SWIM_SPEED（本mod自己注册的attribute，与CRIT_CHANCE同机制），
//但由于没有NeoForge那样的游泳移动补丁，这个modifier目前不会真正影响游泳速度——
//已在AttributeInit中标注，等以后有mixin接入游泳移动逻辑再生效。
public class FrenzyEffect extends ModEffect {
    public FrenzyEffect() {
        super(MobEffectCategory.NEUTRAL, 16711680, false);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "frenzy_attack_damage"), 1, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "frenzy_armor"), 1, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "frenzy_movement_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(AttributeInit.SWIM_SPEED.holder(), ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "frenzy_swim_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
