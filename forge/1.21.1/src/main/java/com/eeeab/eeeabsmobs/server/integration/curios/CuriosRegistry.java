package com.eeeab.eeeabsmobs.server.integration.curios;

/**
 * No-op on Forge: Curios has no Forge build for 1.21.1 (its Forge artifact stops at 1.20.6).
 * See {@link ICuriosApi} for the full reasoning. The NeoForge branch keeps the real registry.
 */
public class CuriosRegistry {
    private CuriosRegistry() {
    }

    public static void init() {
    }

    public static void initClient() {
    }

    public static Object getInstance() {
        return null;
    }
}
