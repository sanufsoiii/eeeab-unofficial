package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.client.particle.util.ModParticleUtils;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityShamanBomb;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemImmortalStaff extends Item {
    public ItemImmortalStaff(net.minecraft.world.item.Item.Properties p) {
        super(p.stacksTo(1).fireResistant());
    }

    //1.21.1中releaseUsing返回类型改为boolean
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        InteractionHand hand = entity.getUsedItemHand();
        if (entity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(stack)) {
                return false;
            }
            player.getCooldowns().addCooldown(stack, 60);
        }
        if (!level.isClientSide()) {
            double yBodyRadians = Math.toRadians(entity.yHeadRot + (180 * (hand == InteractionHand.MAIN_HAND ? 1 : 2)));
            float width = entity.getBbWidth() * 0.6F;
            EntityShamanBomb shamanBomb = new EntityShamanBomb(level, entity, entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z);
            shamanBomb.setOwner(entity);
            shamanBomb.setIsPlayer(entity instanceof Player);
            shamanBomb.setDangerous(ItemImmortalStaff.isDangerBomb(entity));
            shamanBomb.setPos(shamanBomb.getX() + width * Math.cos(yBodyRadians), entity.getY(0.55), shamanBomb.getZ() + width * Math.sin(yBodyRadians));
            level.addFreshEntity(shamanBomb);
        } else {
            ModParticleUtils.annularParticleOutburst(level, 5, ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY(), entity.getZ(), 0.18, 0.15, 360F, 0F, Mth.PI);
            ModParticleUtils.annularParticleOutburst(level, 5, ParticleTypes.LARGE_SMOKE, entity.getX(), entity.getY(), entity.getZ(), 0.16, 0.1, 360F, 0F, -Mth.PI);
        }
        entity.playSound(SoundEvents.BLAZE_SHOOT);
        entity.swing(hand, true);
        return true;
    }

    @Override
    @NotNull
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

        public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        //ModConfigHandler.Item item = ModConfigHandler.COMMON.items;
        //if (TranslateUtils.SHOW_ITEM_CD) tooltip.accept(TranslateUtils.itemCoolTime(item.immortalStaffConfig1.get()));
        if (TranslateUtils.SHOW_ITEM_CD) tooltip.accept(TranslateUtils.itemCoolTime(3));
        tooltip.accept(TranslateUtils.simpleItemText(this.getDescriptionId()));
    }

    /**
     * 根据使用者的幸运值判断是否发射强力弹丸
     *
     * @param caster 使用者
     * @return 是否发射强力弹丸
     */
    public static boolean isDangerBomb(LivingEntity caster) {
        float l0 = 0F;
        AttributeInstance luck = caster.getAttribute(Attributes.LUCK);
        if (luck != null) {
            l0 = (float) luck.getValue();
        }
        return caster.getRandom().nextFloat() < 0.25 + l0 * 0.15;
    }
}
