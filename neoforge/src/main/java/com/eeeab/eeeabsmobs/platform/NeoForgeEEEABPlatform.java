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
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeEEEABPlatform implements EEEABPlatform {

    private static IEventBus MOD_BUS;

    public static void setModBus(IEventBus bus) {
        MOD_BUS = bus;
        ATTACHMENT_TYPES.register(bus);
    }

    public static IEventBus getModBus() {
        return MOD_BUS;
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EEEABMobs.MOD_ID);

    private static final Supplier<AttachmentType<StunCapability.IStunCapability>> STUN_CAPABILITY =
            registerAttachment("stun", StunCapability.StunCapabilityImp::new, StunCapability.StunCapabilityImp::new);
    private static final Supplier<AttachmentType<FrenzyCapability.IFrenzyCapability>> FRENZY_CAPABILITY =
            registerAttachment("frenzy", FrenzyCapability.FrenzyCapabilityImpl::new, FrenzyCapability.FrenzyCapabilityImpl::new);
    private static final Supplier<AttachmentType<ElectricityCapability.IElectricityCapability>> ELECTRICITY_CAPABILITY =
            registerAttachment("electricity", ElectricityCapability.IElectricityCapabilityImpl::new, ElectricityCapability.IElectricityCapabilityImpl::new);
    private static final Supplier<AttachmentType<AbilityCapability.IAbilityCapability>> ABILITY_CAPABILITY =
            registerAbilityAttachment();
    private static final Supplier<AttachmentType<ThreatMemoryCapability.IThreatMemoryCapability>> THREAT_MEMORY_CAPABILITY =
            ATTACHMENT_TYPES.register("threat_memory", () -> AttachmentType.<ThreatMemoryCapability.IThreatMemoryCapability>builder(ThreatMemoryCapability.IThreatMemoryCapabilityImpl::new).build());

    @SuppressWarnings("rawtypes")
    private static final Supplier[] ATTACHMENTS = new Supplier[]{
            STUN_CAPABILITY,
            FRENZY_CAPABILITY,
            ELECTRICITY_CAPABILITY,
            ABILITY_CAPABILITY,
            THREAT_MEMORY_CAPABILITY
    };

    private static final EEEABNetwork NETWORK = new NeoForgeEEEABNetwork();

    private static <T extends NbtCapability> Supplier<AttachmentType<T>> registerAttachment(String name, Supplier<T> initializer, Supplier<T> forCodec) {
        Codec<T> codec = CompoundTag.CODEC.xmap(nbt -> {
            T value = forCodec.get();
            value.deserializeNBT(nbt);
            return value;
        }, NbtCapability::serializeNBT);
        return ATTACHMENT_TYPES.register(name, () -> AttachmentType.builder(initializer::get).serialize(codec).build());
    }

    private static Supplier<AttachmentType<AbilityCapability.IAbilityCapability>> registerAbilityAttachment() {
        Codec<AbilityCapability.IAbilityCapability> codec = CompoundTag.CODEC.xmap(nbt -> {
            AbilityCapability.IAbilityCapability value = new AbilityCapability.AbilityCapabilityImp();
            value.deserializeNBT(nbt);
            return value;
        }, NbtCapability::serializeNBT);
        return ATTACHMENT_TYPES.register("ability", () -> AttachmentType.<AbilityCapability.IAbilityCapability>builder(AbilityCapability.AbilityCapabilityImp::new).serialize(codec).build());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @Nullable
    public <T> T getCapability(Entity entity, CapabilityHandler.CapabilityToken<T> token) {
        if (entity == null || !entity.isAlive()) return null;
        AttachmentType attachmentType = (AttachmentType) ATTACHMENTS[token.getIndex()].get();
        return (T) entity.getData(attachmentType);
    }

    @Override
    public void registerFlammable(Block block, int flammability, int fireSpreadSpeed) {
        ((FireBlock) Blocks.FIRE).setFlammable(block, flammability, fireSpreadSpeed);
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
    public void initWorldgen() {
        // Handled via data/eeeabsmobs/neoforge/biome_modifier/add_ancient_boundary_stone.json
    }

    @Override
    public void initBrewing() {
        NeoForge.EVENT_BUS.addListener((RegisterBrewingRecipesEvent event) ->
                event.getBuilder().addMix(
                        Potions.STRONG_STRENGTH,
                        ItemInit.HEART_OF_PAGAN.get(),
                        PotionInit.FRENZY_POTION.holder()
                ));
    }

    @Override
    public void initAttributes() {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((EntityAttributeModificationEvent event) -> {
                Holder<Attribute> critChance = AttributeInit.CRIT_CHANCE.holder();
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
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "item/" + item + "_in_hand"));
    }

    @Override
    public void registerSpecialModels() {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((ModelEvent.RegisterAdditional event) -> {
                for (String item : ModItemModels.HAND_MODEL_ITEMS) {
                    event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "item/" + item + "_in_hand")));
                }
            });
        }
    }

    @Override
    public void registerItemRenderers() {
        if (MOD_BUS != null) {
            MOD_BUS.addListener((RegisterClientExtensionsEvent event) -> {
                event.registerItem(new ModItemStackRenderProperties(),
                        ItemInit.THE_NETHERWORLD_KATANA.get(),
                        ItemInit.GUARDIAN_AXE.get(),
                        ItemInit.BUSTER_GAUNTLET.get()
                );
                event.registerItem(new ModArmorStackRenderProperties(),
                        ItemInit.GHOST_WARRIOR_HELMET.get(),
                        ItemInit.GHOST_WARRIOR_CHESTPLATE.get(),
                        ItemInit.GHOST_WARRIOR_LEGGINGS.get(),
                        ItemInit.GHOST_WARRIOR_BOOTS.get()
                );
            });
        }
    }

    @Override
    public void registerWorldRender() {
        NeoForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                PoseStack poseStack = event.getPoseStack();
                poseStack.pushPose();
                Vec3 camera = event.getCamera().getPosition();
                poseStack.translate(-camera.x, -camera.y, -camera.z);
                ClientEventHandler.renderWorld(
                        event.getPartialTick().getGameTimeDeltaPartialTick(false),
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
