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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * NeoForge removed FMLJavaModLoadingContext and DistExecutor. The mod event bus (and, if declared, the
 * ModContainer) are now injected directly as constructor parameters by FML - see the loader-4.0.44 sources for
 * net.minecraftforge.fml.common.Mod / ModContainer. Side-specific construction (ClientProxy vs ServerProxy, formerly
 * DistExecutor.runForDist) is now a plain FMLEnvironment.dist check, since DistExecutor no longer exists.
 * @Mod.EventBusSubscriber was also removed - listeners are registered by hand in the constructor instead.
 */
@Mod(EEEABMobs.MOD_ID)
public class EEEABMobs {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "eeeabsmobs";
    public static ServerProxy PROXY = createProxy();
    // Forge has no drop-in for NeoForge's payload-registry networking; it still uses the classic
    // SimpleChannel API (see ServerProxy#initNetwork), which - unlike NeoForge's PacketDistributor - is not
    // static and has to be reached through a channel instance held somewhere. This mirrors where the
    // Forge 1.20.1 original kept it.
    public static net.minecraftforge.network.SimpleChannel NETWORK;

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

    /**
     * Forge constructs the @Mod class with either its own FMLJavaModLoadingContext or no arguments at
     * all (confirmed in FMLModContainer.constructMod's bytecode: it looks up a one-arg constructor of
     * that type, then a no-arg one). NeoForge instead hands the mod event bus straight to the
     * constructor, which is what this class was written for -- hence "NoSuchMethodException:
     * EEEABMobs.<init>()" and a dead mod on Forge. The bus is fetched from the context instead.
     */
    public EEEABMobs() {
        final IEventBus bus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
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
        // Forge's capability system (unlike NeoForge's data attachments) needs two separate hooks: which
        // interfaces are capabilities (mod bus, RegisterCapabilitiesEvent) and which objects carry which
        // capability instances (Forge bus, AttachCapabilitiesEvent<Entity> via addGenericListener) - see
        // CapabilityHandler. This is the original Forge 1.20.1 wiring, confirmed unchanged in the 1.21.1 jar.
        bus.addListener(CapabilityHandler::registerCapabilities);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityHandler::attachEntityCapability);
        PROXY.initMod(bus);
        PROXY.initForge(MinecraftForge.EVENT_BUS);
        bus.addListener(this::setup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::complete);
        bus.addListener(this::onModConfigInit);
        // Forge's networking (SimpleChannel) is built directly, not through a registration event - there is
        // no Forge equivalent of RegisterPayloadHandlersEvent, so this is called eagerly instead of added as
        // a listener.
        PROXY.initNetwork();
        // BrewingRecipeRegisterEvent extends Event without implementing IModBusEvent (checked with javap
        // against the real jar), so it is fired on the game bus, not the mod bus.
        MinecraftForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Upstream registered this class on the game bus too, but its only annotated method handles a
        // mod-bus event and is already registered explicitly in the constructor. NeoForge rejects both
        // shapes: an annotated mod-bus method found on the game bus, and a register() call on a class
        // with no game-bus handlers at all. So the call goes entirely.
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        MinecraftForge.EVENT_BUS.register(new CombatTriggerEvent());
        event.enqueueWork(CuriosRegistry::init);
    }

    // Forge (unlike NeoForge, whose BrewingRecipeRegistry.addRecipe was made @ApiStatus.Internal) still fires
    // this on the Forge event bus - net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent, verified
    // against the real jar with javap - and hands out a PotionBrewing.Builder with its own
    // addRecipe(IBrewingRecipe), so ModBrewingRecipe (which already implements IBrewingRecipe) plugs straight in.
    private void registerBrewingRecipes(final net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent event) {
        event.addRecipe(new ModBrewingRecipe(Potions.STRONG_STRENGTH, ItemInit.HEART_OF_PAGAN.get(),
                PotionInit.FRENZY_POTION.getHolder().orElseThrow()));
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            TipNotificationHandler.init();
            BossBarRegistry.init();
            CuriosRegistry.initClient();
            // net.minecraftforge.client.event.RegisterMenuScreensEvent does not exist in Forge (that's a
            // NeoForge redesign) - MenuScreens.register is still public here (verified against the real jar
            // with javap), same as Forge 1.20.1, so this goes back to a direct call from client setup.
            net.minecraft.client.gui.screens.MenuScreens.register(MenuInit.ANIMATION_CONTROLLER.get(), AnimationControllerScreen::new);
        });
    }

    private void complete(final FMLLoadCompleteEvent event) {
        CombatTriggerHandler.init();
        ItemInit.initializeAttributes();
        PROXY.loadComplete(modEventBus);
    }

    /**
     * Deliberately not annotated @SubscribeEvent. ModConfigEvent is a mod-bus event and this method is
     * already registered explicitly on the mod bus in the constructor, but the class as a whole is also
     * handed to MinecraftForge.EVENT_BUS.register(this) -- which scans for annotated methods and, on the game
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
