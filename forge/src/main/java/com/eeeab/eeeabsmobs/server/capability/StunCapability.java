package com.eeeab.eeeabsmobs.server.capability;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StunCapability {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "stun_processor_cap");

    public interface IStunCapability extends GeneralCapability, INBTSerializable<CompoundTag> {
    }

    public static class StunCapabilityImp implements IStunCapability {
        private boolean inStun;

        @Override
        public boolean flag() {
            return inStun;
        }

        public void setInStun(boolean inStun) {
            this.inStun = inStun;
        }

        @Override
        public void onStart(LivingEntity entity) {
            if (entity != null) {
                entity.stopUsingItem();
            }
            inStun = true;
        }

        @Override
        public void tick(LivingEntity entity) {
            if (inStun) {
                if (entity.level().isClientSide) {
                    for (int i = 0; i < 5; i++) {
                        Vec3 pos = new Vec3(entity.getX(), entity.getY() + (entity.getBbHeight() + 0.25D), entity.getZ()).add(new Vec3(1.0D, 0, 0).yRot((float) Math.toRadians(entity.getRandom().nextInt(360))));
                        entity.level().addParticle(ParticleTypes.CRIT, pos.x(), pos.y(), pos.z(), 0, 0, 0);
                    }
                }
            }
        }

        @Override
        public void onEnd(LivingEntity entity) {
            inStun = false;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putBoolean("inStun", flag());
            return compoundTag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            setInStun(nbt.getBoolean("inStun"));
        }
    }

    public static class StunCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<IStunCapability> instance = LazyOptional.of(StunCapabilityImp::new);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CapabilityHandler.STUN_CAPABILITY.orEmpty(cap, instance.cast());
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            return instance.orElseThrow(NullPointerException::new).serializeNBT(provider);
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            instance.orElseThrow(NullPointerException::new).deserializeNBT(provider, nbt);
        }
    }
}
