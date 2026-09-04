package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.potion.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.BuiltInRegistries;

public class EffectInit {
    public static final FabricDeferredRegister<MobEffect> EFFECTS = FabricDeferredRegister.create(BuiltInRegistries.MOB_EFFECT, EEEABMobs.MOD_ID);
    public static final FabricDeferredRegister.Entry<MobEffect> ERODE_EFFECT = EFFECTS.register("erode_effect", ErodeEffect::new);
    public static final FabricDeferredRegister.Entry<MobEffect> ARMOR_LOWER_EFFECT = EFFECTS.register("armor_lower_effect", ArmorLowerEffect::new);
    public static final FabricDeferredRegister.Entry<MobEffect> STUN_EFFECT = EFFECTS.register("stun_effect", StunEffect::new);
    public static final FabricDeferredRegister.Entry<MobEffect> FRENZY_EFFECT = EFFECTS.register("frenzy_effect", FrenzyEffect::new);
    public static final FabricDeferredRegister.Entry<MobEffect> ELECTRIFIED_EFFECT = EFFECTS.register("electrified_effect", OverloadEffect::new);

    public static void register(Object bus) {
        EFFECTS.register(bus);
    }
}
