package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.platform.PlatformServices;
import com.eeeab.eeeabsmobs.server.capability.*;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class CapabilityHandler {

    public static final class CapabilityToken<T> {
        private final String name;
        private final int index;

        public CapabilityToken(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }
    }

    public static final CapabilityToken<StunCapability.IStunCapability> STUN_CAPABILITY =
            new CapabilityToken<>("stun", 0);
    public static final CapabilityToken<FrenzyCapability.IFrenzyCapability> FRENZY_CAPABILITY =
            new CapabilityToken<>("frenzy", 1);
    public static final CapabilityToken<ElectricityCapability.IElectricityCapability> ELECTRICITY_CAPABILITY =
            new CapabilityToken<>("electricity", 2);
    public static final CapabilityToken<AbilityCapability.IAbilityCapability> ABILITY_CAPABILITY =
            new CapabilityToken<>("ability", 3);
    public static final CapabilityToken<ThreatMemoryCapability.IThreatMemoryCapability> THREATMEMORY_CAPABILITY =
            new CapabilityToken<>("threat_memory", 4);

    public static final CapabilityToken<?>[] CAPABILITIES = new CapabilityToken<?>[]{
            STUN_CAPABILITY,
            FRENZY_CAPABILITY,
            ELECTRICITY_CAPABILITY,
            ABILITY_CAPABILITY,
            THREATMEMORY_CAPABILITY,
    };

    public static void register(Object unused) {
    }

    @Nullable
    public static <T> T getCapability(Entity entity, CapabilityToken<T> token) {
        if (entity == null || !entity.isAlive()) return null;
        return PlatformServices.get().getCapability(entity, token);
    }
}
