package com.eeeab.eeeabsmobs.forge;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.platform.ForgeEEEABPlatform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EEEABMobs.MOD_ID)
public class EEEABMobsForge {

    public EEEABMobsForge() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(EEEABMobs.MOD_ID, modBus);
        ForgeEEEABPlatform.setModBus(modBus);
        EEEABMobs.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> EEEABMobsForgeClient::init);
    }
}
