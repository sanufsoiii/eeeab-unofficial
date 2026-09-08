package com.eeeab.eeeabsmobs.server.command;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.message.PopupNotificationMessage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CombatHintHudCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(
                Commands.literal("popup")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("entityType", ResourceArgument.resource(buildContext, Registries.ENTITY_TYPE))
                                .suggests((SuggestionProvider<CommandSourceStack>) (SuggestionProvider<?>) SuggestionProviders.SUMMONABLE_ENTITIES)
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    Holder.Reference<EntityType<?>> entityTypeHolder = ResourceArgument.getSummonableEntityType(context, "entityType");
                                    Identifier entityType = entityTypeHolder.key().identifier();
                                    sendHint(player, entityType, 0);
                                    context.getSource().sendSuccess(() -> Component.literal("success"), false);
                                    return 1;
                                })
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 2))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            Holder.Reference<EntityType<?>> entityTypeHolder = ResourceArgument.getSummonableEntityType(context, "entityType");
                                            Identifier entityType = entityTypeHolder.key().identifier();
                                            int level = IntegerArgumentType.getInteger(context, "level");
                                            sendHint(player, entityType, level);
                                            context.getSource().sendSuccess(() -> Component.literal("success"), false);
                                            return 1;
                                        })
                                )
                        )
        );
    }

    private static void sendHint(ServerPlayer player, Identifier location, int level) {
        ServerPlayNetworking.send(player, new PopupNotificationMessage(location, level));
    }
}
