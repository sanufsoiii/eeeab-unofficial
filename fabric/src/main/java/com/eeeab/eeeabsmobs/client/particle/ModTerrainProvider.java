package com.eeeab.eeeabsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModTerrainProvider extends TerrainParticle.Provider {
    @Override
    public Particle createParticle(BlockParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        BlockState blockstate = type.getState();
        if (blockstate.isAir() || blockstate.is(Blocks.MOVING_PISTON)) return null;
        // Was type.getPos() + Particle#updateSprite(BlockState, BlockPos), a NeoForge patch to BlockParticleOption
        // and TerrainParticle with no Fabric equivalent. Vanilla's own TerrainParticle already takes a BlockPos
        // directly in its constructor for the same texture-lookup purpose, so that overload is used instead,
        // with the position derived from the particle's own spawn coordinates the same way vanilla's particle
        // engine does for BLOCK-type particles.
        Particle particle = new TerrainParticle(level, x, y, z, 0, 0, 0, blockstate, BlockPos.containing(x, y, z));
        particle.setLifetime(particle.getLifetime() * 2);
        particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
        return particle;
    }
}
