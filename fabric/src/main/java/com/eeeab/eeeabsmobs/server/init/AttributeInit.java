package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.core.registries.BuiltInRegistries;

public class AttributeInit {
    public static final FabricDeferredRegister<Attribute> ATTRIBUTES = FabricDeferredRegister.create(BuiltInRegistries.ATTRIBUTE, EEEABMobs.MOD_ID);
    public static final FabricDeferredRegister.Entry<Attribute> CRIT_CHANCE = ATTRIBUTES.register("crit_chance", () -> new RangedAttribute("eeeabsmobs.crit_chance", 1D, 0D, 10D).setSyncable(true));

    // NeoForgeMod.SWIM_SPEED has no Fabric/vanilla equivalent (Attributes.WATER_MOVEMENT_EFFICIENCY affects
    // underwater mining speed, not swim speed). Registered here as our own attribute, same as CRIT_CHANCE above,
    // rather than inventing a second registry mechanism; only FrenzyEffect reads it.
    public static final FabricDeferredRegister.Entry<Attribute> SWIM_SPEED = ATTRIBUTES.register("swim_speed", () -> new RangedAttribute("eeeabsmobs.swim_speed", 1D, 0D, 1024D).setSyncable(true));

    public static void register(Object bus) {
        ATTRIBUTES.register(bus);
    }
}
