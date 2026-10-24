package com.eeeab.eeeabsmobs.server.entity.effect.projectile;

import com.eeeab.eeeabsmobs.server.entity.effect.IEntity;
import com.eeeab.eeeabsmobs.server.init.EntityInit;
import net.minecraft.core.Position;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class EntityPoisonArrow extends AbstractArrow implements IEntity {
    private int duration;

    public EntityPoisonArrow(EntityType<? extends EntityPoisonArrow> entityType, Level level) {
        super(entityType, level);
    }

    // AbstractArrow dropped its (EntityType,double,double,double,Level) position constructor - only the
    // ItemStack-carrying variants take a position directly now. This arrow isn't fired from a real weapon
    // ItemStack, so construct via (EntityType,Level) and position it explicitly instead.
    public EntityPoisonArrow(Level level, Position position) {
        this(EntityInit.POISON_ARROW.get(), level);
        this.setPos(position.x(), position.y(), position.z());
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
        this.duration = 100;
        this.setBaseDamage(6.0D);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.discard();
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        super.doPostHurtEffects(target);
        target.addEffect(new MobEffectInstance(MobEffects.POISON, this.duration, 1), this.getEffectSource());
    }
}
