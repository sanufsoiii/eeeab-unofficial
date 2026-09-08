package com.eeeab.eeeabsmobs;

import com.eeeab.animate.client.gui.AnimationControllerScreen;
import com.eeeab.eeeabsmobs.client.gui.BossBarRegistry;
import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import com.eeeab.eeeabsmobs.server.ServerProxy;
import com.eeeab.eeeabsmobs.server.advancement.ModCriteriaTriggers;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import com.eeeab.eeeabsmobs.server.handler.ModConfigHandler;
import com.eeeab.eeeabsmobs.server.handler.ServerEventHandler;
import com.eeeab.eeeabsmobs.server.init.*;
import com.eeeab.eeeabsmobs.server.integration.curios.CuriosRegistry;
import com.eeeab.eeeabsmobs.server.entity.trigger.CombatTriggerEvent;
import com.eeeab.eeeabsmobs.server.entity.trigger.CombatTriggerHandler;
import com.eeeab.eeeabsmobs.server.util.ModBrewingRecipe;
import com.eeeab.eeeabsmobs.server.util.TranslateUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * NeoForge removed FMLJavaModLoadingContext and DistExecutor. The mod event bus (and, if declared, the
 * ModContainer) are now injected directly as constructor parameters by FML - see the loader-4.0.44 sources for
 * net.neoforged.fml.common.Mod / ModContainer. Side-specific construction (ClientProxy vs ServerProxy, formerly
 * DistExecutor.runForDist) is now a plain FMLEnvironment.dist check, since DistExecutor no longer exists.
 * @Mod.EventBusSubscriber was also removed - listeners are registered by hand in the constructor instead.
 */
@Mod(EEEABMobs.MOD_ID)
public class EEEABMobs {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "eeeabsmobs";
    public static ServerProxy PROXY = createProxy();

    /**
     * ClientProxy is annotated {@code @OnlyIn(Dist.CLIENT)}, so NeoForge's RuntimeDistCleaner strips
     * it on a dedicated server and refuses any attempt to load it. That means the class must not be
     * named in this class's bytecode at all -- neither a ternary nor a helper method returning
     * {@code new ClientProxy()} is enough, because resolving the constructor reference still asks
     * the class loader for it and the server dies during mod construction.
     * <p>
     * Forge's DistExecutor got away with {@code () -> ClientProxy::new} because the method reference
     * is an invokedynamic that is only linked when actually executed. NeoForge removed DistExecutor,
     * so the reference is deferred by hand with reflection, which leaves no constant-pool entry to
     * resolve on the wrong side.
     */
    private static ServerProxy createProxy() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return new ServerProxy();
        }
        try {
            return (ServerProxy) Class.forName("com.eeeab.eeeabsmobs.client.ClientProxy")
                    .getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Running on the client but ClientProxy is missing", e);
        }
    }

    private IEventBus modEventBus;

    public EEEABMobs(final IEventBus bus) {
        this.modEventBus = bus;
        ItemInit.register(bus);
        ModCriteriaTriggers.register(bus);
        BlockInit.register(bus);
        BlockEntityInit.register(bus);
        EntityInit.register(bus);
        CreativeTabInit.register(bus);
        AttributeInit.register(bus);
        EffectInit.register(bus);
        PotionInit.register(bus);
        ParticleInit.register(bus);
        SoundInit.register(bus);
        MenuInit.register(bus);
        StructuresInit.register(bus);
        WorldGenInit.register(bus);
        CapabilityHandler.register(bus);
        PROXY.initMod(bus);
        PROXY.initForge(NeoForge.EVENT_BUS);
        bus.addListener(this::setup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::registerMenuScreens);
        bus.addListener(this::complete);
        bus.addListener(this::onModConfigInit);
        bus.addListener(PROXY::initNetwork);
        // RegisterBrewingRecipesEvent extends Event without implementing IModBusEvent (checked with
        // javap against the real jar), so it is fired on the game bus, not the mod bus. Registering it
        // on the mod bus makes the bus reject the listener outright and kills mod construction.
        NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Upstream registered this class on the game bus too, but its only annotated method handles a
        // mod-bus event and is already registered explicitly in the constructor. NeoForge rejects both
        // shapes: an annotated mod-bus method found on the game bus, and a register() call on a class
        // with no game-bus handlers at all. So the call goes entirely.
        NeoForge.EVENT_BUS.register(new ServerEventHandler());
        NeoForge.EVENT_BUS.register(new CombatTriggerEvent());
        event.enqueueWork(CuriosRegistry::init);
    }

    // BrewingRecipeRegistry.addRecipe(IBrewingRecipe) was made internal (@ApiStatus.Internal) - brewing recipes
    // are registered through this mod-bus event instead (net.neoforged.neoforge.event.brewing.
    // RegisterBrewingRecipesEvent), which hands out a PotionBrewing.Builder with its own addRecipe(IBrewingRecipe).
    private void registerBrewingRecipes(final net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent event) {
        event.getBuilder().addRecipe(new ModBrewingRecipe(Potions.STRONG_STRENGTH, ItemInit.HEART_OF_PAGAN.get(), PotionInit.FRENZY_POTION));
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            TipNotificationHandler.init();
            BossBarRegistry.init();
            CuriosRegistry.initClient();
        });
    }

    // MenuScreens.register(MenuType, ScreenConstructor) became private in 1.21; mods now register client
    // screens through this mod-bus event instead (net.neoforged.neoforge.client.event.RegisterMenuScreensEvent).
    private void registerMenuScreens(final net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(MenuInit.ANIMATION_CONTROLLER.get(), AnimationControllerScreen::new);
    }

    private void complete(final FMLLoadCompleteEvent event) {
        CombatTriggerHandler.init();
        ItemInit.initializeAttributes();
        PROXY.loadComplete(modEventBus);
    }

    /**
     * Deliberately not annotated @SubscribeEvent. ModConfigEvent is a mod-bus event and this method is
     * already registered explicitly on the mod bus in the constructor, but the class as a whole is also
     * handed to NeoForge.EVENT_BUS.register(this) -- which scans for annotated methods and, on the game
     * bus, rejects a mod-bus signature outright, aborting common setup. Upstream carried the same
     * duplicate registration; Forge tolerated it, NeoForge validates and throws.
     */
    public void onModConfigInit(final ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == ModConfigHandler.COMMON_SPEC) {
            ModConfigHandler.Item item = ModConfigHandler.COMMON.items;
            updateItem(item.busterGauntlet);
            updateItem(item.chainsword);
            updateItem(item.doomboltAxe);
            //updateItem(item.netherworldKatana);
            TranslateUtils.SHOW_ITEM_CD = ModConfigHandler.COMMON.others.enableShowItemCD.get();
        }
    }

    private static void updateItem(ModConfigHandler.ToolConfig toolConfig) {
        toolConfig.attackDamageValue = toolConfig.attackDamage.get().floatValue();
        toolConfig.attackSpeedValue = toolConfig.attackSpeed.get().floatValue();
    }
}
