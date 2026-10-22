package com.eeeab.eeeabsmobs.fabric;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.fabricmc.api.ModInitializer;

public class EEEABMobsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EEEABMobs.init();
    }
}
