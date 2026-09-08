package com.eeeab.eeeabsmobs.server.advancement;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, EEEABMobs.MOD_ID);

    public static final RegistryObject<KilledTrigger> KILL_BOSS_IN_CHALLENGE_MODE =
            TRIGGERS.register("kill_boss_in_challenge_mode", KilledTrigger::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }
}
