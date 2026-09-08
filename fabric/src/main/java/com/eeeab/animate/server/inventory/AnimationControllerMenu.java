package com.eeeab.animate.server.inventory;

import com.eeeab.eeeabsmobs.server.init.MenuInit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class AnimationControllerMenu extends AbstractContainerMenu {
    public final Entity animationEntity;

    // Fabric's ExtendedScreenHandlerType hands over the decoded extra-data value directly instead of a raw
    // FriendlyByteBuf to read from by hand - see server/init/MenuInit.java for the StreamCodec that decodes it.
    public AnimationControllerMenu(int id, Inventory inv, Integer entityId) {
        this(id, inv, inv.player.level().getEntity(entityId));
    }

    public AnimationControllerMenu(int id, Inventory inv, Entity entity) {
        super(MenuInit.ANIMATION_CONTROLLER.get(), id);
        animationEntity = entity;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }
}
