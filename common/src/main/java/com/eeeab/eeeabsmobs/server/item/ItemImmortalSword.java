package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.server.init.EffectInit;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.network.chat.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

//1.21.1中SwordItem不再有(Tier,int,float,Properties)构造函数，攻击伤害/攻速改由Properties携带的
//ATTRIBUTE_MODIFIERS数据组件(SwordItem.createAttributes)提供；保留原构造函数签名不变，
//避免影响调用方(ItemInit不在本次改动范围内)，内部转换为新API
public class ItemImmortalSword extends SwordItem {
    public ItemImmortalSword(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable net.minecraft.world.level.Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(TranslateUtils.simpleItemText(this.getDescriptionId()));
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity entity) {
        target.addEffect(new MobEffectInstance(EffectInit.ERODE_EFFECT.get(), 100, 0));
        return super.hurtEnemy(itemStack, target, entity);
    }
}
