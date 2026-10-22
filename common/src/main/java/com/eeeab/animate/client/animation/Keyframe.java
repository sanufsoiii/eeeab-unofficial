package com.eeeab.animate.client.animation;
import org.joml.Vector3f;
public record Keyframe(float timestamp, Vector3f target, AnimationChannel.Interpolation interpolation) {
}