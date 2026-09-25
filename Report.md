# Assignment 1  Builder Pattern Report

**Course:** Software Design Patterns  
**Domain:** Computer Configuration (PC Builder)  
**Language:** Java (JDK 17+)
**Name & Group:** Sanzhar Zhunis, SE-2519


---

## 1. Problem Description & Individual Variant

In custom PC building, a computer configuration consists of many dependent components such as CPU, RAM, GPU, power supply (PSU), cooling, and storage.

Domain: Computer Configuration.

Product Properties: 11 total properties (4 required: `buildName`, `cpuModel`, `ramGB`, `storageGB`; 7 optional with sensible defaults; 3 data types: String, int, boolean; 1 immutable value object: `WarrantyPlan`).

Individual Constraint: A build configured with a `HIGH_END` GPU draws heavy power and heat. Therefore, it strictly requires a power supply of at least 650W (`psuWattage >= 650`), liquid cooling (`coolingType == LIQUID`), and at least 8GB of RAM (`ramGB >= 8`) to avoid system bottlenecking.

Required Presets: Budget Office, Gaming High-End, and Workstation.

---

## 2. Part A - Constructor-Based Solution & Design Problems

Before introducing the Builder pattern, I created an initial implementation in `com.pcbuilder.legacy.ComputerBuildLegacy` using traditional constructors.

```java
// Legacy constructor approach with multiple telescoping constructors
public ComputerBuildLegacy(String buildName, String cpuModel, int ramGB, int storageGB) {
    this(buildName, cpuModel, ramGB, storageGB, "SSD", "NONE", 450, "AIR", false, "No OS", 1);
}

public ComputerBuildLegacy(String buildName, String cpuModel, int ramGB, int storageGB,
                            String storageType, String gpuTier, int psuWattage,
                            String coolingType, boolean rgbEnabled, String operatingSystem,
                            int warrantyYears) { ... }
```
## Identified Design Problems

Telescoping Constructor Anti-Pattern: To support optional fields I had to create multiple overloaded constructors. Adding new optional features in the future forces us to write even more constructor signatures, making class maintenance very hard.   


Type-Blindness and Confusing Parameters: In client code, calling the 11-argument constructor looks like this: new ComputerBuildLegacy("Gaming Rig", "Ryzen 7", 32, 2000, "NVME", "HIGH_END", 850, "LIQUID", true, "Windows 11", 3). Looking at true or 3 without checking the constructor source code makes it hard to know what parameter position means what.


Lack of Invariant Protection & Invalid States: The legacy constructor simply assigns parameters directly to fields. It allows a caller to pass a HIGH_END GPU with a 300W power supply and air cooling. The object gets instantiated in an invalid, dangerous state without throwing any errors.

## 3. Part B & C - Builder Pattern & Validation Architecture
To fix these problems, I refactored the design to use the Builder Pattern:   

Product (ComputerBuild): Made fully immutable with private constructor and getter methods only.   


Builder (ComputerBuild.Builder): Static nested class inside ComputerBuild. Mandatory fields are required in the Builder constructor, while optional fields have default values and fluent setter methods.   


Validator (ComputerBuildValidator): Encapsulates all domain checks into a package-private class.   


Value Object (WarrantyPlan): Immutable value object representing warranty details.

## Enforced Validation Rules
Single-Field Rules:
buildName: Must not be null or blank.   

ramGB: Must be at least 4 GB.   

storageGB: Must be strictly greater than 0.   

psuWattage: Must be at least 300 W.

## Cross-Field Rules (Individual Constraints):
GPU Power & Cooling Requirement: If gpuTier == HIGH_END, then psuWattage must be >= 650 AND coolingType must be LIQUID.   

GPU Memory Bottleneck Rule: If gpuTier == HIGH_END, then ramGB must be >= 8.   

If any validation rule fails when .build() is executed, ComputerBuildValidator throws a ComputerBuildValidationException with a clear message explaining what went wrong.

## 4. Part D - Director & Preset Configurations
The ComputerBuildDirector encapsulates common, reusable assembly recipes so client code does not duplicate builder chains:   
budgetOffice: Intel i3, 8GB RAM, 256GB SSD, Integrated GPU, 400W PSU, Air Cooling, Linux Mint, Standard 1-Year Warranty.   

