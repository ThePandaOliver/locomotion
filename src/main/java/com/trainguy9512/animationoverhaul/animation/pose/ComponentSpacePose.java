package com.trainguy9512.animationoverhaul.animation.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.trainguy9512.animationoverhaul.animation.joint.JointSkeleton;
import com.trainguy9512.animationoverhaul.animation.joint.JointTransform;
import org.joml.Matrix4f;

public class ComponentSpacePose extends AnimationPose {

    private ComponentSpacePose(JointSkeleton jointSkeleton) {
        super(jointSkeleton);
    }

    private ComponentSpacePose(AnimationPose pose){
        super(pose);
    }


    /**
     * Creates a blank animation pose using a joint skeleton as the template.
     * @param jointSkeleton         Template joint skeleton
     * @return                      New animation pose
     */
    public static ComponentSpacePose of(JointSkeleton jointSkeleton){
        return new ComponentSpacePose(jointSkeleton);
    }


    public static ComponentSpacePose of(AnimationPose pose){
        return new ComponentSpacePose(pose);
    }

    /**
     * Retrieves a copy of the transform for the supplied joint.
     * @param joint                 Joint string identifier
     * @return                      Joint transform
     */
    public JointTransform getComponentSpaceTransform(String joint){
        return JointTransform.of(this.jointTransforms.getOrDefault(joint, JointTransform.ZERO));
    }

    /**
     * Creates a local space pose from this component space pose.
     */
    public LocalSpacePose convertedToLocalSpace(){
        LocalSpacePose pose = LocalSpacePose.of(this);
        pose.convertChildrenJointsToLocalSpace(this.getJointSkeleton().getRootJoint(), new PoseStack());
        return pose;
    }
}
