package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.animate.server.inventory.AnimationControllerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, EEEABMobs.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AnimationControllerMenu>> ANIMATION_CONTROLLER = REGISTRY.register("animation_controller", () -> IMenuTypeExtension.create(AnimationControllerMenu::new));

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
