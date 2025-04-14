package com.trainguy9512.locomotion.mixin;

import com.trainguy9512.locomotion.LocomotionMain;
import com.trainguy9512.locomotion.animation.animator.JointAnimator;
import com.trainguy9512.locomotion.animation.animator.JointAnimatorDispatcher;
import com.trainguy9512.locomotion.animation.animator.entity.FirstPersonPlayerJointAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.BlocksAttacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlocksAttacks.class)
public class MixinBlocksAttacks {

    @Inject(
            method = "onBlocked",
            at = @At("HEAD")
    )
    public void playShieldImpactMontageOnShieldBlocked(ServerLevel serverLevel, LivingEntity livingEntity, CallbackInfo ci) {
        if (Minecraft.getInstance().isLocalPlayer(livingEntity.getUUID())) {
            LocomotionMain.LOGGER.info("blocked!");
            JointAnimatorDispatcher.getInstance().getFirstPersonPlayerDataContainer().ifPresent(dataContainer -> dataContainer.getDriver(FirstPersonPlayerJointAnimator.HAS_BLOCKED_ATTACK).trigger());
        }
    }
}
