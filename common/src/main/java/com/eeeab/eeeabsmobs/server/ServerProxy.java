package com.eeeab.eeeabsmobs.server;

import com.eeeab.eeeabsmobs.platform.PlatformServices;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalLaser;
import com.eeeab.eeeabsmobs.server.entity.trigger.CombatTriggerEvent;
import com.eeeab.eeeabsmobs.server.message.ModNetwork;
import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;

public class ServerProxy {
    public static final String VERSION = "1.0";

    public void initMod() {
        PlatformServices.get().initAttributes();
        PlatformServices.get().initWorldgen();
        CombatTriggerEvent.register();
    }

    public void initBrewing() {
        PlatformServices.get().initBrewing();
    }

    public void loadComplete() {
    }

    public Object getISTERProperties() {
        return null;
    }

    public Object getASTEProperties() {
        return null;
    }

    public void playImmortalLaserSound(EntityImmortalLaser laser) {
    }

    public void playTickableSound(Entity entity, int id) {
    }

    public void stopTickableSound(Entity entity) {
    }

    public void registerCommands() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, selection) -> onCommandRegister(dispatcher, registryAccess));
    }

    public void onCommandRegister(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        com.eeeab.eeeabsmobs.server.command.CombatHintHudCommand.register(dispatcher, context);
    }

    public void initNetwork() {
        ModNetwork.init();
    }
}
