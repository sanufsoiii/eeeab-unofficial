package com.eeeab.eeeabsmobs.server.capability;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.init.ParticleInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ElectricityCapability {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "electricity_cap");

    public interface IElectricityCapability extends GeneralCapability, NbtCapability {
    }

    public static class IElectricityCapabilityImpl implements IElectricityCapability {
        private boolean isElectrified;

        @Override
        public boolean flag() {
            return this.isElectrified;
        }

        @Override
        public void tick(LivingEntity entity) {
            if (this.isElectrified) {
                if (entity.level().isClientSide()) {
                    int count = entity.getRandom().nextInt(5);
                    if (entity.tickCount % 3 == 0) {
                        for (int i = 0; i < count; i++) {
                            double posY = entity.getY() + (entity.getBbHeight() * 0.25F) + (entity.getBbHeight() * 0.8F) * entity.getRandom().nextDouble();
                            entity.level().addParticle(ParticleInit.GUARDIAN_SPARK.get(), entity.getRandomX(0.6), posY, entity.getRandomZ(0.6), 0D, 0.007D, 0D);
                        }
                    }
                }
            }
        }

        @Override
        public void onStart(LivingEntity entity) {
            this.isElectrified = true;
        }

        @Override
        public void onEnd(LivingEntity entity) {
            this.isElectrified = false;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("isElectrified", this.isElectrified);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.isElectrified = nbt.getBoolean("isElectrified");
        }
    }
}
