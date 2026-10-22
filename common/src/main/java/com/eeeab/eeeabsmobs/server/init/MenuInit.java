package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.animate.server.inventory.AnimationControllerMenu;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.BuiltInRegistries;

public class MenuInit {
    public static final FabricDeferredRegister<MenuType<?>> REGISTRY = FabricDeferredRegister.create(BuiltInRegistries.MENU, EEEABMobs.MOD_ID);

    public static final FabricDeferredRegister.Entry<MenuType<AnimationControllerMenu>> ANIMATION_CONTROLLER = REGISTRY.register("animation_controller",
            () -> MenuRegistry.ofExtended((syncId, inv, buf) -> new AnimationControllerMenu(syncId, inv, buf.readVarInt())));

    public static void register(Object bus) {
        REGISTRY.register(bus);
    }
}
