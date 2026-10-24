package com.eeeab.eeeabsmobs.platform;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.util.ModItemModels;
import com.eeeab.eeeabsmobs.client.render.BossBarTexReloadListener;
import com.eeeab.eeeabsmobs.client.render.util.ModArmorStackRenderProperties;
import com.eeeab.eeeabsmobs.client.render.util.ModItemStackRender;
import com.eeeab.eeeabsmobs.server.capability.*;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import com.eeeab.eeeabsmobs.server.handler.ClientEventHandler;
import com.eeeab.eeeabsmobs.server.init.AttributeInit;
import com.eeeab.eeeabsmobs.server.init.ItemInit;
import com.eeeab.eeeabsmobs.server.init.PotionInit;
import com.eeeab.eeeabsmobs.server.util.ModResourceKey;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.eeeab.eeeabsmobs.server.capability.IEeeabEntityDataHolder;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class FabricEEEABPlatform implements EEEABPlatform {

    static {
        dev.architectury.event.events.common.PlayerEvent.PLAYER_CLONE.register((oldPlayer, newPlayer, wonGame) -> {
            if (oldPlayer instanceof IEeeabEntityDataHolder oldHolder && newPlayer instanceof IEeeabEntityDataHolder newHolder) {
                Object[] oldCaps = oldHolder.eeeab$getCapabilities();
                Object[] newCaps = newHolder.eeeab$getCapabilities();
                if (oldCaps[3] instanceof AbilityCapability.IAbilityCapability oldAbility) {
                    AbilityCapability.IAbilityCapability newAbility = new AbilityCapability.AbilityCapabilityImp();
                    newAbility.deserializeNBT(oldAbility.serializeNBT());
                    newCaps[3] = newAbility;
                }
            }
        });
    }

    private static final EEEABNetwork NETWORK = new FabricEEEABNetwork();

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(Entity entity, CapabilityHandler.CapabilityToken<T> token) {
        if (!(entity instanceof IEeeabEntityDataHolder holder) || !entity.isAlive()) return null;
        Object[] caps = holder.eeeab$getCapabilities();
        int idx = token.getIndex();
        if (caps[idx] == null) {
            caps[idx] = switch (idx) {
                case 0 -> new StunCapability.StunCapabilityImp();
                case 1 -> new FrenzyCapability.FrenzyCapabilityImpl();
                case 2 -> new ElectricityCapability.IElectricityCapabilityImpl();
                case 3 -> new AbilityCapability.AbilityCapabilityImp();
                case 4 -> new ThreatMemoryCapability.IThreatMemoryCapabilityImpl();
                default -> null;
            };
        }
        return (T) caps[idx];
    }

    @Override
    public void registerFlammable(Block block, int flammability, int fireSpreadSpeed) {
        FlammableBlockRegistry.getDefaultInstance().add(block, flammability, fireSpreadSpeed);
    }

    @Override
    public void registerStrippable(Block input, Block stripped) {
        StrippableBlockRegistry.register(input, stripped);
    }

    @Override
    public void initWorldgen() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ModResourceKey.ANCIENT_BOUNDARY_STONE_CHECKED);
    }

    @Override
    public void initBrewing() {
        FabricBrewingRecipeRegistry.registerPotionRecipe(
                Potions.STRONG_STRENGTH,
                Ingredient.of(ItemInit.HEART_OF_PAGAN.get()),
                PotionInit.FRENZY_POTION.get()
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initAttributes() {
        try {
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
            Attribute critChance = AttributeInit.CRIT_CHANCE.get();
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                if (!DefaultAttributes.hasSupplier(type)) continue;
                EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) type;
                AttributeSupplier supplier = DefaultAttributes.getSupplier(livingType);
                Map<Attribute, AttributeInstance> instances =
                        (Map<Attribute, AttributeInstance>) instancesField.get(supplier);
                if (instances != null && !instances.containsKey(critChance)) {
                    ImmutableMap.Builder<Attribute, AttributeInstance> builder = ImmutableMap.builder();
                    builder.putAll(instances);
                    builder.put(critChance, new AttributeInstance(critChance, inst -> {}));
                    instancesField.set(supplier, builder.build());
                }
            }
        } catch (ReflectiveOperationException e) {
            EEEABMobs.LOGGER.error("Failed to inject CRIT_CHANCE into DefaultAttributes", e);
        }
    }

    @Override
    public ModelResourceLocation getModelHandId(String item) {
        return new ModelResourceLocation(new ResourceLocation(EEEABMobs.MOD_ID, "item/" + item + "_in_hand"), "fabric_resource");
    }

    @Override
    public void registerSpecialModels() {
        ModelLoadingPlugin.register(context -> {
            ResourceLocation[] ids = java.util.Arrays.stream(ModItemModels.HAND_MODEL_ITEMS)
                    .map(item -> new ResourceLocation(EEEABMobs.MOD_ID, "item/" + item + "_in_hand"))
                    .toArray(ResourceLocation[]::new);
            context.addModels(ids);
        });
    }

    @Override
    public void registerItemRenderers() {
        ModItemStackRender bewlr = new ModItemStackRender();
        BuiltinItemRendererRegistry.INSTANCE.register(ItemInit.THE_NETHERWORLD_KATANA.get(), bewlr::renderByItem);
        BuiltinItemRendererRegistry.INSTANCE.register(ItemInit.GUARDIAN_AXE.get(), bewlr::renderByItem);
        BuiltinItemRendererRegistry.INSTANCE.register(ItemInit.BUSTER_GAUNTLET.get(), bewlr::renderByItem);
        BuiltinItemRendererRegistry.INSTANCE.register(ItemInit.IMMORTAL_STAFF.get(), new ModItemModels()::renderByItem);

        ModArmorStackRenderProperties armorRenderer = new ModArmorStackRenderProperties();
        ArmorRenderer.register(armorRenderer,
                ItemInit.GHOST_WARRIOR_HELMET.get(), ItemInit.GHOST_WARRIOR_CHESTPLATE.get(),
                ItemInit.GHOST_WARRIOR_LEGGINGS.get(), ItemInit.GHOST_WARRIOR_BOOTS.get());
    }

    @Override
    public void registerMenuScreens() {
        net.minecraft.client.gui.screens.MenuScreens.register(com.eeeab.eeeabsmobs.server.init.MenuInit.ANIMATION_CONTROLLER.get(), com.eeeab.animate.client.gui.AnimationControllerScreen::new);
    }

    @Override
    public void registerWorldRender() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            PoseStack poseStack = context.matrixStack();
            poseStack.pushPose();
            Vec3 camera = context.camera().getPosition();
            poseStack.translate(-camera.x, -camera.y, -camera.z);
            ClientEventHandler.renderWorld(context.tickDelta(), poseStack, Minecraft.getInstance().renderBuffers().bufferSource());
            poseStack.popPose();
        });
    }

    @Override
    public void registerClientReloadListener() {
        BossBarTexReloadListener delegate = new BossBarTexReloadListener();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(EEEABMobs.MOD_ID, "boss_bar_textures");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager,
                                                  ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler,
                                                  Executor backgroundExecutor, Executor gameExecutor) {
                return delegate.reload(barrier, resourceManager, prepareProfiler, applyProfiler, backgroundExecutor, gameExecutor);
            }
        });
    }

    @Override
    public EEEABNetwork getNetwork() {
        return NETWORK;
    }
}