gamingHighEnd: Ryzen 7, 32GB RAM, 2TB NVMe SSD, HIGH_END GPU, 850W PSU, Liquid Cooling, RGB enabled, Windows 11, 3-Year Extended Warranty with Accidental Damage.   

workstation: Threadripper CPU, 64GB RAM, 4TB NVMe SSD, MID_RANGE GPU, 650W PSU, Air Cooling, Ubuntu 24.04 LTS, 3-Year Extended Warranty.

## 5. Part E - Clean Code: Before and After
   Fragment 1: Flag Arguments & Arbitrary Strings to Domain-Oriented Fluent API
```
// Legacy code passing arbitrary strings and flags
ComputerBuildLegacy pc = new ComputerBuildLegacy(
    "Gaming", "Ryzen 7", 32, 2000, "NVME", "HIGH_END", 850, "LIQUID", true, "Windows 11", 3
);
```
```
// Refactored fluent builder API
ComputerBuild pc = new ComputerBuild.Builder("Gaming Rig", "Ryzen 7", 32, 2000)
    .storedOn(StorageType.NVME)
    .equippedWithGpu(GpuTier.HIGH_END)
    .liquidCooled()
    .withRgbLighting()
    .build();
```
1. Primitive obsession, magic strings, and boolean flags (true) made code unreadable and prone to typos like "Liquid" vs "LIQUID".   
2. Avoiding flag arguments and using descriptive domain-oriented method names.   
3. Methods like .liquidCooled() read like English and remove argument ordering ambiguity


Fragment 2: Monolithic Validation to Single Responsibility Principle
```
// Monolithic build method doing instantiation AND validation
public ComputerBuild build() {
    if (this.ramGB < 4) throw new RuntimeException("Low RAM");
    if (this.gpuTier == GpuTier.HIGH_END && this.psuWattage < 650) throw new RuntimeException("Low PSU");
    return new ComputerBuild(this);
}
```
```
// Delegating validation to dedicated validator class
public ComputerBuild build() {
    ComputerBuildValidator.validate(this);
    return new ComputerBuild(this);
}
```
1. The build() method had two responsibilities: managing build logic and validating domain rules.
2. Single Responsibility Principle (SRP).   
3. Builder only handles object assembly, while ComputerBuildValidator handles domain consistency rules.

Fragment 3: Complex Nested Conditional to Small Functions
```
// Single massive if-statement block for validation
static void validate(Builder b) {
    if (b.buildName == null || b.buildName.isBlank()) throw new ComputerBuildValidationException("Invalid name");
    if (b.gpuTier == GpuTier.HIGH_END && (b.psuWattage < 650 || b.coolingType != CoolingType.LIQUID)) {
        throw new ComputerBuildValidationException("Invalid high-end GPU config");
    }
}
```
```
// Extracted into small, single-purpose validation checks
static void validate(ComputerBuild.Builder builder) {
    requireBuildName(builder);
    requireHighEndGpuHasEnoughPower(builder);
}

private static void requireHighEndGpuHasEnoughPower(ComputerBuild.Builder builder) {
    if (builder.gpuTier() != GpuTier.HIGH_END) return;
    if (builder.psuWattage() < HIGH_END_GPU_MIN_PSU_WATTAGE) {
        throw new ComputerBuildValidationException("HIGH_END gpuTier requires psuWattage >= 650W");
    }
    if (builder.coolingType() != CoolingType.LIQUID) {
        throw new ComputerBuildValidationException("HIGH_END gpuTier requires liquidCooled()");
    }
}
```
1. Long compound conditional statements made it hard to isolate which condition failed. 2. 
2. Small functions and single level of abstraction.   
3. Each private rule method checks one constraint and yields clear error messages. 

## 6. Part F - Design Decision & Rejected AlternativeDecision: 
Separating ComputerBuildValidator as a package-private class instead of embedding rules inside Builder.build() or ComputerBuild constructor.   

Alternative: Putting validation if statements directly inside ComputerBuild.Builder.build().   

