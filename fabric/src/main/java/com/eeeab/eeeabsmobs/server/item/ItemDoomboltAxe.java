package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityCameraShake;
import com.eeeab.eeeabsmobs.server.entity.effect.EntitySurge;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityThrownDoomboltAxe;
import com.eeeab.eeeabsmobs.server.entity.util.ModEntityUtils;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.init.SoundInit;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

//属性修饰符系统迁移到ItemAttributeModifiers数据组件，见ConfigurableItem注释；
//ForgeMod.ENTITY_REACH -> vanilla的Attributes.ENTITY_INTERACTION_RANGE
public class ItemDoomboltAxe extends Item implements ConfigurableItem, SlidingDoorLockKey {
    private ItemAttributeModifiers defaultModifiers;

    public ItemDoomboltAxe(ToolMaterial tier, Properties properties) {
        // getDefaultAttributeModifiers(ItemStack) has no vanilla equivalent; baked into the default template
        // once here instead - see ItemChainsword for the full explanation.
        super(properties.axe(tier, 0F, 0F).component(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, buildDefaultAttributes()));
        this.defaultModifiers = this.creatAttributesFromConfig();
    }

    // initializeClient(NeoForge-only client-extension callback) removed - BEWLR rendering for this
    // item is now wired directly in ClientProxy via Fabric API's BuiltinItemRendererRegistry.

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
            if (HitResult.Type.BLOCK == result.getType() && Direction.UP == result.getDirection()) {
                player.swing(hand, true);
                player.getCooldowns().addCooldown(new net.minecraft.world.item.ItemStack(this), (int) (ModConfigHandler.COMMON.items.doomboltAxeConfig.get() * 20));
                if (!level.isClientSide()) {
                    Vec3 lookAngle = player.getLookAngle();
                    Vec3[] vec3s = new Vec3[]{lookAngle.yRot(0.5F), lookAngle, lookAngle.yRot(-0.5F)};
                    double reach = 2;
                    int baseCount = 5;
                    double spacing = 1.2D;
                    for (Vec3 vec3 : vec3s) {
                        for (int i = 0; i < baseCount; i++) {
                            double distance = spacing * (i + 1.5);
                            float f0 = (float) Mth.atan2(vec3.z, vec3.x);
                            double x = player.getX() + Mth.cos(f0) * distance;
                            double y = player.getY();
                            double z = player.getZ() + Mth.sin(f0) * distance;
                            Vec3 point = ModEntityUtils.checkSummonEntityPointNullable(player.level(), x, z, y - reach, y + reach);
                            if (point == null) point = new Vec3(x, y, z);
                            EntitySurge surge = new EntitySurge(player.level(), point.x, point.y, point.z, f0, i * 4, itemStack.copy(), player);
                            player.level().addFreshEntity(surge);
                        }
                    }
                }
                itemStack.hurtAndBreak(1, player, (hand == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND));
                player.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1F, 1F + player.getRandom().nextFloat() * 0.1F);
                EntityCameraShake.cameraShake(level, player.position(), 6, 0.15F, 3, 12);
                return InteractionResult.CONSUME;
            }
        } else {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int i = this.getUseDuration(stack, entity) - timeLeft;
            if (i >= 10) {
                if (!level.isClientSide()) {
                    if (stack.getDamageValue() >= stack.getMaxDamage() - 1) return false;
                    stack.hurtAndBreak(1, player, (entity.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND));
                    EntityThrownDoomboltAxe doomboltAxe = new EntityThrownDoomboltAxe(level, player, stack);
                    doomboltAxe.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4F, 1.0F);
                    if (player.getAbilities().instabuild) {
                        doomboltAxe.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                    level.addFreshEntity(doomboltAxe);
                    level.playSound(null, doomboltAxe, SoundInit.DOOMBOLTAXE_THROW.get(), SoundSource.PLAYERS, 1.0F, 0.7F);
                    if (!player.getAbilities().instabuild) {
                        player.getInventory().removeItem(stack);
                    }
                    EntityCameraShake.cameraShake(level, player.position(), 4, 0.15F, 2, 8);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        // 1.21.11: the throw/charge arm pose (ArmPose.THROW_TRIDENT) is driven by ItemUseAnimation.TRIDENT.
        // SPEAR has no case in AvatarRenderer.getArmPose, so it produced no pose — the old UseAnim.SPEAR maps
        // to TRIDENT here (this is exactly what vanilla TridentItem returns).
        return ItemUseAnimation.TRIDENT;
    }

    @Override
    public void hurtEnemy(ItemStack itemStack, LivingEntity hitEntity, LivingEntity livingEntity) {
        if (!hitEntity.level().isClientSide()) {
            hitEntity.playSound(SoundInit.GIANT_AXE_HIT.get(), 1F, 0.2F);
        }
        super.hurtEnemy(itemStack, hitEntity, livingEntity);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (TranslateUtils.SHOW_ITEM_CD) tooltip.accept(TranslateUtils.itemCoolTime(ModConfigHandler.COMMON.items.doomboltAxeConfig.get()));
        TranslateUtils.complexText(TranslateUtils.ITEM_PREFIX, 2, ChatFormatting.GRAY, this.getDescriptionId()).forEach(tooltip);
    }

    private static ItemAttributeModifiers buildDefaultAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, ModConfigHandler.COMMON.items.doomboltAxe.attackDamageValue - 1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, ModConfigHandler.COMMON.items.doomboltAxe.attackSpeedValue - 4D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "doombolt_axe_entity_reach"), 1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public ItemAttributeModifiers creatAttributesFromConfig() {
        return buildDefaultAttributes();
    }

    @Override
    public void refreshAttributesFromConfig() {
        this.defaultModifiers = this.creatAttributesFromConfig();
    }
}
