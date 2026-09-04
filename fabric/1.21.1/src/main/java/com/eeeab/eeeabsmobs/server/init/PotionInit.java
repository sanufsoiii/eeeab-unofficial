package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.registries.BuiltInRegistries;

public class PotionInit {
    private static final FabricDeferredRegister<Potion> POTION = FabricDeferredRegister.create(BuiltInRegistries.POTION, EEEABMobs.MOD_ID);

    public static final FabricDeferredRegister.Entry<Potion> FRENZY_POTION = POTION.register("frenzy_potion",
            () -> new Potion(new MobEffectInstance(EffectInit.FRENZY_EFFECT.holder(), 1800, 4)));

    public static void register(Object bus) {
        POTION.register(bus);
    }
}
