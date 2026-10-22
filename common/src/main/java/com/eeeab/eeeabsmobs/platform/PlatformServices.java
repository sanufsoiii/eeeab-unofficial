package com.eeeab.eeeabsmobs.platform;

import com.eeeab.eeeabsmobs.EEEABMobs;

import java.util.ServiceLoader;

public final class PlatformServices {

    private static volatile EEEABPlatform instance;

    private PlatformServices() {
    }

    public static EEEABPlatform get() {
        EEEABPlatform local = instance;
        if (local == null) {
            synchronized (PlatformServices.class) {
                local = instance;
                if (local == null) {
                    local = load();
                    instance = local;
                }
            }
        }
        return local;
    }

    private static EEEABPlatform load() {
        for (EEEABPlatform candidate : ServiceLoader.load(EEEABPlatform.class)) {
            EEEABMobs.LOGGER.debug("Using EEEABMobs platform implementation: {}", candidate.getClass().getName());
            return candidate;
        }
        throw new IllegalStateException(
                "No EEEABPlatform implementation found. Is the " + EEEABMobs.MOD_ID
                        + " loader module present on the classpath?");
    }
}
