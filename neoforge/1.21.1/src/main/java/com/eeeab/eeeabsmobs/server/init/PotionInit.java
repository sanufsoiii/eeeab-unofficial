package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PotionInit {
    private static final DeferredRegister<Potion> POTION = DeferredRegister.create(Registries.POTION, EEEABMobs.MOD_ID);

    public static final DeferredHolder<Potion, Potion> FRENZY_POTION = POTION.register("frenzy_potion",
            () -> new Potion(new MobEffectInstance(EffectInit.FRENZY_EFFECT, 1800, 4)));

    public static void register(IEventBus bus) {
        POTION.register(bus);
    }
}
