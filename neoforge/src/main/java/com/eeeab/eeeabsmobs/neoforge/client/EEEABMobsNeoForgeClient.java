package com.eeeab.eeeabsmobs.neoforge.client;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.ClientProxy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge client-only entrypoint.
 */
@Mod(value = EEEABMobs.MOD_ID, dist = Dist.CLIENT)
public class EEEABMobsNeoForgeClient {

    public EEEABMobsNeoForgeClient() {
        ClientProxy.init();
    }
}
