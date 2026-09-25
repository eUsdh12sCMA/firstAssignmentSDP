package com.pcbuilder.domain;

import com.pcbuilder.validation.ComputerBuildValidationException;

/**
 * A validated computer build.
 *
 * This class has no public constructor, the only way to get an instance is
 * through {@link Builder#build()}, which guarantees every ComputerBuild that
 * exists in the program satisfies the rules in {@link ComputerBuildValidator}.
 * All fields are final and there are no setters, so once built, a
 * ComputerBuild cannot be put into an invalid state later.
 */
public final class ComputerBuild {

    private final String buildName;
    private final String cpuModel;
    private final int ramGB;
    private final int storageGB;
    private final StorageType storageType;
    private final GpuTier gpuTier;
    private final int psuWattage;
    private final CoolingType coolingType;
    private final boolean rgbEnabled;
    private final String operatingSystem;
    private final WarrantyPlan warranty;

    private ComputerBuild(Builder builder) {
        this.buildName = builder.buildName;
        this.cpuModel = builder.cpuModel;
        this.ramGB = builder.ramGB;
        this.storageGB = builder.storageGB;
        this.storageType = builder.storageType;
        this.gpuTier = builder.gpuTier;
        this.psuWattage = builder.psuWattage;
        this.coolingType = builder.coolingType;
        this.rgbEnabled = builder.rgbEnabled;
        this.operatingSystem = builder.operatingSystem;
        this.warranty = builder.warranty;
    }

    public String getBuildName() {
        return buildName;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public int getRamGB() {
        return ramGB;
    }

    public int getStorageGB() {
        return storageGB;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public GpuTier getGpuTier() {
        return gpuTier;
    }

    public int getPsuWattage() {
        return psuWattage;
    }

    public CoolingType getCoolingType() {
        return coolingType;
    }

    public boolean isRgbEnabled() {
        return rgbEnabled;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public WarrantyPlan getWarranty() {
        return warranty;
    }

    @Override
    public String toString() {
        return "ComputerBuild{" +
                "buildName='" + buildName + '\'' +
                ", cpuModel='" + cpuModel + '\'' +
                ", ramGB=" + ramGB +
                ", storageGB=" + storageGB +
                ", storageType=" + storageType +
                ", gpuTier=" + gpuTier +
                ", psuWattage=" + psuWattage +
                ", coolingType=" + coolingType +
                ", rgbEnabled=" + rgbEnabled +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", warranty=" + warranty +
                '}';
    }

    /**
     * Fluent builder for {@link ComputerBuild}.
     *
     * The four required properties (name, CPU, RAM, storage size) have to be
     * supplied through the constructor, because a build without them does not
     * mean anything. Everything else has a sensible default and can be
     * overridden with a chained, domain-named call instead of a generic
     * setter - e.g. {@code .liquidCooled()} instead of {@code .setCooling(true)}.
     */
    public static class Builder {

        // required
        private final String buildName;
        private final String cpuModel;
        private final int ramGB;
        private final int storageGB;

        // optional, with defaults
        private StorageType storageType = StorageType.SSD;
        private GpuTier gpuTier = GpuTier.NONE;
        private int psuWattage = 450;
        private CoolingType coolingType = CoolingType.AIR;
        private boolean rgbEnabled = false;
        private String operatingSystem = "No OS";
        private WarrantyPlan warranty = WarrantyPlan.standard();

        public Builder(String buildName, String cpuModel, int ramGB, int storageGB) {
            this.buildName = buildName;
            this.cpuModel = cpuModel;
            this.ramGB = ramGB;
            this.storageGB = storageGB;
        }

        public Builder storedOn(StorageType storageType) {
            this.storageType = storageType;
            return this;
        }

        public Builder equippedWithGpu(GpuTier gpuTier) {
            this.gpuTier = gpuTier;
            return this;
        }

        public Builder poweredBy(int psuWattage) {
            this.psuWattage = psuWattage;
            return this;
        }

        public Builder airCooled() {
            this.coolingType = CoolingType.AIR;
            return this;
        }

        public Builder liquidCooled() {
            this.coolingType = CoolingType.LIQUID;
            return this;
        }

        public Builder withRgbLighting() {
            this.rgbEnabled = true;
            return this;
        }

        public Builder runningOS(String operatingSystem) {
            this.operatingSystem = operatingSystem;
            return this;
        }

        public Builder coveredBy(WarrantyPlan warranty) {
            this.warranty = warranty;
            return this;
        }

        /**
         * Validates the current configuration and if it is consistent,
         * returns a new immutable ComputerBuild. The builder itself keeps its
         * state afterwards so it can be reused to produce another,
         * independent build -see ComputerBuildTest#reusingBuilderDoesNotAffectPreviouslyBuiltProduct
         *
         * @throws ComputerBuildValidationException if any rule in ComputerBuildValidator fails
         */
        public ComputerBuild build() {
            ComputerBuildValidator.validate(this);
            return new ComputerBuild(this);
        }

        // package-private accessors, used only by ComputerBuildValidator so it
        // can check the current state before the product is created.
        String buildName() {
            return buildName;
        }

        int ramGB() {
            return ramGB;
        }

        int storageGB() {
            return storageGB;
        }

        GpuTier gpuTier() {
            return gpuTier;
        }

        int psuWattage() {
            return psuWattage;
        }

        CoolingType coolingType() {
            return coolingType;
        }

        WarrantyPlan warranty() {
            return warranty;
        }
    }
}