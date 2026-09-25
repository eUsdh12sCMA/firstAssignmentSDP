package com.pcbuilder.app;

import com.pcbuilder.build.ComputerBuildDirector;
import com.pcbuilder.domain.ComputerBuild;
import com.pcbuilder.domain.GpuTier;
import com.pcbuilder.domain.StorageType;
import com.pcbuilder.domain.WarrantyPlan;
import com.pcbuilder.validation.ComputerBuildValidationException;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Presets built through the Director ===");

        ComputerBuild office = ComputerBuildDirector.budgetOffice("Reception PC", "Intel i3-13100");
        System.out.println(office);

        ComputerBuild gaming = ComputerBuildDirector.gamingHighEnd("Streaming Rig", "AMD Ryzen 7 7800X3D");
        System.out.println(gaming);

        ComputerBuild workstation = ComputerBuildDirector.workstation("Render Node 1", "AMD Threadripper 7960X");
        System.out.println(workstation);

        System.out.println();
        System.out.println("=== A one-off build, assembled manually with the fluent API ===");

        ComputerBuild custom = new ComputerBuild.Builder("Home Server", "Intel N100", 16, 4000)
                .storedOn(StorageType.HDD)
                .equippedWithGpu(GpuTier.NONE)
                .poweredBy(350)
                .airCooled()
                .runningOS("Debian 12")
                .coveredBy(WarrantyPlan.extended(2))
                .build();
        System.out.println(custom);

        System.out.println();
        System.out.println("=== Rejecting an invalid configuration ===");

        try {
            new ComputerBuild.Builder("Broken Rig", "AMD Ryzen 5", 16, 1000)
                    .equippedWithGpu(GpuTier.HIGH_END)
                    .poweredBy(500) // too low for a HIGH_END gpu
                    .airCooled()    // also wrong for a HIGH_END gpu
                    .build();
        } catch (ComputerBuildValidationException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Reusing one Builder for two independent Products ===");

        ComputerBuild.Builder reusable = new ComputerBuild.Builder("Base Model", "Intel i5-13400", 16, 512)
                .storedOn(StorageType.SSD)
                .poweredBy(500);

        ComputerBuild first = reusable.build();
        reusable.equippedWithGpu(GpuTier.MID_RANGE).liquidCooled();
        ComputerBuild second = reusable.build();

        System.out.println("first:  " + first);
        System.out.println("second: " + second);
        System.out.println("first still has gpuTier=" + first.getGpuTier()
                + " and coolingType=" + first.getCoolingType() + " - it was not touched by the second build()");
    }
}