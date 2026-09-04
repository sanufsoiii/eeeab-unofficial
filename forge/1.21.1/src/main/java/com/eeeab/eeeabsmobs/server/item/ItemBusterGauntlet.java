package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.animate.client.util.ItemAnimationUtils;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityPulsedGrenade;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.init.SoundInit;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.function.Consumer;

//1.21.1中Item不再提供getDefaultAttributeModifiers(EquipmentSlot)这种按槽位查询的Multimap钩子，
//属性修饰符改为不可变的ItemAttributeModifiers数据组件；为保留"可在运行时按配置刷新"的行为，
//改用NeoForge扩展方法IItemExtension#getDefaultAttributeModifiers(ItemStack)，
//该方法在每次查询时被调用(而非仅构造时读取一次组件)，因此defaultModifiers字段依然可以被refreshAttributesFromConfig()动态替换。
public class ItemBusterGauntlet extends SwordItem implements ConfigurableItem {
    private final ItemAttributeModifiers offHandModifiers;
    private ItemAttributeModifiers defaultModifiers;

    public ItemBusterGauntlet(Tier tier, Properties properties) {
        super(tier, properties);
        this.offHandModifiers = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "buster_gauntlet_offhand_attack_speed"), 0.1D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.OFFHAND)
                .build();
        this.defaultModifiers = creatAttributesFromConfig();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        //InteractionHand anotherHand = usedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        //ItemStack anotherStack = player.getItemInHand(anotherHand);
        //if (anotherStack.canPerformAction(ToolActions.SHIELD_BLOCK) && !player.getCooldowns().isOnCooldown(anotherStack.getItem())) {
        //    return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        //}
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemstack);
        }
        player.getCooldowns().addCooldown(this, (int) (ModConfigHandler.COMMON.items.busterGauntletConfig3.get() * 20));
        player.startUsingItem(usedHand);
        if (level.isClientSide) ItemAnimationUtils.start(player.getItemInHand(usedHand), player.tickCount);
        else {
            double yBodyRadians = Math.toRadians(player.yHeadRot + (180 * (player.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1 : 2)));
            float width = player.getBbWidth();
            EntityPulsedGrenade grenade = new EntityPulsedGrenade(level, player, true);
            grenade.setRadius(ModConfigHandler.COMMON.items.busterGauntletConfig2.get().floatValue());
            Vec3 lookPos = player.getLookAngle();
            Vec3 playerPos = player.position();
            grenade.shoot(lookPos.x, lookPos.y, lookPos.z, 0.7F, 1F);
            grenade.setPos(playerPos.x + width * 0.7F * Math.cos(yBodyRadians), player.getY(0.6D), playerPos.z + width * 0.7F * Math.sin(yBodyRadians));
            level.addFreshEntity(grenade);
            itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(usedHand));
        }
        player.playSound(SoundInit.PULSED_GRENADE_LAUNCH.get());
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (TranslateUtils.SHOW_ITEM_CD) tooltip.add(TranslateUtils.itemCoolTime(ModConfigHandler.COMMON.items.busterGauntletConfig3.get()));
        tooltip.add(TranslateUtils.simpleItemText(this.getDescriptionId(), ModConfigHandler.COMMON.items.busterGauntletConfig1.get()));
    }

    //取代旧的Item#getDefaultAttributeModifiers(EquipmentSlot)：NeoForge的IItemExtension钩子按ItemStack查询整份属性组件，
    //主手用可配置的defaultModifiers，副手用固定的offHandModifiers，合并成一份ItemAttributeModifiers返回。
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (ItemAttributeModifiers.Entry entry : this.defaultModifiers.modifiers()) {
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }
        for (ItemAttributeModifiers.Entry entry : this.offHandModifiers.modifiers()) {
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }
        return builder.build();
    }

    @Override
    public ItemAttributeModifiers creatAttributesFromConfig() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, ModConfigHandler.COMMON.items.busterGauntlet.attackDamageValue - 1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, ModConfigHandler.COMMON.items.busterGauntlet.attackSpeedValue - 4D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void refreshAttributesFromConfig() {
        this.defaultModifiers = creatAttributesFromConfig();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) EEEABMobs.PROXY.getISTERProperties());
    }
}
