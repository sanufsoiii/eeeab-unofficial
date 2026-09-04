package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.server.capability.*;
import com.eeeab.eeeabsmobs.server.entity.mob.IMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

/**
 * NeoForge's data-attachment system (net.minecraftforge.attachment.AttachmentType) does not exist in Forge.
 * Forge 1.21.1 still has the classic Forge 1.20.1-style capability system unchanged (verified against the
 * real jar with javap: Capability<T>, CapabilityManager, CapabilityToken, RegisterCapabilitiesEvent,
 * AttachCapabilitiesEvent and ICapabilityProvider are all present with the same signatures as 1.20.1, and
 * INBTSerializable already takes HolderLookup.Provider in this jar, matching what the capability impl
 * classes in this package already implement). So this is restored to the original Forge 1.20.1 design
 * rather than reusing anything from the NeoForge attachment port.
 */
public class CapabilityHandler {
    public static final Capability<StunCapability.IStunCapability> STUN_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<FrenzyCapability.IFrenzyCapability> FRENZY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<ElectricityCapability.IElectricityCapability> ELECTRICITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<AbilityCapability.IAbilityCapability> ABILITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<ThreatMemoryCapability.IThreatMemoryCapability> THREATMEMORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final Capability<?>[] CAPABILITIES = new Capability[]{
            STUN_CAPABILITY,
            FRENZY_CAPABILITY,
            ELECTRICITY_CAPABILITY,
            ABILITY_CAPABILITY,
            THREATMEMORY_CAPABILITY,
    };

    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(AbilityCapability.IAbilityCapability.class);
        event.register(ElectricityCapability.IElectricityCapability.class);
        event.register(StunCapability.IStunCapability.class);
        event.register(FrenzyCapability.IFrenzyCapability.class);
        event.register(ThreatMemoryCapability.IThreatMemoryCapability.class);
    }

    @SuppressWarnings("unchecked")
    public static void attachEntityCapability(final AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof LivingEntity) {
            event.addCapability(StunCapability.ID, new StunCapability.StunCapabilityProvider());
            event.addCapability(FrenzyCapability.ID, new FrenzyCapability.FrenzyCapabilityProvider());
            event.addCapability(ElectricityCapability.ID, new ElectricityCapability.ElectricityCapabilityProvider());
            if (entity instanceof IMob mob && mob.isBossLevel()) {
                event.addCapability(ThreatMemoryCapability.ID, new ThreatMemoryCapability.ThreatMemoryCapabilityProvider());
            }
            if (entity instanceof Player) {
                event.addCapability(AbilityCapability.ID, new AbilityCapability.AbilityCapabilityProvider());
            }
        }
    }

    @Nullable
    public static <T> T getCapability(Entity entity, Capability<T> capability) {
        if (entity == null) return null;
        if (!entity.isAlive()) return null;
        return entity.getCapability(capability).orElse(null);
    }
}
