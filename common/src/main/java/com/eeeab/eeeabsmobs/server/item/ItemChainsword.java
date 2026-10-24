package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemChainsword extends SwordItem implements ConfigurableItem, SlidingDoorLockKey {
    private final float damage;
    private Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ItemChainsword(Tier tier, Properties properties) {
        super(tier, 3, -2.4F, properties);
        this.damage = (float) ModConfigHandler.COMMON.items.chainsword.attackDamageValue;
        this.defaultModifiers = this.creatAttributesFromConfig();
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level, List<Component> tooltip, TooltipFlag flagIn) {
        ModConfigHandler.Item items = ModConfigHandler.COMMON.items;
        tooltip.add(TranslateUtils.simpleItemText(this.getDescriptionId(), Component.literal("+" + items.chainswordConfig1.get() + "%"), Component.literal(items.chainswordConfig2.get() + "")));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> creatAttributesFromConfig() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", ModConfigHandler.COMMON.items.chainsword.attackDamageValue - 1D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", ModConfigHandler.COMMON.items.chainsword.attackSpeedValue - 4D, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    @Override
    public void refreshAttributesFromConfig() {
        this.defaultModifiers = this.creatAttributesFromConfig();
    }

    @Override
    public int getKeyLevel() {
        return 1;
    }
}
