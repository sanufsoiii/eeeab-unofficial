package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemSoulSummonNecklace extends Item implements ICuriosItem {
    public ItemSoulSummonNecklace(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        ModConfigHandler.Item item = ModConfigHandler.COMMON.items;
        if (TranslateUtils.SHOW_ITEM_CD) tooltip.accept(TranslateUtils.itemCoolTime(item.summoningSoulNecklaceConfig2.get()));
        tooltip.accept(TranslateUtils.simpleItemText(this.getDescriptionId(), item.summoningSoulNecklaceConfig1.get()));
    }
}
