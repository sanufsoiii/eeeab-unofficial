package com.eeeab.eeeabsmobs.platform;

import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public interface EEEABPlatform {

    @Nullable
    <T> T getCapability(Entity entity, CapabilityHandler.CapabilityToken<T> token);

    void registerFlammable(Block block, int flammability, int fireSpreadSpeed);

    void registerStrippable(Block input, Block stripped);

    void initWorldgen();

    void initBrewing();

    void initAttributes();

    ModelResourceLocation getModelHandId(String item);

    void registerSpecialModels();

    void registerItemRenderers();

    void registerWorldRender();

    void registerClientReloadListener();

    EEEABNetwork getNetwork();
}
