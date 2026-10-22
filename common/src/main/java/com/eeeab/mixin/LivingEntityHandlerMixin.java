package com.eeeab.mixin;

import com.eeeab.eeeabsmobs.server.handler.ServerEventHandler;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import java.util.Collection;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric has no event bus, and (verified by listing every class in fabric-entity-events-v1, fabric-lifecycle-
 * events-v1 and fabric-events-interaction-v0 with {@code javap}) no callback at all for a mutable damage amount,
 * knockback, fall damage, jump, or mob-effect-added/removed notification - these are exactly the NeoForge events
 * ({@code LivingIncomingDamageEvent}, {@code LivingKnockBackEvent}, {@code LivingFallEvent},
 * {@code LivingEvent.LivingJumpEvent}, {@code MobEffectEvent.Added/Remove/Expired}, {@code LivingHealEvent},
 * {@code LivingBreatheEvent}) {@code ServerEventHandler} used to subscribe to. This mixin is the honest
 * replacement: it injects directly into the LivingEntity methods that NeoForge's events used to wrap, and
 * delegates the actual game logic back to the platform-agnostic static methods on
 * {@link com.eeeab.eeeabsmobs.server.handler.ServerEventHandler} so the behaviour itself lives in one place.
 * <p>
 * Every injection point below was confirmed to exist with this exact signature by {@code javap -p} against the
 * Loom-mapped (official Mojang mappings) 1.21.1 merged jar before being used.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityHandlerMixin {

    // Drives the four per-tick capabilities (stun/ability/frenzy/electricity) and the guardian-core-durability
    // player tick, replacing EntityTickEvent.Post + PlayerTickEvent.Post.
    @Inject(method = "tick", at = @At("TAIL"))
    private void eeeabmobs$tick(CallbackInfo ci) {
        ServerEventHandler.onLivingEntityTick((LivingEntity) (Object) this);
    }

    // Replaces LivingFallEvent: cancel fall damage entirely when true.
    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void eeeabmobs$causeFallDamage(double fallDistance, float damageMultiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (ServerEventHandler.onLivingFall((LivingEntity) (Object) this, (float) fallDistance)) {
            cir.setReturnValue(false);
        }
    }

    // Replaces LivingKnockBackEvent (setStrength(0) + setCanceled(true) together == "no knockback happens").
    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void eeeabmobs$knockback(double strength, double x, double z, CallbackInfo ci) {
        if (ServerEventHandler.onLivingEntityKnockBack((LivingEntity) (Object) this)) {
            ci.cancel();
        }
    }

    // Replaces LivingEvent.LivingJumpEvent (stun zeroed horizontal jump velocity, kept the vertical impulse).
    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void eeeabmobs$jumpFromGround(CallbackInfo ci) {
        ServerEventHandler.onLivingJump((LivingEntity) (Object) this);
    }

    // Replaces LivingIncomingDamageEvent (pre-mitigation amount mutation: crit chance, chainsword combo,
    // threat-memory recording, shaman-bomb rebound stun).
    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float eeeabmobs$hurt(float amount, ServerLevel level, DamageSource source) {
        return ServerEventHandler.onLivingEntityHurt((LivingEntity) (Object) this, source, amount);
    }

    // Replaces LivingHealEvent (erode effect reduces incoming heals).
    @ModifyVariable(method = "heal", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float eeeabmobs$heal(float amount) {
        return ServerEventHandler.onLivingEntityHeal((LivingEntity) (Object) this, amount);
    }

    // Replaces LivingBreatheEvent (frenzy lets an entity breathe underwater); canBreatheUnderwater() is `final`
    // in source, but a mixin patches bytecode directly so the injection still applies.
    @Inject(method = "canBreatheUnderwater", at = @At("HEAD"), cancellable = true)
    private void eeeabmobs$canBreatheUnderwater(CallbackInfoReturnable<Boolean> cir) {
        if (ServerEventHandler.onLivingEntityBreathe((LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }

    // Replaces MobEffectEvent.Added.
    @Inject(method = "onEffectAdded", at = @At("HEAD"))
    private void eeeabmobs$onEffectAdded(MobEffectInstance effectInstance, net.minecraft.world.entity.Entity source, CallbackInfo ci) {
        ServerEventHandler.onAddPointEffect((LivingEntity) (Object) this, effectInstance);
    }

    // Replaces MobEffectEvent.Remove + MobEffectEvent.Expired collapsed into one notification (see
    // ServerEventHandler's class javadoc for why that collapse is safe here).
    @Inject(method = "onEffectsRemoved", at = @At("HEAD"))
    private void eeeabmobs$onEffectsRemoved(Collection<MobEffectInstance> effectInstances, CallbackInfo ci) {
        for (MobEffectInstance effectInstance : effectInstances) {
            ServerEventHandler.onEffectRemoved((LivingEntity) (Object) this, effectInstance);
        }
    }

    // The cancellation half of MobEffectEvent.Remove (frenzy refuses to be manually cured) - removeEffect(Holder)
    // is the entry point that curative items/commands call, so cancelling here before removal happens is the
    // correct equivalent of NeoForge's event.setCanceled(true).
    @Inject(method = "removeEffect", at = @At("HEAD"), cancellable = true)
    private void eeeabmobs$removeEffect(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (ServerEventHandler.onRemoveEffectAttempt((LivingEntity) (Object) this, effect)) {
            cir.setReturnValue(false);
        }
    }
}
