package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.inventory.AnimationControllerMenu;
import com.eeeab.eeeabsmobs.server.item.util.ModItemStackUtils;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//1.21.1中ItemStack不再有hasTag()方法(NBT改为数据组件)，改用has(DataComponents.CUSTOM_DATA)判断；
//NetworkHooks.openScreen已被移除，改用vanilla的ServerPlayer#openMenu(MenuProvider, Consumer<RegistryFriendlyByteBuf>)
public class ItemAnimationController extends Item {
    private static final String NBT_ENTITY_UUID = "animationEntityUUID";

    public ItemAnimationController() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof AnimatedEntity) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID(NBT_ENTITY_UUID, entity.getUUID());
            ModItemStackUtils.putNBT(stack, NBT_ENTITY_UUID, tag);
            if (player.level().isClientSide) {
                player.displayClientMessage(TranslateUtils.simpleOtherText(this.getDescriptionId(), null, entity.getName().getString()), true);
            }
        }
        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ModItemStackUtils.hasNBT(stack, NBT_ENTITY_UUID) || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(TranslateUtils.simpleItemText(this.getDescriptionId()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel serverLevel && itemStack.has(DataComponents.CUSTOM_DATA)) {
            Entity entity = serverLevel.getEntity(ModItemStackUtils.getNBT(itemStack, NBT_ENTITY_UUID).getUUID(NBT_ENTITY_UUID));
            if (player instanceof ServerPlayer serverPlayer && entity instanceof LivingEntity livingEntity && entity instanceof AnimatedEntity && entity.isAlive()) {
                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public @NotNull Component getDisplayName() {
                        return livingEntity.getName();
                    }

                    @Override
                    public @NotNull AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                        return new AnimationControllerMenu(containerId, playerInventory, livingEntity);
                    }
                }, buf -> buf.writeInt(livingEntity.getId()));
            }
            if (entity == null || !entity.isAlive()) {
                ModItemStackUtils.removeNbt(itemStack, NBT_ENTITY_UUID);
                return InteractionResultHolder.fail(itemStack);
            } else {
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        return InteractionResultHolder.success(itemStack);
    }
}
