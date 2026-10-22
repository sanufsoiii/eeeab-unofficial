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
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
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

    private static final AttachmentType<StunCapability.IStunCapability> STUN_CAPABILITY =
            registerAttachment("stun", StunCapability.StunCapabilityImp::new, StunCapability.StunCapabilityImp::new);
    private static final AttachmentType<FrenzyCapability.IFrenzyCapability> FRENZY_CAPABILITY =
            registerAttachment("frenzy", FrenzyCapability.FrenzyCapabilityImpl::new, FrenzyCapability.FrenzyCapabilityImpl::new);
    private static final AttachmentType<ElectricityCapability.IElectricityCapability> ELECTRICITY_CAPABILITY =
            registerAttachment("electricity", ElectricityCapability.IElectricityCapabilityImpl::new, ElectricityCapability.IElectricityCapabilityImpl::new);
    private static final AttachmentType<AbilityCapability.IAbilityCapability> ABILITY_CAPABILITY =
            registerAbilityAttachment();
    private static final AttachmentType<ThreatMemoryCapability.IThreatMemoryCapability> THREAT_MEMORY_CAPABILITY =
            AttachmentRegistry.<ThreatMemoryCapability.IThreatMemoryCapability>builder()
                    .initializer(ThreatMemoryCapability.IThreatMemoryCapabilityImpl::new)
                    .buildAndRegister(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "threat_memory"));

    private static final AttachmentType<?>[] ATTACHMENTS = new AttachmentType<?>[]{
            STUN_CAPABILITY,
            FRENZY_CAPABILITY,
            ELECTRICITY_CAPABILITY,
            ABILITY_CAPABILITY,
            THREAT_MEMORY_CAPABILITY
    };

    private static final EEEABNetwork NETWORK = new FabricEEEABNetwork();

    private static <T extends NbtCapability> AttachmentType<T> registerAttachment(String name, Supplier<T> initializer, Supplier<T> forCodec) {
        Codec<T> codec = CompoundTag.CODEC.xmap(nbt -> {
            T value = forCodec.get();
            value.deserializeNBT(nbt);
            return value;
        }, NbtCapability::serializeNBT);
        return AttachmentRegistry.<T>builder()
                .initializer(initializer)
                .persistent(codec)
                .buildAndRegister(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, name));
    }

    private static AttachmentType<AbilityCapability.IAbilityCapability> registerAbilityAttachment() {
        Codec<AbilityCapability.IAbilityCapability> codec = CompoundTag.CODEC.xmap(nbt -> {
            AbilityCapability.IAbilityCapability value = new AbilityCapability.AbilityCapabilityImp();
            value.deserializeNBT(nbt);
            return value;
        }, NbtCapability::serializeNBT);
        return AttachmentRegistry.<AbilityCapability.IAbilityCapability>builder()
                .initializer(AbilityCapability.AbilityCapabilityImp::new)
                .persistent(codec)
                .buildAndRegister(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "ability"));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(Entity entity, CapabilityHandler.CapabilityToken<T> token) {
        if (entity == null || !entity.isAlive()) return null;
        AttachmentType<?> att = ATTACHMENTS[token.getIndex()];
        return (T) ((AttachmentTarget) entity).getAttachedOrCreate((AttachmentType<Object>) att);
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
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
                builder.registerPotionRecipe(
                        Potions.STRONG_STRENGTH,
                        Ingredient.of(ItemInit.HEART_OF_PAGAN.get()),
                        PotionInit.FRENZY_POTION.holder()
                ));
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
            Holder<Attribute> critChance = AttributeInit.CRIT_CHANCE.holder();
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                if (!DefaultAttributes.hasSupplier(type)) continue;
                EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) type;
                AttributeSupplier supplier = DefaultAttributes.getSupplier(livingType);
                Map<Holder<Attribute>, AttributeInstance> instances =
                        (Map<Holder<Attribute>, AttributeInstance>) instancesField.get(supplier);
                if (instances != null && !instances.containsKey(critChance)) {
                    ImmutableMap.Builder<Holder<Attribute>, AttributeInstance> builder = ImmutableMap.builder();
                    builder.putAll(instances);
                    builder.put(critChance, new AttributeInstance(critChance, AttributeInstance::getAttribute));
                    instancesField.set(supplier, builder.build());
                }
            }
        } catch (ReflectiveOperationException e) {
            EEEABMobs.LOGGER.error("Failed to inject CRIT_CHANCE into DefaultAttributes", e);
        }
    }

    @Override
    public ModelResourceLocation getModelHandId(String item) {
        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "item/" + item + "_in_hand"), "fabric_resource");
    }

    @Override
    public void registerSpecialModels() {
        ModelLoadingPlugin.register(context -> {
            ResourceLocation[] ids = java.util.Arrays.stream(ModItemModels.HAND_MODEL_ITEMS)
                    .map(item -> ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "item/" + item + "_in_hand"))
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
    public void registerWorldRender() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            PoseStack poseStack = context.matrixStack();
            poseStack.pushPose();
            Vec3 camera = context.camera().getPosition();
            poseStack.translate(-camera.x, -camera.y, -camera.z);
            ClientEventHandler.renderWorld(context.tickCounter().getGameTimeDeltaPartialTick(false), poseStack, Minecraft.getInstance().renderBuffers().bufferSource());
            poseStack.popPose();
        });
    }

    @Override
    public void registerClientReloadListener() {
        BossBarTexReloadListener delegate = new BossBarTexReloadListener();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "boss_bar_textures");
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
