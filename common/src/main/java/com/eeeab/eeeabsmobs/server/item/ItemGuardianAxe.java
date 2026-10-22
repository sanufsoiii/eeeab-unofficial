package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.particle.util.ModParticleUtils;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityCameraShake;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityGuardianBlade;
import com.eeeab.eeeabsmobs.server.entity.util.ModEntityUtils;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.init.SoundInit;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Objects;

//属性修饰符系统迁移到ItemAttributeModifiers数据组件；
//附魔不再是可继承子类(SweepingEdgeEnchantment已移除)，扫荡伤害比例公式(1-1/(level+1))直接内联；
//ToolAction/ToolActions重命名为ItemAbility/ItemAbilities；getEnchantmentLevel参数改为Holder<Enchantment>
public class ItemGuardianAxe extends Item implements ConfigurableItem, IUnbreakableItem {
    private ItemAttributeModifiers defaultModifiers;

    public ItemGuardianAxe(ToolMaterial tier, Properties properties) {
        // getDefaultAttributeModifiers(ItemStack) has no vanilla equivalent (NeoForge-only, dynamically
        // re-queried per stack); DataComponents.ATTRIBUTE_MODIFIERS is baked into the default template once
        // here instead, same approach as ItemChainsword/ItemImmortalSword elsewhere in this package.
        super(properties.axe(tier, 0F, 0F).component(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, buildDefaultAttributes()));
        this.defaultModifiers = this.creatAttributesFromConfig();
    }

    // initializeClient(NeoForge-only client-extension callback) removed - BEWLR rendering for this
    // item is now wired directly in ClientProxy via Fabric API's BuiltinItemRendererRegistry.

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (Objects.equals(hand, InteractionHand.MAIN_HAND) && HitResult.Type.BLOCK == result.getType() && Direction.UP == result.getDirection()) {
            player.swing(hand, true);
            player.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.5F, 1F + player.getRandom().nextFloat() * 0.1F);
            EntityCameraShake.cameraShake(level, player.position(), 8, 0.125F, 0, 20);
            double x, y, z;
            BlockPos blockPos = result.getBlockPos();
            if (player.position().distanceTo(blockPos.getCenter()) > 5) {
                float f0 = (float) Math.toRadians(player.getYRot() + 90);
                x = player.getX() + Mth.cos(f0) * 3.0D;
                y = player.getY() + 0.1D;
                z = player.getZ() + Mth.sin(f0) * 3.0D;
            } else {
                x = blockPos.getX();
                y = blockPos.getY();
                z = blockPos.getZ();
            }
            ModParticleUtils.roundParticleOutburst(player.level(), 40, new ParticleOptions[]{ParticleTypes.SOUL_FIRE_FLAME}, x, y, z, 0.3F);
            if (!level.isClientSide()) {
                Vec3 lookAngle = player.getLookAngle();
                Vec3[] vec3s = new Vec3[]{lookAngle.yRot(0.5F), lookAngle, lookAngle.yRot(-0.5F)};
                Vec3 point = ModEntityUtils.checkSummonEntityPoint(player, player.getX(), player.getZ(), y - 5, y);
                for (Vec3 vec3 : vec3s) {
                    float f0 = (float) Mth.atan2(vec3.z, vec3.x);
                    float f1 = 1F + player.getBbWidth();
                    x = point.x + Mth.cos(f0) * f1;
                    y = point.y;
                    z = point.z + Mth.sin(f0) * f1;
                    EntityGuardianBlade blade = new EntityGuardianBlade(player.level(), player, x, y, z, f0, false);
                    player.level().addFreshEntity(blade);
                }
            }
            player.getCooldowns().addCooldown(new net.minecraft.world.item.ItemStack(this), (int) (ModConfigHandler.COMMON.items.doomboltAxeConfig.get() * 20));
            return InteractionResult.SUCCESS;
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

    @Override
    public void hurtEnemy(ItemStack itemStack, LivingEntity hitEntity, LivingEntity livingEntity) {
        if (!hitEntity.level().isClientSide()) {
            hitEntity.playSound(SoundInit.GIANT_AXE_HIT.get(), 1F, 0.2F);
        }
        super.hurtEnemy(itemStack, hitEntity, livingEntity);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        if (TranslateUtils.SHOW_ITEM_CD) tooltip.accept(TranslateUtils.itemCoolTime(ModConfigHandler.COMMON.items.doomboltAxeConfig.get()));
        //int i = (int) (SweepingEdgeEnchantment.getSweepingDamageRatio(ModConfigHandler.COMMON.items.judgmentAxeConfig1.get()) * 100);
        //扫荡边缘附魔的伤害比例公式：1 - 1/(level+1)，SweepingEdgeEnchantment类在1.21.1中已移除(附魔不再是可继承子类)，故内联该公式
        int i = (int) ((1.0F - 1.0F / (1 + 1.0F)) * 100);
        TranslateUtils.complexText(TranslateUtils.ITEM_PREFIX, 2, ChatFormatting.GRAY, this.getDescriptionId(), Component.literal(i > 0 ? i + "%" : "1.0").withStyle(ChatFormatting.YELLOW)).forEach(tooltip);
    }

    // getEnchantmentLevel(ItemStack, Holder<Enchantment>) removed - NeoForge-only IItemExtension hook (used
    // to grant an item an enchantment level it doesn't actually have via NBT/component), no vanilla/Fabric
    // caller exists for it. This granted a free effective Sweeping Edge I level; that free level is gone.

    // canPerformAction(ItemStack, ItemAbility) removed - net.neoforged.neoforge.common.ItemAbility/ItemAbilities
    // is a NeoForge-only IItemExtension hook (used to gate enchantment applicability such as Sweeping Edge);
    // vanilla/Fabric has no such per-item ability query and nothing calls it, so it was dead code here.

    private static ItemAttributeModifiers buildDefaultAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, ModConfigHandler.COMMON.items.doomboltAxe.attackDamageValue - 1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, ModConfigHandler.COMMON.items.doomboltAxe.attackSpeedValue - 4D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "guardian_axe_knockback_resistance"), 0.1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
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

    @Override
    public boolean canBreakItem() {
        return false;
    }
}
