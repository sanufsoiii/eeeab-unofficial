package com.eeeab.eeeabsmobs.server;

import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalLaser;
import com.eeeab.eeeabsmobs.server.init.AttributeInit;
import com.eeeab.eeeabsmobs.server.message.*;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import com.eeeab.animate.server.message.MessageAnimation;
import com.eeeab.animate.server.message.MessagePlayAnimation;
import com.eeeab.animate.server.message.MessageStopAnimation;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * NeoForge's mod-bus/game-bus event system (RegisterCommandsEvent, EntityAttributeModificationEvent,
 * RegisterPayloadHandlersEvent/PayloadRegistrar) does not exist on Fabric - there is no generic event bus at
 * all. Each of those events is replaced below with the specific Fabric API hook that does the same job:
 * CommandRegistrationCallback for commands, and PayloadTypeRegistry/ServerPlayNetworking for networking. There
 * is no Fabric API equivalent of EntityAttributeModificationEvent (a hook that lets a mod append an attribute
 * to every already-registered vanilla LivingEntity's attribute map); see {@link #addCritChanceToAllLivingTypes()}
 * for how that gap is bridged.
 */
public class ServerProxy {
    public static final String VERSION = "1.0";

    /**
     * Replaces the old ModLoadingContext/ModConfigSpec-based config registration and the mod-bus-driven
     * data-attachment registration (CapabilityHandler.register(bus)). Both of those live in server/handler/**,
     * which is out of this pass's scope and still entirely NeoForge-shaped (ModConfigSpec, AttachmentType,
     * DeferredRegister<AttachmentType<?>>) - neither compiles on Fabric yet. Once that package is ported,
     * whatever it exposes needs to be invoked from EEEABMobs.onInitialize() in place of this comment.
     */
    public void initMod() {
        addCritChanceToAllLivingTypes();
        initWorldgen();
        com.eeeab.eeeabsmobs.server.entity.trigger.CombatTriggerEvent.register();
    }

    /**
     * NeoForge's BiomeModifier registry (data/eeeabsmobs/neoforge/biome_modifier/*.json, e.g.
     * add_ancient_boundary_stone.json: "type": "neoforge:add_features", biomes "#minecraft:is_overworld",
     * step "underground_ores") has no Fabric counterpart - Fabric ignores that JSON entirely, and
     * ResourceKey&lt;BiomeModifier&gt; / NeoForgeRegistries.Keys.BIOME_MODIFIERS do not exist on Fabric.
     * fabric-biome-api-v1's BiomeModifications is the idiomatic replacement (verified with javap against the
     * Loom-mapped jar: BiomeModifications.addFeature(Predicate&lt;BiomeSelectionContext&gt;,
     * GenerationStep.Decoration, ResourceKey&lt;PlacedFeature&gt;), with BiomeSelectors.foundInOverworld()
     * matching the same #minecraft:is_overworld tag the JSON used. So yes - the ancient boundary stone ore
     * still generates in overworld biomes on Fabric, just wired through code instead of a data file.
     */
    private void initWorldgen() {
        net.fabricmc.fabric.api.biome.v1.BiomeModifications.addFeature(
                net.fabricmc.fabric.api.biome.v1.BiomeSelectors.foundInOverworld(),
                net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES,
                com.eeeab.eeeabsmobs.server.util.ModResourceKey.ANCIENT_BOUNDARY_STONE_CHECKED);
    }

    /**
     * NeoForge removed BrewingRecipeRegistry.addRecipe (marked @ApiStatus.Internal) and replaced it with the
     * RegisterBrewingRecipesEvent mod-bus event. Fabric never had BrewingRecipeRegistry to begin with -
     * Fabric API instead patches PotionBrewing.Builder to implement FabricBrewingRecipeRegistryBuilder
     * (verified with javap against the Loom-mapped jar: "class PotionBrewing$Builder implements
     * net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder") and exposes a BUILD event that
     * runs once when PotionBrewing.bootstrap() assembles the vanilla + modded recipe table. This is the
     * idiomatic Fabric hook, not a guess: FabricBrewingRecipeRegistryBuilder.registerPotionRecipe takes an
     * Ingredient rather than a single Item, so ModBrewingRecipe (server/util, out of scope) is not reusable
     * here and the recipe is registered inline instead.
     */
    public void initBrewing() {
        net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
                builder.registerPotionRecipe(
                        net.minecraft.world.item.alchemy.Potions.STRONG_STRENGTH,
                        net.minecraft.world.item.crafting.Ingredient.of(com.eeeab.eeeabsmobs.server.init.ItemInit.HEART_OF_PAGAN.get()),
                        com.eeeab.eeeabsmobs.server.init.PotionInit.FRENZY_POTION.holder()
                ));
    }

    /**
     * NeoForge's EntityAttributeModificationEvent hands out the (mutable, at that point) AttributeSupplier
     * for every vanilla LivingEntity type so a mod can append its own Attribute to each one. Fabric has no
     * equivalent - fabric-api ships no attribute-registry module at all (checked: no *attribute* jar under
     * any fabric-api artifact for 0.116.15+1.21.1). Vanilla's own DefaultAttributes.SUPPLIERS map is a plain
     * private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> built once at classload, and
     * each AttributeSupplier's own "instances" map is an ImmutableMap (confirmed via javap -c: AttributeSupplier$
     * Builder.create() pushes into a com.google.common.collect.ImmutableMap$Builder). Neither map is designed to
     * be mutated after the fact.
     * <p>
     * This reflectively replaces each AttributeSupplier's ImmutableMap with a copy that additionally carries
     * CRIT_CHANCE, run once at mod init before any entity is constructed. It reproduces NeoForge's runtime
     * effect exactly (every living entity type ends up with a CRIT_CHANCE AttributeInstance), but it is a
     * reflection-based substitution for a missing platform hook, not a real Fabric API, and needs the mod-init
     * ordering to hold (nothing may read DefaultAttributes.getSupplier(...) before this runs).
     */
    private void addCritChanceToAllLivingTypes() {
        try {
            // The field name ("instances") is a Yarn/MojMap name that Loom does NOT remap when it
            // appears as a reflection string literal, so it fails at runtime under intermediary
            // mappings. Locate it by type instead: AttributeSupplier holds exactly one Map field.
            Field instancesField = null;
            for (Field f : AttributeSupplier.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(f.getType())) {
                    instancesField = f;
                    break;
                }
            }
            if (instancesField == null) {
                throw new NoSuchFieldException("no Map field on AttributeSupplier");
            }
            instancesField.setAccessible(true);
            Holder<Attribute> critChance = AttributeInit.CRIT_CHANCE.holder();
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                if (!DefaultAttributes.hasSupplier(type)) continue;
                @SuppressWarnings("unchecked")
                EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) type;
                AttributeSupplier supplier = DefaultAttributes.getSupplier(livingType);
                @SuppressWarnings("unchecked")
                Map<Holder<Attribute>, AttributeInstance> existing =
                        (Map<Holder<Attribute>, AttributeInstance>) instancesField.get(supplier);
                if (existing.containsKey(critChance)) continue;
                AttributeInstance instance = new AttributeInstance(critChance, ignored -> {});
                Map<Holder<Attribute>, AttributeInstance> replacement = ImmutableMap.<Holder<Attribute>, AttributeInstance>builder()
                        .putAll(existing)
                        .put(critChance, instance)
                        .build();
                instancesField.set(supplier, replacement);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to graft eeeabsmobs:crit_chance onto vanilla entity attribute maps", e);
        }
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

    /**
     * NeoForge's RegisterCommandsEvent -> Fabric API's CommandRegistrationCallback (fabric-command-api-v2,
     * verified present among the remapped mod jars for this build). Both hand out the same brigadier
     * CommandDispatcher<CommandSourceStack> and CommandBuildContext; Commands.CommandSelection replaces
     * NeoForge's Commands.CommandSelection-less event (Fabric's callback additionally reports whether this is
     * a dedicated/integrated-server registration, unused here).
     */
    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> onCommandRegister(dispatcher, context));
    }

    public void onCommandRegister(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        //jar时注释这段代码
        //CombatHintHudCommand.register(dispatcher, context);
    }

    /**
     * NeoForge replaced the SimpleChannel/NetworkRegistry.ChannelBuilder API with RegisterPayloadHandlersEvent's
     * PayloadRegistrar. Fabric's networking API (fabric-networking-api-v1) splits the same job into two pieces
     * that both sides must call: PayloadTypeRegistry.playS2C()/playC2S() to register each payload's StreamCodec
     * (needed by whichever side serializes it, so both directions are registered from common init), and
     * ServerPlayNetworking/ClientPlayNetworking.registerGlobalReceiver(...) to attach the handler on the side
     * that actually receives it. This method does the common (type-registration) half plus the server-side (C2S)
     * receiver half; ClientProxy does the client-side (S2C) receiver half.
     * <p>
     * The server/message/** Handler classes still implement NeoForge's IPayloadHandler<T> (server/message is out
     * of this pass's scope) - once that package is ported to Fabric's PlayPayloadHandler<T> shape, its Handler
     * instances plug directly into registerGlobalReceiver below in place of the inline lambdas.
     */
    public void initNetwork() {
        PayloadTypeRegistry.playS2C().register(ICapabilityMessage.TYPE, ICapabilityMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(UseAbilityMessage.TYPE, UseAbilityMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerUseAbilityMessage.TYPE, PlayerUseAbilityMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateBossBarMessage.TYPE, UpdateBossBarMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PopupNotificationMessage.TYPE, PopupNotificationMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(MessageAnimation.TYPE, MessageAnimation.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(MessagePlayAnimation.TYPE, MessagePlayAnimation.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(MessageStopAnimation.TYPE, MessageStopAnimation.STREAM_CODEC);

        // Server receives these two (C2S). Handlers here are placeholders until server/message/** exposes
        // Fabric-shaped handlers - see class javadoc.
        ServerPlayNetworking.registerGlobalReceiver(PlayerUseAbilityMessage.TYPE, PlayerUseAbilityMessage::handle);
        ServerPlayNetworking.registerGlobalReceiver(MessagePlayAnimation.TYPE, MessagePlayAnimation::handle);
    }
}
