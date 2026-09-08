package com.eeeab.eeeabsmobs.client.particle.lib;

import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.component.RibbonComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.data.RibbonParticleData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Vector4f;

//参考自: https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/ParticleRibbon.java
@Environment(EnvType.CLIENT)
public class ParticleRibbon extends AdvancedParticleBase {
    public Vec3[] positions;
    public Vec3[] prevPositions;

    public float texPanOffset;

    protected ParticleRibbon(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double motionX, double motionY, double motionZ, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, int length, ParticleComponent[] components, SpriteSet spriteSet) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, motionX, motionY, motionZ, rotation, scale, r, g, b, a, drag, duration, emissive, false, components, spriteSet);
        positions = new Vec3[length];
        prevPositions = new Vec3[length];
        if (positions.length >= 1) positions[0] = new Vec3(getPosX(), getPosY(), getPosZ());
        if (prevPositions.length >= 1) prevPositions[0] = getPrevPos();
    }

    @Override
    protected void updatePosition() {
        super.updatePosition();
    }

    // 1.21.9 note: the original custom ribbon/trail mesh was drawn immediately via a VertexConsumer strip,
    // which the deferred QuadParticleRenderState pipeline has no direct equivalent for. Rather than fake it,
    // ParticleRibbon now inherits AdvancedParticleBase#extract (renders as a single billboard quad). The
    // connected trail visual is lost pending a submitCustomGeometry-based reimplementation - verify/redo in-game.

    @Override
    public AABB getBoundingBox() {
        if (positions == null || positions.length <= 0 || positions[0] == null) return super.getBoundingBox();
        double minX = positions[0].x() - 0.1;
        double minY = positions[0].y() - 0.1;
        double minZ = positions[0].z() - 0.1;
        double maxX = positions[0].x() + 0.1;
        double maxY = positions[0].y() + 0.1;
        double maxZ = positions[0].z() + 0.1;
        for (Vec3 pos : positions) {
            if (pos == null) continue;
            minX = Math.min(minX, pos.x());
            minY = Math.min(minY, pos.y());
            minZ = Math.min(minZ, pos.z());
            maxX = Math.max(maxX, pos.x());
            maxY = Math.max(maxY, pos.y());
            maxZ = Math.max(maxZ, pos.z());
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public float getMinUPublic() {
        return getU0();
    }

    public float getMaxUPublic() {
        return getU1();
    }

    public float getMinVPublic() {
        return getV0();
    }

    public float getMaxVPublic() {
        return getV1();
    }

    @Environment(EnvType.CLIENT)
    public static final class Factory implements ParticleProvider<RibbonParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(RibbonParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, net.minecraft.util.RandomSource randomSource) {
            ParticleRibbon particle;
            if (typeIn.isAnimation()) {
                particle = new ParticleRibbon(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getRotation(), typeIn.getScale(), typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getAlpha(), typeIn.getAirDiffusionSpeed(), typeIn.getDuration(), typeIn.isEmissive(), typeIn.getLength(), typeIn.getComponents(), spriteSet);
                particle.setSpriteFromAge(spriteSet);
            } else {
                particle = new ParticleRibbon(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getRotation(), typeIn.getScale(), typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getAlpha(), typeIn.getAirDiffusionSpeed(), typeIn.getDuration(), typeIn.isEmissive(), typeIn.getLength(), typeIn.getComponents(), null);
                particle.setSprite(spriteSet.get(randomSource));
            }
            return particle;
        }
    }

    public static void spawnRibbon(Level world, ParticleType<? extends RibbonParticleData> particle, int length, double x, double y, double z, double motionX, double motionY, double motionZ, boolean faceCamera, double yaw, double pitch, double roll, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, boolean isAnimation) {
        spawnRibbon(world, particle, length, x, y, z, motionX, motionY, motionZ, faceCamera, yaw, pitch, roll, scale, r, g, b, a, drag, duration, emissive, new ParticleComponent[]{}, isAnimation);
    }

    public static void spawnRibbon(Level world, ParticleType<? extends RibbonParticleData> particle, int length, double x, double y, double z, double motionX, double motionY, double motionZ, boolean faceCamera, double yaw, double pitch, double roll, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, ParticleComponent[] components, boolean isAnimation) {
        ParticleRotation rotation = faceCamera ? new ParticleRotation.FaceCamera((float) 0) : new ParticleRotation.EulerAngles((float) yaw, (float) pitch, (float) roll);
        world.addParticle(new RibbonParticleData(particle, rotation, scale, r, g, b, a, drag, duration, emissive, length, components, isAnimation), x, y, z, motionX, motionY, motionZ);
    }
}