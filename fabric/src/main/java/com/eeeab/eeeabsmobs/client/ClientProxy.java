package com.eeeab.eeeabsmobs.client;

import com.eeeab.eeeabsmobs.client.gui.BossBarRegistry;
import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import com.eeeab.eeeabsmobs.client.render.BossBarTexReloadListener;
import com.eeeab.eeeabsmobs.client.render.LightningManager;
import com.eeeab.eeeabsmobs.client.render.util.ModArmorStackRenderProperties;
import com.eeeab.eeeabsmobs.client.render.util.ModItemStackRender;
import com.eeeab.eeeabsmobs.server.init.ItemInit;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import com.eeeab.eeeabsmobs.client.sound.ImmortalLaserSound;
import com.eeeab.eeeabsmobs.client.sound.ability.GuardianLaserSound;
import com.eeeab.eeeabsmobs.server.ServerProxy;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalLaser;
import com.eeeab.eeeabsmobs.server.init.MenuInit;
import com.eeeab.eeeabsmobs.server.integration.curios.CuriosRegistry;
import com.eeeab.eeeabsmobs.server.message.ICapabilityMessage;
import com.eeeab.eeeabsmobs.server.message.PopupNotificationMessage;
import com.eeeab.eeeabsmobs.server.message.UpdateBossBarMessage;
import com.eeeab.eeeabsmobs.server.message.UseAbilityMessage;
import com.eeeab.animate.client.gui.AnimationControllerScreen;
import com.eeeab.animate.server.message.MessageAnimation;
import com.eeeab.animate.server.message.MessageStopAnimation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import com.eeeab.eeeabsmobs.server.handler.RegisterHandler;
import com.eeeab.eeeabsmobs.server.handler.ClientEventHandler;

/**
 * Declared as the "client" entrypoint in fabric.mod.json, so it now implements ClientModInitializer instead of
 * overriding proxy hooks called from a shared mod-bus. Fabric instantiates a *separate* ClientProxy instance
 * for that entrypoint - not the same object as EEEABMobs.PROXY (built reflectively in EEEABMobs so common code
 * can call e.g. playImmortalLaserSound() without a compile-time reference to this class). That's harmless here
 * since every mutable field on this class is static.
 * <p>
 * Everything that used to run in RegisterClientReloadListenersEvent, RegisterMenuScreensEvent and
 * FMLClientSetupEvent now runs directly in {@link #onInitializeClient()} - Fabric has no equivalent staged
 * client-setup events, just this one entrypoint method.
 */
@Environment(EnvType.CLIENT)
public class ClientProxy extends ServerProxy implements ClientModInitializer {
    public static final Int2ObjectMap<AbstractTickableSoundInstance> ENTITY_SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();
    public static final Map<UUID, Identifier> BOSS_BAR_REGISTRY_NAMES = new HashMap<>();
    public static final LightningManager LIGHTNING_RENDER = new LightningManager();

