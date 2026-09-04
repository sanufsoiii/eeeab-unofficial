package com.eeeab.eeeabsmobs.server.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.ToolAction;

//1.21.1中PickaxeItem不再有(Tier,int,float,Properties)构造函数，攻击伤害/攻速固定值(8, -2.8F)改由
//Properties携带的ATTRIBUTE_MODIFIERS数据组件(DiggerItem.createAttributes)提供；
//ToolAction/ToolActions重命名为ItemAbility/ToolActions
public class ItemSkyfallHammer extends PickaxeItem {
    public ItemSkyfallHammer(Tier tier, Properties properties) {
        super(tier, properties.component(DataComponents.ATTRIBUTE_MODIFIERS, DiggerItem.createAttributes(tier, 8, -2.8F)));
    }

    //@Override
    //public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    //    ItemStack stack = player.getMainHandItem();
    //    if (!world.isClientSide && stack.is(this)) {
    //        if (SkyfallHammerAbility.canPlayerUseAbility(player)) {
    //            AbilityHandler.INSTANCE.sendAbilityMessage(player, AbilityHandler.SKYFALL_HAMMER_ABILITY);
    //            player.getCooldowns().addCooldown(this, 20);
    //            return InteractionResultHolder.success(stack);
    //        }
    //    }
    //    return InteractionResultHolder.pass(player.getItemInHand(hand));
    //}

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction itemAbility) {
        return super.canPerformAction(stack, itemAbility) || ToolActions.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
    }
}
