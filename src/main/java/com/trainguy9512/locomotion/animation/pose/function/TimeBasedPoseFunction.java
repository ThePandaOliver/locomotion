package com.trainguy9512.locomotion.animation.pose.function;

import com.trainguy9512.locomotion.animation.pose.AnimationPose;
import com.trainguy9512.locomotion.util.TimeSpan;

import java.util.function.Function;

public abstract class TimeBasedPoseFunction<P extends AnimationPose> implements PoseFunction<P> {

    protected final Function<FunctionEvaluationState, Boolean> isPlayingFunction;
    protected final Function<FunctionEvaluationState, Float> playRateFunction;
    protected float resetStartTimeOffsetTicks;

    protected float timeTicksElapsed;
    protected float playRate;
    protected boolean isPlaying;

    protected TimeBasedPoseFunction(Function<FunctionEvaluationState, Boolean> isPlayingFunction, Function<FunctionEvaluationState, Float> playRateFunction, float resetStartTimeOffsetTicks){
        this.isPlayingFunction = isPlayingFunction;
        this.playRateFunction = playRateFunction;
        this.resetStartTimeOffsetTicks = resetStartTimeOffsetTicks;

        this.timeTicksElapsed = this.resetStartTimeOffsetTicks;
    }

    @Override
    public void tick(FunctionEvaluationState evaluationState) {
        this.isPlaying = isPlayingFunction.apply(evaluationState);
        this.playRate = this.isPlaying ? playRateFunction.apply(evaluationState) : 0;

        this.updateTime(evaluationState);
    }

    protected void updateTime(FunctionEvaluationState evaluationState) {
        if (evaluationState.isResetting()) {
            this.resetTime();
        }
        if (this.isPlaying) {
            this.timeTicksElapsed += this.playRate;
        }
    }

    protected void resetTime(){
        this.timeTicksElapsed = this.resetStartTimeOffsetTicks;
    }

    protected TimeSpan getInterpolatedTimeElapsed(FunctionInterpolationContext context){
        return TimeSpan.ofTicks(this.timeTicksElapsed - (1 - context.partialTicks()) * this.playRate);
    }

    public static class Builder<B> {

        protected Function<FunctionEvaluationState, Float> playRateFunction;
        protected Function<FunctionEvaluationState, Boolean> isPlayingFunction;
        protected float resetStartTimeOffsetTicks;

        protected Builder() {
            this.playRateFunction = (interpolationContext) -> 1f;
            this.isPlayingFunction = (interpolationContext) -> true;
            this.resetStartTimeOffsetTicks = 0;
        }

        /**
         * Sets the function that is used to update the function's play rate each tick.
         * <p>
         * The play rate is used as a multiplier when incrementing time, so 1 is normal time, 0.5 is half as fast, and 2 is twice as fast.
         */
        @SuppressWarnings("unchecked")
        public B setPlayRate(Function<FunctionEvaluationState, Float> playRateFunction) {
            this.playRateFunction = playRateFunction;
            return (B) this;
        }

        /**
         * Sets a constant play rate.
         * <p>
         * The play rate is used as a multiplier when incrementing time, so 1 is normal time, 0.5 is half as fast, and 2 is twice as fast.
         */
        @SuppressWarnings("unchecked")
        public B setPlayRate(float playRate) {
            this.playRateFunction = evaluationState -> playRate;
            return (B) this;
        }

        /**
         * Sets the function that is used to update whether the function is playing or not each tick.
         */
        @SuppressWarnings("unchecked")
        public B setIsPlaying(Function<FunctionEvaluationState, Boolean> isPlayingFunction) {
            this.isPlayingFunction = isPlayingFunction;
            return (B) this;
        }

        /**
         * Sets the reset start time offset, which is used to offset the start time when the function is reset. Default is 0.
         * @param startTimeOffset           Offset time.
         */
        @SuppressWarnings("unchecked")
        public B setResetStartTimeOffsetTicks(TimeSpan startTimeOffset) {
            this.resetStartTimeOffsetTicks = startTimeOffset.inTicks();
            return (B) this;
        }
    }
}
