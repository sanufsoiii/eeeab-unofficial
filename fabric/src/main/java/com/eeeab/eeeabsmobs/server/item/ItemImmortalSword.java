package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.server.init.EffectInit;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

//1.21.1中SwordItem不再有(Tier,int,float,Properties)构造函数，攻击伤害/攻速改由Properties携带的
//ATTRIBUTE_MODIFIERS数据组件(SwordItem.createAttributes)提供；保留原构造函数签名不变，
//避免影响调用方(ItemInit不在本次改动范围内)，内部转换为新API
public class ItemImmortalSword extends Item {
    public ItemImmortalSword(ToolMaterial tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(properties.sword(tier, (float) attackDamageModifier, attackSpeedModifier));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        tooltip.accept(TranslateUtils.simpleItemText(this.getDescriptionId()));
    }

    @Override
    public void hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity entity) {
        target.addEffect(new MobEffectInstance(EffectInit.ERODE_EFFECT.holder(), 100, 0));
        super.hurtEnemy(itemStack, target, entity);
    }
}
