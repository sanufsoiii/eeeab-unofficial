package com.eeeab.eeeabsmobs.server.entity.mob;

/**
 * Vanilla's {@code net.minecraft.world.entity.MobType} was removed in 1.21.1 (Smite/Bane-of-Arthropods
 * bonuses are now data-driven via entity type tags on the enchantment itself instead of a per-entity
 * Java override). This enum keeps the mod's own internal "what kind of mob is this for our own AI
 * checks" marker alive; it is no longer tied into vanilla's enchantment damage bonus calculation.
 */
public enum ModMobType {
    IMMORTAL,
    PLAYER_SUMMONS,
    UNDEAD
}
