package com.eeeab.eeeabsmobs.client;

import com.eeeab.animate.client.gui.AnimationControllerScreen;
import com.eeeab.eeeabsmobs.client.gui.BossBarRegistry;
import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import com.eeeab.eeeabsmobs.client.render.LightningManager;
import com.eeeab.eeeabsmobs.client.sound.ImmortalLaserSound;
import com.eeeab.eeeabsmobs.client.sound.ability.GuardianLaserSound;
import com.eeeab.eeeabsmobs.platform.PlatformServices;
import com.eeeab.eeeabsmobs.server.ServerProxy;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalLaser;
import com.eeeab.eeeabsmobs.server.handler.ClientEventHandler;
import com.eeeab.eeeabsmobs.server.handler.RegisterHandler;
import com.eeeab.eeeabsmobs.server.init.MenuInit;
import com.eeeab.eeeabsmobs.server.integration.curios.CuriosRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientProxy extends ServerProxy {
    public static final Int2ObjectMap<AbstractTickableSoundInstance> ENTITY_SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();
    public static final Map<UUID, ResourceLocation> BOSS_BAR_REGISTRY_NAMES = new HashMap<>();
    public static final LightningManager LIGHTNING_RENDER = new LightningManager();

    public static void init() {
        RegisterHandler.registerAll();
        ClientEventHandler.register();
        PlatformServices.get().registerClientReloadListener();
        MenuRegistry.registerScreenFactory(MenuInit.ANIMATION_CONTROLLER.get(), AnimationControllerScreen::new);
        TipNotificationHandler.init();
        BossBarRegistry.init();
        CuriosRegistry.initClient();
        PlatformServices.get().registerItemRenderers();
        PlatformServices.get().registerWorldRender();
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
