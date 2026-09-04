package com.eeeab.eeeabsmobs.server.entity;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.animate.server.animation.release.cooldown.CooldownManager;
import com.eeeab.animate.server.handler.AnimationHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * <b>EEEABMobLibrary</b><br/>
 * <p>
 * IEntityWithComplexSpawn (NeoForge-only: lets an entity attach extra data to its own spawn packet, used below
 * to give a newly-tracking client the in-progress animation + tick immediately) has no Fabric/vanilla
 * equivalent - Fabric never patched entity spawn packets with a generic extra-data hook the way NeoForge did.
 * Ongoing animation changes still sync fine (AnimationHandler sends MessageAnimation/MessagePlayAnimation over
 * the mod's own payloads whenever the animation actually changes), but a client that starts tracking this
 * entity <i>mid-animation</i> (e.g. walks into render distance, or re-logs) will render it as NO_ANIMATION
 * until the next animation change fires - this is a real, honestly-unfixed regression, not a silent one. A
 * full fix would mean threading animation/animationTick through SynchedEntityData instead (which self-syncs to
 * new trackers for free) and reconciling that with the existing message-based path; that is a larger change to
 * this shared library class than this pass's scope covers.
 */
public abstract class EEEABMobLibrary extends EEEABMobEntity implements AnimatedEntity {
    private int animationTick;
    private Animation animation = NO_ANIMATION;
    private final AnimationState animationState = new AnimationState();
    private final CooldownManager cooldownManager = new CooldownManager();
    public boolean canplayHurtAnimation;
    /**
     * 受到伤害时中断其他动画
     */
    public boolean hurtInterruptsAnimation;
    /**
     * 死亡时清除其他正在播放的叠加动画
     */
    public boolean clearRedundantAnimationsOnDeath;

    public EEEABMobLibrary(EntityType<? extends EEEABMobLibrary> type, Level level) {
        super(type, level);
        this.canplayHurtAnimation = this.getHurtAnimation() != null;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.cooldownManager.tick();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        boolean attack = super.hurt(source, damage);
        if (attack && this.canplayHurtAnimation && this.getHealth() > 0.0F && (this.isNoAnimation() || this.hurtInterruptsAnimation)) {
            this.playAnimation(this.getHurtAnimation());
        }
        return attack;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (reason.shouldDestroy()) {
            this.cooldownManager.clear();
        }
    }

    @Override
    protected void dying() {
        if (this.getDeathAnimation() != null && this.getAnimation() != this.getDeathAnimation()) {
            if (this.clearRedundantAnimationsOnDeath) this.stopAllSuperpositionAnimation();
            this.playAnimation(this.getDeathAnimation());
        }
    }

    @Override
    protected int getDeathDuration() {
        Animation death;
        if ((death = getDeathAnimation()) != null) return death.getDuration();
        return 20;
    }

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (animation != NO_ANIMATION) {
            onAnimationStart(animation);
        } else {
            onAnimationFinish(this.animation);
        }
        this.animation = animation;
    }

    @Override
    public AnimationState getAnimationState(Animation animation) {
        return this.animationState;
    }

    // writeSpawnData/readSpawnData (IEntityWithComplexSpawn) removed - see the class javadoc above.

    public CooldownManager getCooldownManager() {
        return this.cooldownManager;
    }

    public void playAnimation(Animation animation) {
        if (animation != null) AnimationHandler.INSTANCE.sendAnimationMessage(this, animation);
    }

    public boolean isNoAnimation() {
        return NO_ANIMATION == this.animation;
    }

    public void stopAllSuperpositionAnimation() {
        AnimationHandler.INSTANCE.sendAnimationMessage(this, true);
    }

    protected void onAnimationStart(Animation animation) {
    }

    protected void onAnimationFinish(Animation animation) {
    }

    public Animation getDeathAnimation() {
        return null;
    }

    public Animation getHurtAnimation() {
        return null;
    }
}
