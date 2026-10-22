package com.eeeab.eeeabsmobs.neoforge;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.platform.NeoForgeEEEABPlatform;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge common entrypoint.
 */
@Mod(EEEABMobs.MOD_ID)
public class EEEABMobsNeoForge {

    public EEEABMobsNeoForge(ModContainer container) {
        NeoForgeEEEABPlatform.setModBus(container.getEventBus());
        EEEABMobs.init();
    }
}
