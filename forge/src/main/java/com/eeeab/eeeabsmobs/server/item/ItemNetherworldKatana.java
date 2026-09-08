package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

//1.21.1中SwordItem不再有(Tier,int,float,Properties)构造函数，攻击伤害/攻速固定值(8, -2.6F)改由
//Properties携带的ATTRIBUTE_MODIFIERS数据组件(SwordItem.createAttributes)提供，其余按配置刷新的逻辑此前已被注释掉，保持原样
public class ItemNetherworldKatana extends SwordItem implements ConfigurableItem, IUnbreakableItem {
    //private ItemAttributeModifiers defaultModifiers;

    public ItemNetherworldKatana(Tier tier, Properties properties) {
        //super(tier, (int) (-3D + ModConfigHandler.COMMON.items.netherworldKatana.attackDamageValue), (float) (-4D + ModConfigHandler.COMMON.items.netherworldKatana.attackSpeedValue), properties);
        super(tier, properties.component(DataComponents.ATTRIBUTE_MODIFIERS, SwordItem.createAttributes(tier, 8, -2.6F)));
        //this.defaultModifiers = this.creatAttributesFromConfig();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) EEEABMobs.PROXY.getISTERProperties());
    }

    //@Override
    //public ItemAttributeModifiers getDefaultAttributeModifiers(net.minecraft.world.item.ItemStack stack) {
    //    return this.defaultModifiers;
    //}

    @Override
    public ItemAttributeModifiers creatAttributesFromConfig() {
        //builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", ModConfigHandler.COMMON.items.netherworldKatana.attackDamageValue - 1D, AttributeModifier.Operation.ADDITION));
        //builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", ModConfigHandler.COMMON.items.netherworldKatana.attackSpeedValue - 4D, AttributeModifier.Operation.ADDITION));
        //builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(NETHERWORLD_KATANA_BASE_ENTITY_REACH_UUID, "Weapon modifier", 1.5D, AttributeModifier.Operation.ADDITION));
        return ItemAttributeModifiers.EMPTY;
    }

    @Override
    public void refreshAttributesFromConfig() {
        //this.defaultModifiers = this.creatAttributesFromConfig();
    }

    @Override
    public boolean canBreakItem() {
        //return ModConfigHandler.COMMON.items.ghostWarriorSeriesConfig1.get();
        return false;
    }
}
