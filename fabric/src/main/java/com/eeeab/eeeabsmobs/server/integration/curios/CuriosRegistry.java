package com.eeeab.eeeabsmobs.server.integration.curios;

/**
 * No-op on Fabric: Curios has no Fabric build for 1.21.1 (see {@link ICuriosApi}). Matches the Forge branch's
 * stub exactly.
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
