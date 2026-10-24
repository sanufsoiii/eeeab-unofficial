package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.server.ability.Ability;
import com.eeeab.eeeabsmobs.server.ability.AbilityHandler;
import com.eeeab.eeeabsmobs.server.block.BlockVoidCrackPortalLock;
import com.eeeab.eeeabsmobs.server.capability.AbilityCapability;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.init.SoundInit;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

//1.21.1中Item.Properties#defaultDurability已移除，统一用durability(int)设置最大耐久(会自动令物品可损耗)；
//canBeDepleted()钩子已移除，durability(int)本身即令物品具备可损耗的MAX_DAMAGE数据组件，无需再单独声明
public class ItemGuardianCore extends Item {

    public ItemGuardianCore(net.minecraft.world.item.Item.Properties p) {
        super(p.rarity(Rarity.EPIC).durability(100).fireResistant());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        AbilityCapability.IAbilityCapability capability = AbilityHandler.INSTANCE.getAbilityCapability(player);
        ItemStack itemStack = player.getItemInHand(hand);
        if (capability != null) {
            player.startUsingItem(hand);
            if (itemStack.getDamageValue() + 1 < itemStack.getMaxDamage()) {
                if (!level.isClientSide())
                    AbilityHandler.INSTANCE.sendAbilityMessage(player, AbilityHandler.GUARDIAN_LASER_ABILITY);
                itemStack.hurtAndBreak(1, player, (hand == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND));
                player.playSound(SoundInit.NAMELESS_GUARDIAN_ACCUMULATING.get(), 1.5F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 3.5F);
                player.startUsingItem(hand);
                return InteractionResult.CONSUME;
            } else {
                try {
                    Optional.ofNullable(capability.getAbilitiesMap().get(AbilityHandler.GUARDIAN_LASER_ABILITY)).orElseThrow().end();
                } catch (Exception ignored) {
                    //EEEABMobs.LOGGER.error("An unforeseen error has occurred! Item Id: '{}', Exception Stack Msg: ", this.getDescriptionId(itemStack), e);
                }
            }
        }
        return super.use(level, player, hand);
    }

    //1.21.1中releaseUsing返回类型改为boolean
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player) {
            Ability<?> guardianLaserAbility = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.GUARDIAN_LASER_ABILITY);
            if (guardianLaserAbility != null && guardianLaserAbility.isUsing()) {
                guardianLaserAbility.end();
            }
        }
        return super.releaseUsing(stack, level, livingEntity, timeCharged);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player entity = context.getPlayer();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ItemStack itemstack = context.getItemInHand();
        Level world = context.getLevel();
        if (entity != null && entity.mayUseItemAt(pos, context.getClickedFace(), itemstack) && itemstack.getDamageValue() == 0) {
            BlockState clickedState = world.getBlockState(pos);
            if (clickedState.getBlock() instanceof BlockVoidCrackPortalLock) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

        public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(TranslateUtils.simpleItemText(this.getDescriptionId(), Component.literal(ModConfigHandler.COMMON.items.guardianCoreConfig1.get() + "").withStyle(ChatFormatting.YELLOW)));
        //TranslateUtils.complexText(TranslateUtils.ITEM_PREFIX, 2, ChatFormatting.GRAY, this.getDescriptionId()).forEach(tooltip::add);
        tooltip.addAll(TranslateUtils.complexText(TranslateUtils.ITEM_PREFIX, 1, ChatFormatting.GRAY, this.getDescriptionId()));
    }

    // initializeClient/IClientItemExtensions#getArmPose removed: a per-item held-arm-pose override
    // (crossbow-hold pose while using this item) has no Fabric API equivalent and would need a mixin into
    // HumanoidArmorLayer/ItemInHandRenderer's pose selection to reproduce - out of reach without one, so this
    // is a real, deliberate loss: the arm pose while using Guardian Core falls back to vanilla's default
    // ITEM pose instead of CROSSBOW_HOLD. Everything else about the item (the ability itself, in use()
    // above) is unaffected - purely cosmetic.
}
