package com.pcbuilder.domain;

import com.pcbuilder.validation.ComputerBuildValidationException;

/**
 * All validation rules for ComputerBuild.Builder live here, one rule per
 * method. This used to be a single big if-chain inside build() itself - see
 * report.md, Part E, "Before -> After" #1, for why it got pulled out.
 *
 * Package-private on purpose: only ComputerBuild.Builder is supposed to call
 * this, nothing outside the domain package needs it directly.
 */
final class ComputerBuildValidator {

    private static final int MIN_RAM_GB = 4;
    private static final int MIN_PSU_WATTAGE = 300;
    private static final int HIGH_END_GPU_MIN_PSU_WATTAGE = 650;
    private static final int HIGH_END_GPU_MIN_RAM_GB = 8;

    private ComputerBuildValidator() {
        // not meant to be instantiated
    }

    static void validate(ComputerBuild.Builder builder) {
        requireBuildName(builder);
        requireMinimumRam(builder);
        requireStorageSize(builder);
        requireMinimumPsu(builder);
        requireHighEndGpuHasEnoughPower(builder);
        requireEnoughRamForHighEndGpu(builder);
    }

    // ---- single-field rules ----

    private static void requireBuildName(ComputerBuild.Builder builder) {
        if (builder.buildName() == null || builder.buildName().isBlank()) {
            throw new ComputerBuildValidationException("buildName must not be blank");
        }
    }

    private static void requireMinimumRam(ComputerBuild.Builder builder) {
        if (builder.ramGB() < MIN_RAM_GB) {
            throw new ComputerBuildValidationException(
                    "ramGB must be at least " + MIN_RAM_GB + " GB, got " + builder.ramGB());
        }
    }

    private static void requireStorageSize(ComputerBuild.Builder builder) {
        if (builder.storageGB() <= 0) {
            throw new ComputerBuildValidationException(
                    "storageGB must be greater than 0, got " + builder.storageGB());
        }
    }

    private static void requireMinimumPsu(ComputerBuild.Builder builder) {
        if (builder.psuWattage() < MIN_PSU_WATTAGE) {
            throw new ComputerBuildValidationException(
                    "psuWattage must be at least " + MIN_PSU_WATTAGE + "W, got " + builder.psuWattage());
        }
    }

    // ---- cross-field rules ----

    /**
     * The individual constraint for this variant: a HIGH_END GPU draws enough
     * power and heat that it needs both a beefier PSU and liquid cooling.
     * Two conditions, one rule, because they describe the same real-world fact.
     */
    private static void requireHighEndGpuHasEnoughPower(ComputerBuild.Builder builder) {
        if (builder.gpuTier() != GpuTier.HIGH_END) {
            return;
        }
        if (builder.psuWattage() < HIGH_END_GPU_MIN_PSU_WATTAGE) {
            throw new ComputerBuildValidationException(
                    "HIGH_END gpuTier requires psuWattage >= " + HIGH_END_GPU_MIN_PSU_WATTAGE
                            + "W, got " + builder.psuWattage());
        }
        if (builder.coolingType() != CoolingType.LIQUID) {
            throw new ComputerBuildValidationException(
                    "HIGH_END gpuTier requires liquidCooled(), got " + builder.coolingType());
        }
    }

    private static void requireEnoughRamForHighEndGpu(ComputerBuild.Builder builder) {
        if (builder.gpuTier() == GpuTier.HIGH_END && builder.ramGB() < HIGH_END_GPU_MIN_RAM_GB) {
            throw new ComputerBuildValidationException(
                    "HIGH_END gpuTier needs at least " + HIGH_END_GPU_MIN_RAM_GB
                            + "GB of RAM to avoid bottlenecking, got " + builder.ramGB());
        }
    }
}