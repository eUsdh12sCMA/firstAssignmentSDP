package com.pcbuilder.build;

import com.pcbuilder.domain.ComputerBuild;
import com.pcbuilder.domain.GpuTier;
import com.pcbuilder.domain.StorageType;
import com.pcbuilder.domain.WarrantyPlan;

/**
 * Knows the three "recipes" our clients ask for most often. A Director is not
 * strictly required to use Builder, but without it the three presets below
 * would just be copy-pasted builder chains scattered across the client code -
 * see report.md, Part F, for why we decided the duplication was worth
 * removing with a Director instead of leaving it to the caller.
 */
public final class ComputerBuildDirector {

    private ComputerBuildDirector() {
    }

    public static ComputerBuild budgetOffice(String buildName, String cpuModel) {
        return new ComputerBuild.Builder(buildName, cpuModel, 8, 256)
                .storedOn(StorageType.SSD)
                .equippedWithGpu(GpuTier.NONE)
                .poweredBy(400)
                .airCooled()
                .runningOS("Linux Mint")
                .coveredBy(WarrantyPlan.standard())
                .build();
    }

    public static ComputerBuild gamingHighEnd(String buildName, String cpuModel) {
        return new ComputerBuild.Builder(buildName, cpuModel, 32, 2000)
                .storedOn(StorageType.NVME)
                .equippedWithGpu(GpuTier.HIGH_END)
                .poweredBy(850)
                .liquidCooled()
                .withRgbLighting()
                .runningOS("Windows 11")
                .coveredBy(WarrantyPlan.extendedWithAccidentalDamage(3))
                .build();
    }

    public static ComputerBuild workstation(String buildName, String cpuModel) {
        return new ComputerBuild.Builder(buildName, cpuModel, 64, 4000)
                .storedOn(StorageType.NVME)
                .equippedWithGpu(GpuTier.MID_RANGE)
                .poweredBy(650)
                .airCooled()
                .runningOS("Ubuntu 24.04 LTS")
                .coveredBy(WarrantyPlan.extended(3))
                .build();
    }
}