package com.eeeab.eeeabsmobs.server.advancement;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.KilledTrigger;

public class ModCriteriaTriggers {
    public static final KilledTrigger KILL_BOSS_IN_CHALLENGE_MODE =
            CriteriaTriggers.register(EEEABMobs.MOD_ID + ":kill_boss_in_challenge_mode", new KilledTrigger());

    public static void register(Object unusedModEventBus) {
    }
}
