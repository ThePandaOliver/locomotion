package com.trainguy9512.locomotion.animation.animator.entity;

import com.trainguy9512.locomotion.animation.animator.JointAnimator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public interface EntityJointAnimator<T extends Entity, S extends EntityRenderState> extends JointAnimator<T> {

    /**
     * Does some final operations on the entity model, similar to what would happen in the {@link EntityModel#setupAnim(EntityRenderState)} function. Called every frame after the interpolated pose is applied to the model
     * @param entityModel           Entity model
     * @param entityRenderState     Entity render state
     */
    public void postProcessModelParts(EntityModel<S> entityModel, S entityRenderState);
}
