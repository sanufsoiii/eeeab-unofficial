package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.RegistryObject;

public class PotionInit {
    private static final DeferredRegister<Potion> POTION = DeferredRegister.create(Registries.POTION, EEEABMobs.MOD_ID);

    public static final RegistryObject<Potion> FRENZY_POTION = POTION.register("frenzy_potion",
            () -> new Potion(new MobEffectInstance(EffectInit.FRENZY_EFFECT.getHolder().orElseThrow(), 1800, 4)));

    public static void register(IEventBus bus) {
        POTION.register(bus);
    }
}
