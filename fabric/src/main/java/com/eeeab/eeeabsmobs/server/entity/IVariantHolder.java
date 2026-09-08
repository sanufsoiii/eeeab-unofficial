package com.eeeab.eeeabsmobs.server.entity;

/**
 * Mod-local replacement for vanilla {@code net.minecraft.world.entity.VariantHolder}, which was removed in
 * the 1.21.9 variant rework. Same shape (getVariant/setVariant) so the mod's skeleton/missile variant logic
 * and {@link com.eeeab.eeeabsmobs.client.render.layer.LayerVariantHolder} are unaffected.
 */
public interface IVariantHolder<V> {
    V getVariant();

    void setVariant(V variant);
}
