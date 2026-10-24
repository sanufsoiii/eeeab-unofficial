package com.eeeab.eeeabsmobs.server.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class ItemNetherworldKatana extends SwordItem implements ConfigurableItem, IUnbreakableItem {

    public ItemNetherworldKatana(Tier tier, Properties properties) {
        super(tier, 8, -2.6F, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> creatAttributesFromConfig() {
        return ImmutableMultimap.of();
    }

    @Override
    public void refreshAttributesFromConfig() {
    }

    @Override
    public boolean canBreakItem() {
        return false;
    }
}
