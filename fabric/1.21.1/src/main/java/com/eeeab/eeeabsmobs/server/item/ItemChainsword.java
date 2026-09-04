package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.core.component.DataComponents;
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
        // getDefaultAttributeModifiers(ItemStack) (NeoForge IItemExtension - dynamically re-queried per stack)
        // doesn't exist on vanilla Item; DataComponents.ATTRIBUTE_MODIFIERS is instead a fixed component baked
        // into the item's default template once, at construction (same approach as ItemImmortalSword/
        // ItemNetherworldKatana elsewhere in this package). Config values are read here at mod init.
        super(tier, properties.component(DataComponents.ATTRIBUTE_MODIFIERS, buildDefaultAttributes()));
        this.damage = (float) ModConfigHandler.COMMON.items.chainsword.attackDamageValue;
        this.defaultModifiers = this.creatAttributesFromConfig();
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        ModConfigHandler.Item items = ModConfigHandler.COMMON.items;
        tooltip.add(TranslateUtils.simpleItemText(this.getDescriptionId(), Component.literal("+" + items.chainswordConfig1.get() + "%"), Component.literal(items.chainswordConfig2.get() + "")));
    }

    private static ItemAttributeModifiers buildDefaultAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, ModConfigHandler.COMMON.items.chainsword.attackDamageValue - 1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, ModConfigHandler.COMMON.items.chainsword.attackSpeedValue - 4D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "chainsword_entity_reach"), 1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public ItemAttributeModifiers creatAttributesFromConfig() {
        return buildDefaultAttributes();
    }

    // refreshAttributesFromConfig() (called from ItemInit on config reload) can no longer actually change this
    // item's attributes - DataComponents.ATTRIBUTE_MODIFIERS is baked into the item's default stack template
    // once, in the constructor above, and vanilla has no per-query override point like NeoForge's
    // getDefaultAttributeModifiers(ItemStack) to make it dynamic again. Kept as a no-op (rather than deleted)
    // only so ItemInit's existing reload call site still compiles; config changes to chainsword's attack
    // stats now require a restart to take effect, unlike on NeoForge.
    @Override
    public void refreshAttributesFromConfig() {
        this.defaultModifiers = this.creatAttributesFromConfig();
    }

    @Override
    public int getKeyLevel() {
        return 1;
    }
}
