package com.eeeab.eeeabsmobs.server.capability;

import net.minecraft.nbt.CompoundTag;

/**
 * Fabric-side replacement for NeoForge's {@code INBTSerializable<CompoundTag>}, which every capability interface
 * used to extend. NeoForge's variant carried a {@code HolderLookup.Provider} parameter on both methods so that
 * serialization could resolve registry-dependent holders; none of the five capability implementations in this
 * mod ever used that parameter (checked every impl body - all fields are booleans, primitives, or NBT already
 * built by {@code Ability#writeNBT()}/{@code #readNBT()}), so it is dropped here rather than faked.
 */
public interface NbtCapability {
    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);
}
