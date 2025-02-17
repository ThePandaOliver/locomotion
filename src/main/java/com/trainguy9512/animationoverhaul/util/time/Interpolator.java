package com.trainguy9512.animationoverhaul.util.time;

/**
 * @author Marvin Schürz
 */
@FunctionalInterface
public interface Interpolator<T>  {

    T interpolate(T a, T b, float t);

}
