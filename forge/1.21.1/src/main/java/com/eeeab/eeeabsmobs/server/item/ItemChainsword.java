package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

//1.21.1中不再有单独的"实体交互距离"Forge属性(ForgeMod.ENTITY_REACH)，用vanilla的Attributes.ENTITY_INTERACTION_RANGE代替，
//语义相同(玩家可交互/攻击实体的距离)。属性修饰符系统整体迁移到ItemAttributeModifiers数据组件，见ConfigurableItem注释。
public class ItemChainsword extends SwordItem implements ConfigurableItem, SlidingDoorLockKey {
    private final float damage;
    private ItemAttributeModifiers defaultModifiers;

    public ItemChainsword(Tier tier, Properties properties) {
        super(tier, properties);
        this.damage = (float) ModConfigHandler.COMMON.items.chainsword.attackDamageValue;
        this.defaultModifiers = this.creatAttributesFromConfig();
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return this.defaultModifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        ModConfigHandler.Item items = ModConfigHandler.COMMON.items;
        tooltip.add(TranslateUtils.simpleItemText(this.getDescriptionId(), Component.literal("+" + items.chainswordConfig1.get() + "%"), Component.literal(items.chainswordConfig2.get() + "")));
    }

    @Override
    public ItemAttributeModifiers creatAttributesFromConfig() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, ModConfigHandler.COMMON.items.chainsword.attackDamageValue - 1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, ModConfigHandler.COMMON.items.chainsword.attackSpeedValue - 4D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "chainsword_entity_reach"), 1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
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
