package com.eeeab.eeeabsmobs.server.entity;

/**
 * NeoForge's LivingEntity implements an internal IEntityMultiPart-shaped interface with
 * isMultipartEntity() defaulting to false, overridden true by bosses that carry {@link ModEntityPart}
 * hitboxes (EntityRelicAnnihilator, EntityNamelessGuardian). Vanilla LivingEntity has no such method and
 * we're not mixing one in for a single call site - implemented as a plain marker interface instead, checked
 * with instanceof at the one place that needs it (EntityRelicAnnihilator#canAttack-style targeting).
 */
public interface IMultipartMob {
    boolean isMultipartEntity();
}
