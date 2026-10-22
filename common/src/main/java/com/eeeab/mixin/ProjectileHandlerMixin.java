package com.eeeab.mixin;

import com.eeeab.eeeabsmobs.server.handler.ServerEventHandler;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces NeoForge's {@code ProjectileImpactEvent} (no Fabric API equivalent exists - checked
 * fabric-entity-events-v1 and fabric-events-interaction-v0, neither has a projectile-impact hook). Injecting at
 * the head of {@code onHitEntity} and returning early is the direct equivalent of the old event being canceled
 * ("the impact will not be processed and the projectile will continue flying", per NeoForge's own javadoc for
 * that event - matched here by simply not calling the vanilla body).
 */
@Mixin(Projectile.class)
public abstract class ProjectileHandlerMixin {

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void eeeabmobs$onHitEntity(EntityHitResult hitResult, CallbackInfo ci) {
        if (ServerEventHandler.onProjectileImpact((Projectile) (Object) this, hitResult)) {
            ci.cancel();
        }
    }
}
