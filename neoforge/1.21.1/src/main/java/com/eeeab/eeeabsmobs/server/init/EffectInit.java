package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.potion.*;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EffectInit {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, EEEABMobs.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> ERODE_EFFECT = EFFECTS.register("erode_effect", ErodeEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> ARMOR_LOWER_EFFECT = EFFECTS.register("armor_lower_effect", ArmorLowerEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> STUN_EFFECT = EFFECTS.register("stun_effect", StunEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FRENZY_EFFECT = EFFECTS.register("frenzy_effect", FrenzyEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> ELECTRIFIED_EFFECT = EFFECTS.register("electrified_effect", OverloadEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
