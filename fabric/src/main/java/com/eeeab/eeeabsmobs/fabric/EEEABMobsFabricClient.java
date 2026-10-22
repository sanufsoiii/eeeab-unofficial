package com.eeeab.eeeabsmobs.fabric;

import com.eeeab.eeeabsmobs.client.ClientProxy;
import net.fabricmc.api.ClientModInitializer;

public class EEEABMobsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientProxy.init();
    }
}
