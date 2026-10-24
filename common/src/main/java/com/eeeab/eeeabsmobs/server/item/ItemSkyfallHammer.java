package com.eeeab.eeeabsmobs.server.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.equipment.ArmorMaterial;

//1.21.1中PickaxeItem已移除，所有工具类物品统一继承Item；
import net.minecraft.world.item.PickaxeItem;

public class ItemSkyfallHammer extends PickaxeItem {
    public ItemSkyfallHammer(ToolMaterial tier, Properties properties) {
        super(tier, 8F, -2.8F, properties);
    }

    //@Override
    //public InteractionResult use(Level world, Player player, InteractionHand hand) {
    //    ItemStack stack = player.getMainHandItem();
    //    if (!world.isClientSide() && stack.is(this)) {
    //        if (SkyfallHammerAbility.canPlayerUseAbility(player)) {
    //            AbilityHandler.INSTANCE.sendAbilityMessage(player, AbilityHandler.SKYFALL_HAMMER_ABILITY);
    //            player.getCooldowns().addCooldown(new net.minecraft.world.item.ItemStack(this), 20);
    //            return InteractionResult.SUCCESS;
    //        }
    //    }
    //    return InteractionResult.PASS;
    //}

    // canPerformAction(ItemStack, ItemAbility) removed - net.neoforged.neoforge.common.ItemAbility/ItemAbilities
    // is a NeoForge-only IItemExtension hook (used here to make the hammer usable for sword-only actions), no
    // vanilla/Fabric equivalent exists and nothing outside NeoForge itself calls it. The hammer's own pickaxe
    // behaviour is unaffected; only interactions that specifically consult a sword ItemAbility no longer treat
    // this hammer as a sword too (matching the same precedent as ItemGuardianAxe below).
}