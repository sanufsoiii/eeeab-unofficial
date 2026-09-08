package com.eeeab.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * NeoForge widens {@code Mob#goalSelector}/{@code #targetSelector} (protected final in vanilla) to public via
 * its access-transformer file, which is how ServerEventHandler's old EntityJoinLevelEvent handler could add
 * extra AI goals to arbitrary vanilla mobs (skeletons/zombies/villagers/raiders) from outside the class
 * hierarchy. Fabric Loom has no equivalent AT mechanism available to this mod (an access widener has to be
 * declared by the mod owning the widened class, i.e. Minecraft itself, and build.gradle - out of this port's
 * scope - was not touched to add one). A {@code @Accessor} mixin is the standard Fabric replacement for exactly
 * this situation: it does not change the field's real access modifier, it generates a synthetic public
 * getter that reads the already-protected field directly, verified against the same field names/types found
 * with {@code javap -p} on the Loom-mapped 1.21.1 jar (both `protected final GoalSelector`).
 */
@Mixin(Mob.class)
public interface MobAccessor {
    @Accessor("goalSelector")
    GoalSelector eeeabmobs$getGoalSelector();

    @Accessor("targetSelector")
    GoalSelector eeeabmobs$getTargetSelector();
}
