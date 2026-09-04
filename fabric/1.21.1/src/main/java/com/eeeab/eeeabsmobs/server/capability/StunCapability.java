package com.eeeab.eeeabsmobs.server.capability;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class StunCapability {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "stun_processor_cap");

    public interface IStunCapability extends GeneralCapability, NbtCapability {
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
        public CompoundTag serializeNBT() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putBoolean("inStun", flag());
            return compoundTag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            setInStun(nbt.getBoolean("inStun"));
        }
    }
}
