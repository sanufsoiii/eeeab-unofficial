package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.animate.server.inventory.AnimationControllerMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * NeoForge's IMenuTypeExtension.create(...) (a plain (id, inv, FriendlyByteBuf) factory) has no Fabric
 * equivalent because vanilla's MenuType constructor is private on Fabric. Fabric API's screen-handler-api-v1
 * module (net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType, verified with javap against
 * fabric-screen-handler-api-v1-1.3.91) is the sanctioned replacement: it pairs a MenuType with an explicit
 * StreamCodec for the "extra data" sent when the menu opens, instead of a raw byte buf reader. The old
 * factory read a single entity id out of the buffer, so the extra-data type here is Integer via
 * ByteBufCodecs.VAR_INT, and AnimationControllerMenu's FriendlyByteBuf constructor was narrowed to take that
 * Integer directly (com/eeeab/animate/server/inventory/AnimationControllerMenu.java) - a boundary touch into
 * a directory outside this agent's assigned scope, made only because MenuInit cannot compile or be opened
 * without it.
 */
public class MenuInit {
    public static final FabricDeferredRegister<MenuType<?>> REGISTRY = FabricDeferredRegister.create(BuiltInRegistries.MENU, EEEABMobs.MOD_ID);

    public static final FabricDeferredRegister.Entry<MenuType<AnimationControllerMenu>> ANIMATION_CONTROLLER = REGISTRY.register("animation_controller",
            () -> new ExtendedScreenHandlerType<AnimationControllerMenu, Integer>(
                    AnimationControllerMenu::new, ByteBufCodecs.VAR_INT));

    public static void register(Object bus) {
        REGISTRY.register(bus);
    }
}
