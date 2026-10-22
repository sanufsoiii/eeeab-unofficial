package com.eeeab.eeeabsmobs.server.entity.mob.corpse;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;

import com.eeeab.eeeabsmobs.server.entity.EEEABMobLibrary;
import com.eeeab.eeeabsmobs.server.entity.mob.GlowEntity;
import com.eeeab.eeeabsmobs.server.entity.mob.SummoningEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class EntityAbsCorpse extends EEEABMobLibrary implements Enemy, GlowEntity, SummoningEntity<LivingEntity> {
    private static final EntityDataAccessor<Boolean> DATA_ACTIVE = SynchedEntityData.defineId(EntityAbsCorpse.class, EntityDataSerializers.BOOLEAN);
    private UUID ownerUUID;
    private boolean isSummon;
    boolean valuable;
    @Nullable
    private LivingEntity owner;

    public EntityAbsCorpse(EntityType<? extends EntityAbsCorpse> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.registerCorpseGoals();
    }

    protected void registerCorpseGoals() {
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, 5, false, false, null));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, 5, false, false, (zombie, lvl) -> !(zombie instanceof ZombifiedPiglin)));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractIllager.class, 5, false, false, null));
    }

    // Vanilla's MobType (and getDamageBonus(item, MobType)) was removed in 1.21.1; Smite bonus is now
    // data-driven via entity type tags on the enchantment itself. Registering this entity type under
    // the appropriate tag (e.g. minecraft:undead) in datagen restores the old "extra Smite damage"
    // behaviour; that is a datagen/tags concern outside this file's scope.
    @Override
    public void makePoofParticles() {
        for (int i = 0; i < 10; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(ParticleTypes.SOUL, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    @Override
    protected boolean considersEntityAsAlly(Entity entity) {
        if (super.considersEntityAsAlly(entity)) {
            return true;
        } else return entity instanceof LivingEntity && entity == getOwner();
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTIVE, true);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        if (this.owner != null && this.owner.getHealth() > 0F) {
            compound.store("owner", net.minecraft.core.UUIDUtil.CODEC, owner.getUUID());
        }
        compound.putBoolean("isSummon", this.isSummon());
    }

    @Override
    public void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        this.setOwnerUUID(compound.read("owner", net.minecraft.core.UUIDUtil.CODEC).isPresent() ? compound.read("owner", net.minecraft.core.UUIDUtil.CODEC).orElse(null) : null);
        this.isSummon = compound.getBooleanOr("isSummon", false);
    }

    @Override
    protected boolean shouldDropLoot(net.minecraft.server.level.ServerLevel level) {
        return super.shouldDropLoot(level) && !this.isSummon();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance instance, EntitySpawnReason spawnType, @Nullable SpawnGroupData groupData) {
        return groupData;
    }

    //设置该生物初始生成
    public void afterSpawn() {
        this.isSummon = true;
        this.valuable = true;
    }

    public void setActive(boolean isActive) {
        this.entityData.set(DATA_ACTIVE, isActive);
    }

    public boolean isActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    @Override
    public boolean isSummon() {
        return this.isSummon;
    }

    @Override
    public boolean isGlow() {
        return getHealth() > 0;
    }
}