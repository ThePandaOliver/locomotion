package com.trainguy9512.animationoverhaul.animation.pose.sampler;

import com.trainguy9512.animationoverhaul.animation.data.AnimationDriverContainer;
import com.trainguy9512.animationoverhaul.animation.data.PoseSamplerStateContainer;
import com.trainguy9512.animationoverhaul.animation.pose.AnimationPose;
import com.trainguy9512.animationoverhaul.animation.joint.JointSkeleton;

public interface SampleableFromInput {
    AnimationPose sample(AnimationDriverContainer animationDriverContainer, PoseSamplerStateContainer poseSamplerStateContainer, JointSkeleton jointSkeleton, AnimationPose inputPose);
}
