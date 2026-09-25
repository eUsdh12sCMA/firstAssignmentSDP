package com.pcbuilder.domain;

/**
 * How much graphics power the build has
 * HIGH_END is where most of our validation rules kick in (see ComputerBuildValidator).
 */
public enum GpuTier {
    NONE,
    MID_RANGE,
    HIGH_END
}