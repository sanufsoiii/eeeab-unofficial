package com.eeeab.eeeabsmobs;

import com.eeeab.eeeabsmobs.server.ServerProxy;
import com.eeeab.eeeabsmobs.server.advancement.ModCriteriaTriggers;
import com.eeeab.eeeabsmobs.server.entity.trigger.CombatTriggerHandler;
import com.eeeab.eeeabsmobs.server.init.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fabric has one flat entrypoint interface per side (ModInitializer for "main", ClientModInitializer for
 * "client" - see fabric.mod.json) instead of NeoForge's mod-bus events (FMLCommonSetupEvent,
 * FMLClientSetupEvent, FMLLoadCompleteEvent, RegisterMenuScreensEvent, ...) fired in sequence on a shared
 * constructor-injected IEventBus. There is no multi-phase mod-loading event ladder to hook into, so everything
 * that used to be split across those events now just runs, in order, inside {@link #onInitialize()}.
 * <p>
 * PROXY still exists and is still built via the same reflective trick as the NeoForge branch: ClientProxy is
 * declared @Environment(EnvType.CLIENT) (a Fabric annotation - RuntimeDistCleaner-equivalent stripping is done
 * by the Fabric loader's own class-stripping ASM pass on a dedicated server), so this class's bytecode must
 * never name ClientProxy directly or the server crashes resolving a stripped class during mod construction.
 */
public class EEEABMobs implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "eeeabsmobs";
    public static ServerProxy PROXY = createProxy();

    private static ServerProxy createProxy() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return new ServerProxy();
        }
        try {
            return (ServerProxy) Class.forName("com.eeeab.eeeabsmobs.client.ClientProxy")
                    .getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Running on the client but ClientProxy is missing", e);
        }
    }

    @Override
    public void onInitialize() {
        // Every XInit.register(...) call below exists only to force that class to classload - the
        // FabricDeferredRegister shim (server/init/FabricDeferredRegister.java) registers each entry into the
        // real vanilla registry the instant its static field initializer runs, not when register() is called.
        // The call itself is a no-op kept so the sequencing (and every XInit.FOO.get() call site across the
        // mod) doesn't need to change. Order matches the original NeoForge constructor, since some Init
        // classes reference others' fields (e.g. CreativeTabInit -> ItemInit/BlockInit).
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

        // NOTE (flagged per instructions): CapabilityHandler (server/handler/**, out of this pass's scope)
        // still registers NeoForge AttachmentType<?> via DeferredRegister and does not compile on Fabric yet.
        // Its Fabric replacement (Fabric API's data-attachment-api-v1, already present as a remapped mod jar)
        // needs to be wired in here once that package is ported.
        // CapabilityHandler.register(null);

        PROXY.initMod();
        PROXY.registerCommands();
        PROXY.initNetwork();
        PROXY.initBrewing();

        // Formerly FMLLoadCompleteEvent. Fabric has no distinct "all mods loaded" phase separate from
        // onInitialize() - main entrypoints already run after every mod's classes are discoverable, so this
        // just runs at the end of the same method.
        CombatTriggerHandler.init();
        ItemInit.initializeAttributes();
        PROXY.loadComplete();
    }
}
