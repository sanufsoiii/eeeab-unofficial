package com.eeeab.animate.server.animation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;

public class OverlapAnimationState extends AnimationState {
    private final int duration;

    public OverlapAnimationState(int duration) {
        this.duration = duration;
    }

    public OverlapAnimationState(Animation animation) {
        this.duration = animation.getDuration();
    }

    // 1.21.9 removed AnimationState#updateTime/getAccumulatedTime (time is now computed on demand via
    // getTimeInMillis(ageInTicks)). This is no longer an override - it is called explicitly by the model each
    // frame to auto-stop the overlap animation once its duration elapses.
    public void updateTime(float ageInTicks, float speed) {
        if (this.isStarted()) {
            int accumulateTick = Mth.floor(this.getTimeInMillis(ageInTicks) * speed / 1000F * 20F);
            if (accumulateTick >= this.duration) {
                this.stop();
            }
        }
    }
}
