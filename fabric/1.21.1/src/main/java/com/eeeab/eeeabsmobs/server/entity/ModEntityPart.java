package com.eeeab.eeeabsmobs.server.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

// NeoForge's net.neoforged.neoforge.entity.PartEntity<T> doesn't exist on Fabric/vanilla. Vanilla's own
// multipart entity (EnderDragonPart, net/minecraft/world/entity/boss/EnderDragonPart, verified with javap
// against the Loom-mapped 1.21.1 jar) just extends Entity directly and reuses the parent's own EntityType/Level
// in its constructor (super(parentMob.getType(), parentMob.level())) rather than registering a dedicated
// EntityType for the part - copied that pattern here instead of guessing a NeoForge-shaped replacement.
//参考自: package net.minecraft.world.entity.boss.EnderDragonPart
public class ModEntityPart<T extends LivingEntity> extends Entity {
    public final T entity;
    public final String partName;
    private final EntityDimensions size;

    public ModEntityPart(T parent, String partName, float width, float height) {
        super(parent.getType(), parent.level());
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.entity = parent;
        this.partName = partName;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {
    }

    @Override
    protected void setRot(float yaw, float pitch) {
        this.setYRot(entity.getYRot() % 360.0F);
        this.setXRot(entity.getXRot() % 360.0F);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return !this.isInvulnerableTo(source) && this.entity.hurt(source, damage);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.entity == entity;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size.scale(1.0F);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || (source.getEntity() != null && entity.isAlliedTo(source.getEntity()));
    }
}
