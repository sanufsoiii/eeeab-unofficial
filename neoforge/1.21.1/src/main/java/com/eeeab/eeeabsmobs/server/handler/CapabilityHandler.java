package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.capability.*;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * NeoForge removed the Forge {@code Capability<T>} / {@code ICapabilityProvider} system entirely; there is no
 * drop-in replacement class. What this mod actually used capabilities for was per-entity mutable state (stun,
 * frenzy, electrification, abilities, threat memory), which NeoForge now models as "data attachments"
 * (net.neoforged.neoforge.attachment.AttachmentType). Every Entity implements IAttachmentHolder directly, so
 * there is no more per-entity "provider" registration step (AttachCapabilitiesEvent is gone) - the default
 * value is created lazily the first time it is requested via Entity#getData(AttachmentType).
 * <p>
 * This is a redesign, not a rename: attachments have no "side" (no Direction parameter), no LazyOptional, and
 * serialization uses INBTSerializable#serializeNBT(HolderLookup.Provider) / deserializeNBT(HolderLookup.Provider, T)
 * (registry-aware NBT, because 1.21 stores holders that need a registry to resolve).
 */
public class CapabilityHandler {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EEEABMobs.MOD_ID);

    public static final Supplier<AttachmentType<StunCapability.IStunCapability>> STUN_CAPABILITY =
            ATTACHMENT_TYPES.register("stun", () -> AttachmentType.serializable((Supplier<StunCapability.IStunCapability>) StunCapability.StunCapabilityImp::new).build());
    public static final Supplier<AttachmentType<FrenzyCapability.IFrenzyCapability>> FRENZY_CAPABILITY =
            ATTACHMENT_TYPES.register("frenzy", () -> AttachmentType.serializable((Supplier<FrenzyCapability.IFrenzyCapability>) FrenzyCapability.FrenzyCapabilityImpl::new).build());
    public static final Supplier<AttachmentType<ElectricityCapability.IElectricityCapabilityImpl>> ELECTRICITY_CAPABILITY =
            ATTACHMENT_TYPES.register("electricity", () -> AttachmentType.serializable(ElectricityCapability.IElectricityCapabilityImpl::new).build());
    public static final Supplier<AttachmentType<AbilityCapability.IAbilityCapability>> ABILITY_CAPABILITY =
            ATTACHMENT_TYPES.register("ability", () -> AttachmentType.serializable((Supplier<AbilityCapability.IAbilityCapability>) AbilityCapability.AbilityCapabilityImp::new).build());
    // Threat memory never implemented INBTSerializable upstream either - it is intentionally transient combat
    // state, not persisted across a restart/relog. Kept that way here: builder() without .serialize(...).
    public static final Supplier<AttachmentType<ThreatMemoryCapability.IThreatMemoryCapabilityImpl>> THREATMEMORY_CAPABILITY =
            ATTACHMENT_TYPES.register("threat_memory", () -> AttachmentType.builder(ThreatMemoryCapability.IThreatMemoryCapabilityImpl::new).build());

    @SuppressWarnings("unchecked")
    public static final Supplier<AttachmentType<?>>[] CAPABILITIES = new Supplier[]{
            STUN_CAPABILITY,
            FRENZY_CAPABILITY,
            ELECTRICITY_CAPABILITY,
            ABILITY_CAPABILITY,
            THREATMEMORY_CAPABILITY,
    };

    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    /**
     * Drop-in replacement for the old {@code CapabilityHandler.getCapability(Entity, Capability<T>)} helper.
     * Behaviour differs subtly from the Forge original: attachments always have a default value once touched,
     * so this can only return null when the entity itself is null/dead - never because "no provider was
     * attached" (there is no such concept anymore). Existing null-checks at call sites remain harmless.
     */
    @Nullable
    public static <T> T getCapability(Entity entity, Supplier<AttachmentType<T>> attachmentType) {
        if (entity == null) return null;
        if (!entity.isAlive()) return null;
        return entity.getData(attachmentType);
    }
}