Reasoning: As validation rules grow, putting them in build() pollutes the Builder class. Making ComputerBuildValidator package-private ensures validation logic stays isolated, maintainable, and inaccessible to outside callers, while keeping the Builder clean.

## 7. Part G - UML Class Diagram & Traceability Table
docs/uml-diagram.png

## 8. Part H - Automated Testing Description
Automated testing is implemented in VerifyEdgeCases.java. It runs 10 test scenarios covering valid, invalid, boundary, and builder reuse states:   
1. Valid Scenario 1: Standard budget office build succeeds.   
2. Valid Scenario 2: Custom valid build with high-end GPU, 650W PSU, liquid cooling, and 16GB RAM succeeds.   
3. Valid Scenario 3: Minimal valid RAM (4GB) and minimal PSU (300W) succeeds.   
4. Invalid Scenario 1: Blank build name throws ComputerBuildValidationException.   
5. Invalid Scenario 2: Storage size <= 0 throws ComputerBuildValidationException.   
6. Invalid Scenario 3: RAM < 4GB throws ComputerBuildValidationException. 
7. Boundary Case 1: RAM = 3GB (one below minimum of 4GB) fails.
8. Boundary Case 2: PSU = 299W (one below minimum of 300W) fails.
9. Individual Constraint Test: HIGH_END GPU paired with 649W PSU or AIR cooling fails as expected
10. Builder Reuse / Product Independence: Reusing one Builder instance to generate two successive products demonstrates that modifying the builder after building product 1 does not mutate product 1.

## 9. Git Development History
1. feat: initial domain model and enums
2. refactor: add legacy constructor implementation to demonstrate design problems
3. feat: implement ComputerBuild and inner Builder with fluent API
4. feat: add ComputerBuildValidator with single-field and cross-field rules
5. feat: add ComputerBuildDirector with office, gaming, and workstation presets
6. test: add automated test harness in VerifyEdgeCases covering 10 scenarios

## 10. Sample Program Output
```
=== Presets built through the Director ===
ComputerBuild{buildName='Reception PC', cpuModel='Intel i3-13100', ramGB=8, storageGB=256, storageType=SSD, gpuTier=NONE, psuWattage=400, coolingType=AIR, rgbEnabled=false, operatingSystem='Linux Mint', warranty=1 year(s)}
ComputerBuild{buildName='Streaming Rig', cpuModel='AMD Ryzen 7 7800X3D', ramGB=32, storageGB=2000, storageType=NVME, gpuTier=HIGH_END, psuWattage=850, coolingType=LIQUID, rgbEnabled=true, operatingSystem='Windows 11', warranty=3 year(s), extended + accidental damage}
ComputerBuild{buildName='Render Node 1', cpuModel='AMD Threadripper 7960X', ramGB=64, storageGB=4000, storageType=NVME, gpuTier=MID_RANGE, psuWattage=650, coolingType=AIR, rgbEnabled=false, operatingSystem='Ubuntu 24.04 LTS', warranty=3 year(s), extended}

=== A one-off build, assembled manually with the fluent API ===
ComputerBuild{buildName='Home Server', cpuModel='Intel N100', ramGB=16, storageGB=4000, storageType=HDD, gpuTier=NONE, psuWattage=350, coolingType=AIR, rgbEnabled=false, operatingSystem='Debian 12', warranty=2 year(s), extended}

=== Rejecting an invalid configuration ===
Rejected as expected: HIGH_END gpuTier requires psuWattage >= 650W, got 500

=== Reusing one Builder for two independent Products ===
first:  ComputerBuild{buildName='Base Model', cpuModel='Intel i5-13400', ramGB=16, storageGB=512, storageType=SSD, gpuTier=NONE, psuWattage=500, coolingType=AIR, rgbEnabled=false, operatingSystem='No OS', warranty=1 year(s)}
second: ComputerBuild{buildName='Base Model', cpuModel='Intel i5-13400', ramGB=16, storageGB=512, storageType=SSD, gpuTier=MID_RANGE, psuWattage=500, coolingType=LIQUID, rgbEnabled=false, operatingSystem='No OS', warranty=1 year(s)}
first still has gpuTier=NONE and coolingType=AIR - it was not touched by the second build()

Process finished with exit code 0
```

