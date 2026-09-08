package com.eeeab.eeeabsmobs.server.advancement;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.init.FabricDeferredRegister;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * 自定义成就触发条件
 * <p>
 * Upstream called {@code CriteriaTriggers.register(...)} from a static field initialiser and touched the
 * class from {@code FMLLoadCompleteEvent}. On 1.21.1 the registries are already frozen by then, so the
 * static initialiser threw "Registry is already frozen (trying to add key ... kill_boss_in_challenge_mode)"
 * and took the whole load down. Registration now goes through a DeferredRegister on the mod event bus,
 * which runs during the registry phase like the mod's other registries.
 */
public class ModCriteriaTriggers {
    public static final FabricDeferredRegister<CriterionTrigger<?>> TRIGGERS =
            FabricDeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, EEEABMobs.MOD_ID);

    public static final FabricDeferredRegister.Entry<KilledTrigger> KILL_BOSS_IN_CHALLENGE_MODE =
            TRIGGERS.register("kill_boss_in_challenge_mode", KilledTrigger::new);

    public static void register(Object unusedModEventBus) {
        TRIGGERS.register(unusedModEventBus);
    }
}
