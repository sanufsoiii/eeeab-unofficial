package com.eeeab.eeeabsmobs.server.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

/**
 * Curios integration, disabled on Forge.
 * <p>
 * Curios has no Forge build for 1.21.1 -- its Forge artifact stops at 1.20.6 and the project moved to
 * NeoForge only (checked against its own maven metadata). There is therefore nothing to integrate with
 * here, and the API this class wrapped does not exist on the classpath at all.
 * <p>
 * The mod already guards every call site with {@link #isLoaded()}, so reporting "not loaded" disables the
 * accessory behaviour cleanly rather than faking it. The NeoForge branch keeps the real integration.
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
