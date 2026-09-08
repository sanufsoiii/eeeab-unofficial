package com.eeeab.animate.client.animation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {
   @Environment(EnvType.CLIENT)
   public static class Builder {
      private final float length;
      private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
      private boolean looping;

      public static AnimationDefinition.Builder withLength(float pLengthInSeconds) {
         return new AnimationDefinition.Builder(pLengthInSeconds);
      }

      private Builder(float pLengthInSeconds) {
         this.length = pLengthInSeconds;
      }

      public AnimationDefinition.Builder looping() {
         this.looping = true;
         return this;
      }

      public AnimationDefinition.Builder addAnimation(String pBone, AnimationChannel pAnimationChannel) {
         this.animationByBone.computeIfAbsent(pBone, (p_232278_) -> {
            return Lists.newArrayList();
         }).add(pAnimationChannel);
         return this;
      }

      public AnimationDefinition build() {
         return new AnimationDefinition(this.length, this.looping, this.animationByBone);
      }
   }
}