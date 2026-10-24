package com.eeeab.eeeabsmobs.platform;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.util.ModItemModels;
import com.eeeab.eeeabsmobs.client.render.BossBarTexReloadListener;
import com.eeeab.eeeabsmobs.client.render.util.ModArmorStackRenderProperties;
import com.eeeab.eeeabsmobs.client.render.util.ModItemStackRenderProperties;
import com.eeeab.eeeabsmobs.server.capability.*;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import com.eeeab.eeeabsmobs.server.handler.ClientEventHandler;
import com.eeeab.eeeabsmobs.server.init.AttributeInit;
import com.eeeab.eeeabsmobs.server.init.ItemInit;
import com.eeeab.eeeabsmobs.server.init.PotionInit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ForgeEEEABPlatform implements EEEABPlatform {

    private static IEventBus MOD_BUS;

    public static void setModBus(IEventBus bus) {
        MOD_BUS = bus;
        MOD_BUS.addListener((RegisterCapabilitiesEvent event) -> {
            event.register(StunCapability.IStunCapability.class);
            event.register(FrenzyCapability.IFrenzyCapability.class);
            event.register(ElectricityCapability.IElectricityCapability.class);
            event.register(AbilityCapability.IAbilityCapability.class);
            event.register(ThreatMemoryCapability.IThreatMemoryCapability.class);
        });
    }

    public static final Capability<StunCapability.IStunCapability> STUN_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<FrenzyCapability.IFrenzyCapability> FRENZY_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<ElectricityCapability.IElectricityCapability> ELECTRICITY_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<AbilityCapability.IAbilityCapability> ABILITY_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<ThreatMemoryCapability.IThreatMemoryCapability> THREAT_MEMORY_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});

    private static final EEEABNetwork NETWORK = new ForgeEEEABNetwork();

    static {
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, (AttachCapabilitiesEvent<Entity> event) -> {
            if (event.getObject() instanceof LivingEntity) {
                event.addCapability(new ResourceLocation(EEEABMobs.MOD_ID, "stun"), new StunProvider());
                event.addCapability(new ResourceLocation(EEEABMobs.MOD_ID, "frenzy"), new FrenzyProvider());
                event.addCapability(new ResourceLocation(EEEABMobs.MOD_ID, "electricity"), new ElectricityProvider());
                event.addCapability(new ResourceLocation(EEEABMobs.MOD_ID, "ability"), new AbilityProvider());
                event.addCapability(new ResourceLocation(EEEABMobs.MOD_ID, "threat_memory"), new ThreatMemoryProvider());
            }
        });

        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> {
            event.getOriginal().getCapability(ABILITY_CAP).ifPresent(oldCap -> {
                event.getEntity().getCapability(ABILITY_CAP).ifPresent(newCap -> {
                    newCap.deserializeNBT(oldCap.serializeNBT());
                });
            });
        });
    }

    private static class StunProvider implements ICapabilitySerializable<CompoundTag> {
        private final StunCapability.IStunCapability instance = new StunCapability.StunCapabilityImp();
        private final LazyOptional<StunCapability.IStunCapability> opt = LazyOptional.of(() -> instance);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return STUN_CAP.orEmpty(cap, opt);
        }

        @Override
        public CompoundTag serializeNBT() {
            return instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.deserializeNBT(nbt);
        }
    }

    private static class FrenzyProvider implements ICapabilitySerializable<CompoundTag> {
        private final FrenzyCapability.IFrenzyCapability instance = new FrenzyCapability.FrenzyCapabilityImpl();
        private final LazyOptional<FrenzyCapability.IFrenzyCapability> opt = LazyOptional.of(() -> instance);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return FRENZY_CAP.orEmpty(cap, opt);
        }

        @Override
        public CompoundTag serializeNBT() {
            return instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.deserializeNBT(nbt);
        }
    }

    private static class ElectricityProvider implements ICapabilitySerializable<CompoundTag> {
        private final ElectricityCapability.IElectricityCapability instance = new ElectricityCapability.IElectricityCapabilityImpl();
        private final LazyOptional<ElectricityCapability.IElectricityCapability> opt = LazyOptional.of(() -> instance);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return ELECTRICITY_CAP.orEmpty(cap, opt);
        }

        @Override
        public CompoundTag serializeNBT() {
            return instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.deserializeNBT(nbt);
        }
    }

    private static class AbilityProvider implements ICapabilitySerializable<CompoundTag> {
        private final AbilityCapability.IAbilityCapability instance = new AbilityCapability.AbilityCapabilityImp();
        private final LazyOptional<AbilityCapability.IAbilityCapability> opt = LazyOptional.of(() -> instance);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return ABILITY_CAP.orEmpty(cap, opt);
        }

        @Override
        public CompoundTag serializeNBT() {
            return instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.deserializeNBT(nbt);
        }
    }

    private static class ThreatMemoryProvider implements ICapabilityProvider {
        private final ThreatMemoryCapability.IThreatMemoryCapability instance = new ThreatMemoryCapability.IThreatMemoryCapabilityImpl();
        private final LazyOptional<ThreatMemoryCapability.IThreatMemoryCapability> opt = LazyOptional.of(() -> instance);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return THREAT_MEMORY_CAP.orEmpty(cap, opt);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(Entity entity, CapabilityHandler.CapabilityToken<T> token) {
        if (entity == null || !entity.isAlive()) return null;
        Capability<?> cap = switch (token.getIndex()) {
            case 0 -> STUN_CAP;
            case 1 -> FRENZY_CAP;
            case 2 -> ELECTRICITY_CAP;
            case 3 -> ABILITY_CAP;
            case 4 -> THREAT_MEMORY_CAP;
            default -> null;
        };
        if (cap == null) return null;
        return (T) entity.getCapability(cap).orElse(null);
    }

    @Override
    public void registerFlammable(Block block, int flammability, int fireSpreadSpeed) {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        fireBlock.setFlammable(block, flammability, fireSpreadSpeed);
    }

    @Override
    public void registerStrippable(Block input, Block stripped) {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            Field field = AxeItem.class.getDeclaredField("STRIPPABLES");
            Object base = unsafe.staticFieldBase(field);
            long offset = unsafe.staticFieldOffset(field);
            @SuppressWarnings("unchecked")
            Map<Block, Block> strippables = (Map<Block, Block>) unsafe.getObject(base, offset);
            if (!(strippables instanceof HashMap)) {
                strippables = new HashMap<>(strippables);
                unsafe.putObject(base, offset, strippables);
            }
            strippables.put(input, stripped);
        } catch (Exception e) {
            EEEABMobs.LOGGER.error("Failed to register strippable block", e);
        }
    }

    @Override
    public void registerStrippable(java.util.function.Supplier<Block> input, java.util.function.Supplier<Block> stripped) {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) ->
                    event.enqueueWork(() -> registerStrippable(input.get(), stripped.get()))
            );
        } else {
            registerStrippable(input.get(), stripped.get());
        }
    }

    @Override
    public void initWorldgen() {
        // Handled via data/eeeabsmobs/forge/biome_modifier/add_ancient_boundary_stone.json
    }

    @Override
    public void initBrewing() {
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_STRENGTH)),
                Ingredient.of(ItemInit.HEART_OF_PAGAN.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), PotionInit.FRENZY_POTION.get())
        ));
    }

    @Override
    public void initAttributes() {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((EntityAttributeModificationEvent event) -> {
                Attribute critChance = AttributeInit.CRIT_CHANCE.get();
                for (EntityType<? extends LivingEntity> type : event.getTypes()) {
                    if (!event.has(type, critChance)) {
                        event.add(type, critChance);
                    }
                }
            });
        }
    }

    @Override
    public ModelResourceLocation getModelHandId(String item) {
        return new ModelResourceLocation(new ResourceLocation(EEEABMobs.MOD_ID, item + "_in_hand"), "inventory");
    }

    @Override
    public void registerSpecialModels() {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((ModelEvent.RegisterAdditional event) -> {
                for (String item : ModItemModels.HAND_MODEL_ITEMS) {
                    event.register(new ModelResourceLocation(new ResourceLocation(EEEABMobs.MOD_ID, item + "_in_hand"), "inventory"));
                }
            });
        }
    }

    private static void setItemClientExtension(Item item, Object extension) {
        try {
            Field field = Item.class.getDeclaredField("renderProperties");
            field.setAccessible(true);
            field.set(item, extension);
        } catch (Exception e) {
            EEEABMobs.LOGGER.error("Failed to set client extension for " + item, e);
        }
    }

    @Override
    public void registerItemRenderers() {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) ->
                    event.enqueueWork(this::doRegisterItemRenderers)
            );
        } else {
            doRegisterItemRenderers();
        }
    }

    private void doRegisterItemRenderers() {
        ModItemStackRenderProperties itemProps = new ModItemStackRenderProperties();
        setItemClientExtension(ItemInit.THE_NETHERWORLD_KATANA.get(), itemProps);
        setItemClientExtension(ItemInit.GUARDIAN_AXE.get(), itemProps);
        setItemClientExtension(ItemInit.BUSTER_GAUNTLET.get(), itemProps);

        ModArmorStackRenderProperties armorProps = new ModArmorStackRenderProperties();
        setItemClientExtension(ItemInit.GHOST_WARRIOR_HELMET.get(), armorProps);
        setItemClientExtension(ItemInit.GHOST_WARRIOR_CHESTPLATE.get(), armorProps);
        setItemClientExtension(ItemInit.GHOST_WARRIOR_LEGGINGS.get(), armorProps);
        setItemClientExtension(ItemInit.GHOST_WARRIOR_BOOTS.get(), armorProps);

        setItemClientExtension(ItemInit.IMMORTAL_STAFF.get(), new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new ModItemModels();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerMenuScreens() {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) ->
                    event.enqueueWork(() ->
                            net.minecraft.client.gui.screens.MenuScreens.register(com.eeeab.eeeabsmobs.server.init.MenuInit.ANIMATION_CONTROLLER.get(), com.eeeab.animate.client.gui.AnimationControllerScreen::new)
                    )
            );
        }
    }

    @Override
    public void registerWorldRender() {
        MinecraftForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                PoseStack poseStack = event.getPoseStack();
                poseStack.pushPose();
                Vec3 camera = event.getCamera().getPosition();
                poseStack.translate(-camera.x, -camera.y, -camera.z);
                ClientEventHandler.renderWorld(
                        event.getPartialTick(),
                        poseStack,
                        Minecraft.getInstance().renderBuffers().bufferSource()
                );
                poseStack.popPose();
            }
        });
    }

    @Override
    public void registerClientReloadListener() {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((RegisterClientReloadListenersEvent event) ->
                    event.registerReloadListener(new BossBarTexReloadListener())
            );
        }
    }

    @Override
    public EEEABNetwork getNetwork() {
        return NETWORK;
    }
}
