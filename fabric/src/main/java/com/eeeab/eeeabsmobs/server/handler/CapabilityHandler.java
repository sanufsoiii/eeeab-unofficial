package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.capability.*;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

/**
 * NeoForge's Forge-derived {@code Capability<T>}/{@code ICapabilityProvider} system has no NeoForge-side
 * replacement other than "data attachments" (net.neoforged.neoforge.attachment.AttachmentType) - but Fabric is
 * NOT capability-less either: contrary to the initial assumption for this port, Fabric API itself ships an
 * equivalent, official module, {@code fabric-data-attachment-api-v1} (confirmed present as one of the nested
 * jars inside fabric-api-0.116.15+1.21.1.jar under META-INF/jars/, and already on the compile classpath because
 * build.gradle depends on the "net.fabricmc.fabric-api:fabric-api" umbrella artifact - no build file or new
 * dependency was added for this). It is not a third-party library: it is part of the same Fabric API artifact
 * this mod already requires, and it is what upstream mods use for exactly this "per-entity mutable state"
 * problem NeoForge solves with data attachments.
 * <p>
 * Design, verified with {@code javap -p} against fabric-data-attachment-api-v1-0.116.15.jar:
 * - {@link AttachmentType} is created via {@link AttachmentRegistry#builder()}: an {@code initializer(Supplier)}
 *   provides the lazily-created default value (mirrors NeoForge's {@code AttachmentType.serializable(supplier)}),
 *   and an optional {@code persistent(Codec)} makes it survive a save/load ("threat_memory" intentionally omits
 *   this, exactly as it did on NeoForge - combat aggro state was never persisted there either).
 * - {@link Entity} does not implement {@link AttachmentTarget} in source (see: compiling against the merged
 *   Loom jar does not show it in the `implements` clause), because Fabric API injects that interface onto
 *   Entity/BlockEntity/ChunkAccess via its own mixin (net.fabricmc.fabric.mixin.attachment.AttachmentTargetsMixin,
 *   decompiled and confirmed to implement the impl side of AttachmentTarget). At runtime, after Fabric's own
 *   mixin config applies, every Entity instance genuinely does implement AttachmentTarget - casting to it is the
 *   documented, intended way third-party mods are meant to use this API, not an assumption.
 * - The persistence codec for each capability is built from the capability's own existing
 *   {@code serializeNBT()}/{@code deserializeNBT(CompoundTag)} pair (see {@link NbtCapability}) via
 *   {@code CompoundTag.CODEC.xmap(...)}, so none of the four persisted capabilities' internal (de)serialization
 *   logic needed to be rewritten as a Codec by hand - only re-plumbed.
 */
public class CapabilityHandler {

    public static final AttachmentType<StunCapability.IStunCapability> STUN_CAPABILITY =
            register("stun", StunCapability.StunCapabilityImp::new, StunCapability.StunCapabilityImp::new);
    public static final AttachmentType<FrenzyCapability.IFrenzyCapability> FRENZY_CAPABILITY =
            register("frenzy", FrenzyCapability.FrenzyCapabilityImpl::new, FrenzyCapability.FrenzyCapabilityImpl::new);
    public static final AttachmentType<ElectricityCapability.IElectricityCapability> ELECTRICITY_CAPABILITY =
            register("electricity", ElectricityCapability.IElectricityCapabilityImpl::new, ElectricityCapability.IElectricityCapabilityImpl::new);
    public static final AttachmentType<AbilityCapability.IAbilityCapability> ABILITY_CAPABILITY =
            registerAbility();
    // Threat memory never implemented NBT (de)serialization upstream either - it is intentionally transient
    // combat state, not persisted across a restart/relog. Kept that way here: no persistent(...) call.
    public static final AttachmentType<ThreatMemoryCapability.IThreatMemoryCapability> THREATMEMORY_CAPABILITY =
            AttachmentRegistry.<ThreatMemoryCapability.IThreatMemoryCapability>builder()
                    .initializer(ThreatMemoryCapability.IThreatMemoryCapabilityImpl::new)
                    .buildAndRegister(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "threat_memory"));

    public static final AttachmentType<?>[] CAPABILITIES = new AttachmentType[]{
            STUN_CAPABILITY,
            FRENZY_CAPABILITY,
            ELECTRICITY_CAPABILITY,
            ABILITY_CAPABILITY,
            THREATMEMORY_CAPABILITY,
    };

    private static <T extends NbtCapability> AttachmentType<T> register(String name, Supplier<T> initializer, Supplier<T> forCodec) {
        Codec<T> codec = CompoundTag.CODEC.xmap(nbt -> {
            T value = forCodec.get();
            value.deserializeNBT(nbt);
            return value;
        }, NbtCapability::serializeNBT);
        return AttachmentRegistry.<T>builder()
                .initializer(initializer)
                .persistent(codec)
                .buildAndRegister(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, name));
    }

    private static AttachmentType<AbilityCapability.IAbilityCapability> registerAbility() {
        Codec<AbilityCapability.IAbilityCapability> codec = CompoundTag.CODEC.xmap(nbt -> {
            AbilityCapability.IAbilityCapability value = new AbilityCapability.AbilityCapabilityImp();
            value.deserializeNBT(nbt);
            return value;
        }, NbtCapability::serializeNBT);
        return AttachmentRegistry.<AbilityCapability.IAbilityCapability>builder()
                .initializer(AbilityCapability.AbilityCapabilityImp::new)
                .persistent(codec)
                .buildAndRegister(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "ability"));
    }

    /**
     * Registration happens eagerly above (class-init time), the same idiom as {@code FabricDeferredRegister} -
     * there is no mod-bus RegisterEvent on Fabric to defer to. Kept as a no-op so any out-of-scope call site
     * that still calls {@code CapabilityHandler.register(bus)} during mod init (a leftover from the NeoForge
     * shape) keeps compiling without edits.
     */
    public static void register(Object unusedModEventBus) {
        // Intentionally empty - see class javadoc.
    }

    /**
     * Drop-in replacement for the old {@code CapabilityHandler.getCapability(Entity, Supplier<AttachmentType<T>>)}
     * helper. Behaviour differs subtly from the NeoForge original: an attachment always has a default value once
     * touched, so this can only return null when the entity itself is null/dead - never because "no provider was
     * attached" (there is no such concept anymore, on either loader). Existing null-checks at call sites remain
     * harmless.
     */
    @Nullable
    public static <T> T getCapability(Entity entity, AttachmentType<T> attachmentType) {
        if (entity == null) return null;
        if (!entity.isAlive()) return null;
        return ((AttachmentTarget) entity).getAttachedOrCreate(attachmentType);
    }
}
