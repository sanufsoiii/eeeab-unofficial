package com.eeeab.eeeabsmobs.server.entity;

/**
 * Marker interface for multipart boss entities (EntityRelicAnnihilator, EntityNamelessGuardian).
 * Note: Must NOT define a method named isMultipartEntity(), because on Forge / NeoForge Entity implements
 * IForgeEntity / IEntityExtension which defines isMultipartEntity(). If an entity returns true there without
 * providing Forge/NeoForge's PartEntity<?>[] via getParts(), the game crashes with a NullPointerException
 * during entity tracking (ServerLevel/ClientLevel).
 */
public interface IMultipartMob {
}
