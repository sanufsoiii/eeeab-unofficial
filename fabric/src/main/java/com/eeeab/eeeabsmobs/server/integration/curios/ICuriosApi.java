package com.eeeab.eeeabsmobs.server.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

/**
 * Curios integration, disabled on Fabric.
 * <p>
 * Curios has no Fabric build for 1.21.1 either - like the Forge 1.21.1 branch, its published artifacts stop
 * at NeoForge/Forge only (checked against its own maven metadata). There is nothing to integrate with, and
 * top.theillusivec4.curios.api.* is not on this build's classpath at all.
 * <p>
 * Every call site already guards with {@link #isLoaded()}, so reporting "not loaded" disables the accessory
 * behaviour cleanly rather than faking it, mirroring the Forge branch's stub exactly.
 */
public enum ICuriosApi {
    INSTANCE;

    public static boolean isLoaded() {
        return false;
    }

    public boolean isPresentInventory(LivingEntity entity, Item item) {
        return false;
    }
}
