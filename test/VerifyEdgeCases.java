package com.pcbuilder.legacy;

/**
 * This is the "before Builder" version from Part A of the assignment.
 * It is kept here only so the report can point at real code instead of a
 * screenshot. Nothing else in the project depends on this class see ComputerBuild.Builder
 * for the actual implementation.
 *
 * Problems with this version are discussed in report.md section Part A.
 */
public class ComputerBuildLegacy {

    public final String buildName;
    public final String cpuModel;
    public final int ramGB;
    public final int storageGB;
    public final String storageType;
    public final String gpuTier;
    public final int psuWattage;
    public final String coolingType;
    public final boolean rgbEnabled;
    public final String operatingSystem;
    public final int warrantyYears;

    // constructor #1 - only the "required" fields, everything else gets a
    // hardcoded default that is duplicated in every other constructor below
    public ComputerBuildLegacy(String buildName, String cpuModel, int ramGB, int storageGB) {
        this(buildName, cpuModel, ramGB, storageGB, "SSD", "NONE", 450, "AIR", false, "No OS", 1);
    }

    // constructor #2 - required fields + gpu, because some caller wanted to
    // set the gpu without touching anything else
    public ComputerBuildLegacy(String buildName, String cpuModel, int ramGB, int storageGB, String gpuTier) {
        this(buildName, cpuModel, ramGB, storageGB, "SSD", gpuTier, 450, "AIR", false, "No OS", 1);
    }

    // constructor #3 - required fields + gpu + psu, because a HIGH_END gpu
    // build needed a bigger PSU and nobody wanted to touch the 5-arg version
    public ComputerBuildLegacy(String buildName, String cpuModel, int ramGB, int storageGB,
                               String gpuTier, int psuWattage) {
        this(buildName, cpuModel, ramGB, storageGB, "SSD", gpuTier, psuWattage, "AIR", false, "No OS", 1);
    }

    // constructor #4 - the "give me everything" version. No validation at all:
    // there is nothing here stopping someone from combining a HIGH_END gpu
    // with a 300W PSU and air cooling.
    public ComputerBuildLegacy(String buildName, String cpuModel, int ramGB, int storageGB,
                               String storageType, String gpuTier, int psuWattage,
                               String coolingType, boolean rgbEnabled, String operatingSystem,
                               int warrantyYears) {
        this.buildName = buildName;
        this.cpuModel = cpuModel;
        this.ramGB = ramGB;
        this.storageGB = storageGB;
        this.storageType = storageType;
        this.gpuTier = gpuTier;
        this.psuWattage = psuWattage;
        this.coolingType = coolingType;
        this.rgbEnabled = rgbEnabled;
        this.operatingSystem = operatingSystem;
        this.warrantyYears = warrantyYears;
    }

    // Example call site showing the problem - see report.md Part A.
    // ComputerBuildLegacy gamingRig = new ComputerBuildLegacy(
    //     "Gaming Rig", "Ryzen 7", 32, 2000,
    //     "NVME", "HIGH_END", 850, "LIQUID", true, "Windows 11", 3
    // );
    // What does "true" mean at position 9 without checking the constructor? Which
    // string values are actually legal for gpuTier or coolingType? Nothing here
    // tells you, and the compiler will happily accept "high-end" or "Air" typos.
}