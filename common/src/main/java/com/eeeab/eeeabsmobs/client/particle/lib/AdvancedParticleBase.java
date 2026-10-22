package com.eeeab.eeeabsmobs.client.particle.lib;

import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.data.AdvancedParticleData;
import com.eeeab.eeeabsmobs.server.util.ModMathUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

// 参考自: https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/util/AdvancedParticleBase.java
// 1.21.9: TextureSheetParticle was removed; base is now SingleQuadParticle and drawing goes through the
// deferred QuadParticleRenderState pipeline (extract()) instead of an immediate render(VertexConsumer,...).
// The custom per-mode quaternion is preserved and fed to the inherited extractRotatedQuad(...) helper, which
// builds the rotated quad at the particle's interpolated position. Component#postRender (which used to draw
// extra geometry via a VertexConsumer) is dropped - no component in this mod overrode it to draw.
public class AdvancedParticleBase extends SingleQuadParticle {
    public float airDiffusionSpeed;
    public float red, green, blue;
    public float prevRed, prevGreen, prevBlue, prevAlpha;
    public float scale, prevScale, particleScale;
    public ParticleRotation rotation;
    public boolean emissive;
    public double prevMotionX, prevMotionY, prevMotionZ;
    public SpriteSet spriteSet;

    public ParticleComponent[] components;
    public ParticleRibbon ribbon;

    protected AdvancedParticleBase(ClientLevel worldIn, double pX, double pY, double pZ, double motionX, double motionY, double motionZ, ParticleRotation rotation, double scale, double r, double g, double b, double a, double airDiffusionSpeed, double duration, boolean emissive, boolean canCollide, ParticleComponent[] components, SpriteSet spriteSet) {
        super(worldIn, pX, pY, pZ, 0.0D, 0.0D, 0.0D, null);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.red = (float) (r);
        this.green = (float) (g);
        this.blue = (float) (b);
        this.alpha = (float) (a);
        this.scale = (float) scale;
        this.lifetime = (int) duration;
        this.airDiffusionSpeed = (float) airDiffusionSpeed;
        this.rotation = rotation;
        this.components = components;
        this.emissive = emissive;
        this.ribbon = null;

        for (ParticleComponent component : components) {
            component.init(this);
        }

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.prevRed = this.red;
        this.prevGreen = this.green;
        this.prevBlue = this.blue;
        this.prevAlpha = this.alpha;
        this.rotation.setPrevValues();
        this.prevScale = this.scale;
        this.hasPhysics = canCollide;
        this.spriteSet = spriteSet;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        if (emissive) {
            int k = i >> 16 & 255;
            return 240 | k << 16;
        } else return i;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.particleScale * 0.1F;
    }

    @Override
    public void tick() {
        prevRed = red;
        prevGreen = green;
        prevBlue = blue;
        prevAlpha = alpha;
        prevScale = scale;
        rotation.setPrevValues();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        prevMotionX = xd;
        prevMotionY = yd;
        prevMotionZ = zd;

        for (ParticleComponent component : components) {
            component.preUpdate(this);
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            if (spriteSet != null) {
                this.setSpriteFromAge(spriteSet);
            }
        }

        updatePosition();

        for (ParticleComponent component : components) {
            component.postUpdate(this);
        }

        if (ribbon != null) {
            ribbon.setPos(x, y, z);
            ribbon.positions[0] = new Vec3(x, y, z);
            ribbon.prevPositions[0] = getPrevPos();
        }
    }

    protected void updatePosition() {
        this.move(this.xd, this.yd, this.zd);

        if (this.onGround && hasPhysics) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
        }

