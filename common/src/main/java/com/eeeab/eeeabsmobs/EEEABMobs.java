package com.eeeab.eeeabsmobs;

import com.eeeab.eeeabsmobs.server.ServerProxy;
import com.eeeab.eeeabsmobs.server.advancement.ModCriteriaTriggers;
import com.eeeab.eeeabsmobs.server.entity.trigger.CombatTriggerHandler;
import com.eeeab.eeeabsmobs.server.init.*;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Loader-agnostic mod entry point.
 * The loader modules (:fabric, :neoforge) call {@link #init()} from their own entry points.
 */
public class EEEABMobs {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "eeeabsmobs";
    public static ServerProxy PROXY = createProxy();

    private static volatile boolean initialized;

    private static ServerProxy createProxy() {
        if (Platform.getEnvironment() != Env.CLIENT) {
            return new ServerProxy();
        }
        try {
            return (ServerProxy) Class.forName("com.eeeab.eeeabsmobs.client.ClientProxy")
                    .getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Running on the client but ClientProxy is missing", e);
        }
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        ItemInit.register(null);
        ModCriteriaTriggers.register(null);
        BlockInit.register(null);
        BlockEntityInit.register(null);
        EntityInit.register(null);
        CreativeTabInit.register(null);
        AttributeInit.register(null);
        EffectInit.register(null);
        PotionInit.register(null);
        ParticleInit.register(null);
        SoundInit.register(null);
        MenuInit.register(null);
        StructuresInit.register(null);
        WorldGenInit.register(null);

        PROXY.initMod();
        PROXY.registerCommands();
        PROXY.initNetwork();
        PROXY.initBrewing();

        CombatTriggerHandler.init();
        dev.architectury.event.events.common.LifecycleEvent.SETUP.register(() -> {
            ItemInit.initializeAttributes();
            PROXY.loadComplete();
        });
    }
}
