package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.server.entity.effect.IEntity;
import com.eeeab.eeeabsmobs.server.entity.mob.IMob;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemRemoveMob extends Item {
    private static final double FIND_SIZE = 16;

    public ItemRemoveMob(net.minecraft.world.item.Item.Properties p) {
        super(p.stacksTo(1));
    }

    // Was onLeftClickEntity(ItemStack, Player, Entity) overriding NeoForge's IItemExtension hook; see the same
    // note in ItemAnimationController for why fabric-events-interaction-v0's AttackEntityCallback is the
    // Fabric-idiomatic replacement.
    static {
        dev.architectury.event.events.common.PlayerEvent.ATTACK_ENTITY.register((player, level, entity, hand, hitResult) -> {
            if (!(player.getItemInHand(hand).getItem() instanceof ItemRemoveMob)) {
                return dev.architectury.event.EventResult.pass();
            }
            if (entity instanceof IEntity || entity instanceof IMob) {
                if (!entity.level().isClientSide()) {
                    entity.remove(Entity.RemovalReason.KILLED);
                }
                return dev.architectury.event.EventResult.interruptFalse();
            }
            return dev.architectury.event.EventResult.pass();
        });
    }

    //右键删除16*16*16范围的模组实体
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (!player.level().isClientSide()) {
            for (Entity entity : getNearByEntities(player)) {
                if (entity instanceof IEntity || entity instanceof IMob) entity.remove(Entity.RemovalReason.KILLED);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            player.swing(hand, true);
            player.getCooldowns().addCooldown(stack, 40);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.addAll(TranslateUtils.complexText(TranslateUtils.ITEM_PREFIX, 2, ChatFormatting.GRAY, this.getDescriptionId()));
    }

    private static List<Entity> getNearByEntities(Entity entity) {
        return entity.level().getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(ItemRemoveMob.FIND_SIZE), targetEntity -> targetEntity != entity);
    }
}