        this.xd *= airDiffusionSpeed;
        this.yd *= airDiffusionSpeed;
        this.zd *= airDiffusionSpeed;
    }

    @Override
    public void extract(QuadParticleRenderState renderState, Camera renderInfo, float partialTicks) {
        this.alpha = prevAlpha + (alpha - prevAlpha) * partialTicks;
        if (alpha < 0.01) alpha = 0.01f;
        this.rCol = prevRed + (red - prevRed) * partialTicks;
        this.gCol = prevGreen + (green - prevGreen) * partialTicks;
        this.bCol = prevBlue + (blue - prevBlue) * partialTicks;
        particleScale = prevScale + (scale - prevScale) * partialTicks;

        for (ParticleComponent component : components) {
            component.preRender(this, renderInfo, partialTicks);
        }

        Quaternionf quaternion = new Quaternionf();
        if (rotation instanceof ParticleRotation.FaceCamera faceCameraRot) {
            if (faceCameraRot.faceCameraAngle == 0.0F && faceCameraRot.prevFaceCameraAngle == 0.0F) {
                quaternion = new Quaternionf(renderInfo.rotation());
            } else {
                quaternion = new Quaternionf(renderInfo.rotation());
                quaternion.rotateZ(Mth.lerp(partialTicks, faceCameraRot.prevFaceCameraAngle, faceCameraRot.faceCameraAngle));
            }
        } else if (rotation instanceof ParticleRotation.EulerAngles eulerRot) {
            float rotX = eulerRot.prevPitch + (eulerRot.pitch - eulerRot.prevPitch) * partialTicks;
            float rotY = eulerRot.prevYaw + (eulerRot.yaw - eulerRot.prevYaw) * partialTicks;
            float rotZ = eulerRot.prevRoll + (eulerRot.roll - eulerRot.prevRoll) * partialTicks;
            Quaternionf quatX = ModMathUtils.rotationXYZ(rotX, 0, 0, false);
            Quaternionf quatY = ModMathUtils.rotationXYZ(0, rotY, 0, false);
            Quaternionf quatZ = ModMathUtils.rotationXYZ(0, 0, rotZ, false);
            quaternion.mul(quatZ);
            quaternion.mul(quatY);
            quaternion.mul(quatX);
        } else if (rotation instanceof ParticleRotation.OrientVector orientRot) {
            double x = orientRot.prevOrientation.x + (orientRot.orientation.x - orientRot.prevOrientation.x) * partialTicks;
            double y = orientRot.prevOrientation.y + (orientRot.orientation.y - orientRot.prevOrientation.y) * partialTicks;
            double z = orientRot.prevOrientation.z + (orientRot.orientation.z - orientRot.prevOrientation.z) * partialTicks;
            float pitch = (float) Math.asin(-y);
            float yaw = (float) (Mth.atan2(x, z));
            Quaternionf quatX = ModMathUtils.rotationXYZ(pitch, 0, 0, false);
            Quaternionf quatY = ModMathUtils.rotationXYZ(0, yaw, 0, false);
            quaternion.mul(quatY);
            quaternion.mul(quatX);
        } else if (rotation instanceof ParticleRotation.FaceCameraVertical vertical) {
            double px = Mth.lerp(partialTicks, this.xo, this.x);
            double py = Mth.lerp(partialTicks, this.yo, this.y);
            double pz = Mth.lerp(partialTicks, this.zo, this.z);
            Vec3 vec3 = new Vec3(px, py, pz);
            quaternion = vertical.getRotation(renderInfo, vec3);
        }

        this.extractRotatedQuad(renderState, renderInfo, quaternion, partialTicks);
    }

    // The mod's old public `alpha` field is now the inherited protected SingleQuadParticle#alpha; expose it so
    // out-of-package ParticleComponents can still read/write particle opacity.
    public float getAlpha() {
        return this.alpha;
    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAge() {
        return age;
    }

    public double getPosX() {
        return x;
    }

    public void setPosX(double posX) {
        setPos(posX, this.y, this.z);
    }

    public double getPosY() {
        return y;
    }

    public void setPosY(double posY) {
        setPos(this.x, posY, this.z);
    }

    public double getPosZ() {
        return z;
    }

    public void setPosZ(double posZ) {
        setPos(this.x, this.y, posZ);
    }

    public double getMotionX() {
        return xd;
    }

    public void setMotionX(double motionX) {
        this.xd = motionX;
    }

    public double getMotionY() {
        return yd;
    }

    public void setMotionY(double motionY) {
        this.yd = motionY;
    }

    public double getMotionZ() {
        return zd;
    }

    public void setMotionZ(double motionZ) {
        this.zd = motionZ;
    }

    public Vec3 getPrevPos() {
        return new Vec3(xo, yo, zo);
    }

    public double getPrevPosX() {
        return xo;
    }

    public double getPrevPosY() {
        return yo;
    }

    public double getPrevPosZ() {
        return zo;
    }

    public Level getWorld() {
        return level;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
public static class Factory implements ParticleProvider<AdvancedParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle createParticle(AdvancedParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, net.minecraft.util.RandomSource randomSource) {
            AdvancedParticleBase particle;
            boolean isAnimation = typeIn.isAnimation();
            particle = new AdvancedParticleBase(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getRotation(), typeIn.getScale(), typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getAlpha(), typeIn.getAirDiffusionSpeed(), typeIn.getDuration(), typeIn.isEmissive(), typeIn.getCanCollide(), typeIn.getComponents(), isAnimation ? spriteSet : null);
            if (isAnimation) particle.setSpriteFromAge(spriteSet);
            else particle.setSprite(spriteSet.get(particle.random));
            particle.setColor((float) typeIn.getRed(), (float) typeIn.getGreen(), (float) typeIn.getBlue());
            return particle;
        }
    }

    public static AdvancedParticleData createParticleData(ParticleType<AdvancedParticleData> particle, ParticleRotation rotation,
                                                          double scale, double r, double g, double b, double a, double airDiffusionSpeed,
                                                          double duration, boolean emissive, boolean canCollide, ParticleComponent[] components, boolean isAnimation) {
        return new AdvancedParticleData(particle, rotation, scale, r, g, b, a, airDiffusionSpeed, duration, emissive, canCollide, components, isAnimation);
    }

    public static void spawnParticle(Level level, ParticleType<AdvancedParticleData> particle, double x, double y, double z, double motionX, double motionY, double motionZ, boolean faceCamera, double yaw, double pitch, double roll, double faceCameraAngle, double scale, double r, double g, double b, double a, double airDiffusionSpeed, double duration, boolean emissive, boolean canCollide, boolean isAnimation, ParticleComponent[] components) {
        ParticleRotation rotation = faceCamera ? new ParticleRotation.FaceCamera((float) faceCameraAngle) : new ParticleRotation.EulerAngles((float) yaw, (float) pitch, (float) roll);
        AdvancedParticleData data = createParticleData(particle, rotation, scale, r, g, b, a, airDiffusionSpeed, duration,
                emissive, canCollide, components, isAnimation);
        level.addParticle(data, x, y, z, motionX, motionY, motionZ);
    }

    public static void spawnCustomRotationParticle(Level level, ParticleType<AdvancedParticleData> particle, double x, double y, double z, double motionX, double motionY, double motionZ, ParticleRotation rotation, double scale, double r, double g, double b, double a, double airDiffusionSpeed, double duration, boolean emissive, boolean canCollide, ParticleComponent[] components, boolean isAnimation) {
        AdvancedParticleData data = createParticleData(particle, rotation, scale, r, g, b, a, airDiffusionSpeed, duration,
                emissive, canCollide, components, isAnimation);
        level.addParticle(data, x, y, z, motionX, motionY, motionZ);
    }

    public static void spawnEmptyComponentParticle(Level level, ParticleType<AdvancedParticleData> particle, double x, double y, double z, double motionX, double motionY, double motionZ, boolean faceCamera, double yaw, double pitch, double roll, double faceCameraAngle, double scale, double r, double g, double b, double a, double airDiffusionSpeed, double duration, boolean emissive, boolean canCollide, boolean isAnimation) {
        spawnParticle(level, particle, x, y, z, motionX, motionY, motionZ, faceCamera, yaw, pitch, roll, faceCameraAngle, scale, r, g, b, a,
                airDiffusionSpeed, duration, emissive, canCollide, isAnimation, new ParticleComponent[]{});
    }
}