    @Override
    public void onInitializeClient() {
        // Entity/block-entity renderers, model layers, particle providers and model registration all live
        // in RegisterHandler, which was ported to Fabric's registries in a later pass than the one that wrote
        // this method -- so it was fully converted and simply never called. Without this the game had no
        // renderer for any of the mod's entities: opening the second creative tab page or spawning anything
        // crashed with "entityRenderer is null".
        RegisterHandler.registerAll();
        ClientEventHandler.register();

        // RegisterClientReloadListenersEvent -> Fabric ResourceLoader (fabric-resource-loader-v1, the
        // replacement for the removed ResourceManagerHelper/IdentifiableResourceReloadListener API).
        // The listener now takes the new SharedState-based PreparableReloadListener signature.
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(
                Identifier.fromNamespaceAndPath("eeeabsmobs", "boss_bar_textures"), new BossBarTexReloadListener());

        // RegisterMenuScreensEvent -> MenuScreens.register directly. NeoForge's comment about MenuScreens
        // .register(MenuType, ScreenConstructor) "becoming private in 1.21" is a NeoForge-side access
        // transform, not a vanilla change: javap against the Loom-mapped 1.21.1 jar shows it public
        // (net.minecraft.client.gui.screens.MenuScreens.register(MenuType<? extends M>, ScreenConstructor)),
        // so plain vanilla + Fabric can call it straight from client init.
        MenuScreens.register(MenuInit.ANIMATION_CONTROLLER.get(), AnimationControllerScreen::new);

        // Client-received (S2C) network payloads - the server-received (C2S) half and all type registration
        // live in ServerProxy.initNetwork(), called from EEEABMobs.onInitialize() before this runs.
        ClientPlayNetworking.registerGlobalReceiver(ICapabilityMessage.TYPE, ICapabilityMessage.Client::handle);
        ClientPlayNetworking.registerGlobalReceiver(UseAbilityMessage.TYPE, UseAbilityMessage.Client::handle);
        ClientPlayNetworking.registerGlobalReceiver(UpdateBossBarMessage.TYPE, UpdateBossBarMessage.Client::handle);
        ClientPlayNetworking.registerGlobalReceiver(PopupNotificationMessage.TYPE, PopupNotificationMessage.Client::handle);
        ClientPlayNetworking.registerGlobalReceiver(MessageAnimation.TYPE, MessageAnimation.Client::handle);
        ClientPlayNetworking.registerGlobalReceiver(MessageStopAnimation.TYPE, MessageStopAnimation.Client::handle);

        // Formerly FMLClientSetupEvent.enqueueWork(...).
        TipNotificationHandler.init();
        BossBarRegistry.init();
        CuriosRegistry.initClient();

        // Was each item's IClientItemExtensions#initializeClient (NeoForge, invoked via
        // RegisterClientExtensionsEvent - no Fabric equivalent event exists). Fabric API's
        // ArmorRenderer (fabric-rendering-v1, verified with javap) registers the same custom
        // armor renderers directly, up front, here.
        // NOTE: Fabric removed BuiltinItemRendererRegistry in this version (the whole immediate-mode
        // per-item renderer concept is gone - items now render through the ItemStackRenderState /
        // ItemModel pipeline). Custom hand/inventory models for the_netherworld_katana,
        // guardian_axe, buster_gauntlet and immortal_staff now need the data-driven client item
        // model definitions (assets/<ns>/items/*.json, with per-view-context model selection via
        // "minecraft:select"/"condition" types) instead of ModItemStackRender/ModItemModels. That
        // asset-side wiring is left for a human pass with in-game visual verification.
        ModArmorStackRenderProperties armorRenderer = new ModArmorStackRenderProperties();
        ArmorRenderer.register(armorRenderer,
                ItemInit.GHOST_WARRIOR_HELMET.get(), ItemInit.GHOST_WARRIOR_CHESTPLATE.get(),
                ItemInit.GHOST_WARRIOR_LEGGINGS.get(), ItemInit.GHOST_WARRIOR_BOOTS.get());
    }

    @Override
    public void playImmortalLaserSound(EntityImmortalLaser laser) {
        Minecraft.getInstance().getSoundManager().play(new ImmortalLaserSound(laser));
    }

    @Override
    public void playTickableSound(Entity entity, int id) {
        if (!entity.level().isClientSide()) return;
        AbstractTickableSoundInstance cache = ENTITY_SOUND_INSTANCE_MAP.get(entity.getId());
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        if (id == 0) {
            if (entity instanceof Player player) {
                GuardianLaserSound sound;
                if (cache instanceof GuardianLaserSound guardianLaserSound) {
                    sound = guardianLaserSound;
                } else {
                    sound = new GuardianLaserSound(player);
                }
                if (!soundManager.isActive(sound) && sound.canPlaySound()) {
                    soundManager.queueTickingSound(sound);
                    ENTITY_SOUND_INSTANCE_MAP.put(entity.getId(), sound);
                }
            }
        }
    }

    @Override
    public void stopTickableSound(Entity entity) {
        ENTITY_SOUND_INSTANCE_MAP.remove(entity.getId());
    }
}
