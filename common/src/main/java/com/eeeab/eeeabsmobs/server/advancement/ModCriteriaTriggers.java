package com.eeeab.eeeabsmobs.server.advancement;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.resources.ResourceLocation;

public class ModCriteriaTriggers {
    public static final KilledTrigger KILL_BOSS_IN_CHALLENGE_MODE =
            CriteriaTriggers.register(new KilledTrigger(new ResourceLocation(EEEABMobs.MOD_ID, "kill_boss_in_challenge_mode")));

    public static void register(Object unusedModEventBus) {
    }
}
