# Computer Configuration System (Builder Pattern)

This project is a Java implementation of the **Builder Pattern** for constructing PC builds. It demonstrates how to transition from legacy, complex constructors to a safe, readable fluent API.

## Features

* **Fluent Builder API:** Enables step-by-step assembly with domain-oriented method calls
* **Domain Validation:** Single-field and cross-field rules prevent creation of invalid products
* **Presets via Director:** `ComputerBuildDirector` provides configurations for Office, Gaming, and Workstation setups
* **Immutability:** `ComputerBuild` and `WarrantyPlan` are immutable once constructed
* **Test Suite:** Custom automated testing suite validating 10 distinct edge cases

## Project Structure

```text
assignment-1-builder/
├── src/
│   └── com/
│       └── pcbuilder/
│           ├── app/
│           │   └── Main.java
│           ├── build/
│           │   └── ComputerBuildDirector.java
│           ├── domain/
│           │   ├── ComputerBuild.java
│           │   ├── ComputerBuildValidator.java
│           │   ├── CoolingType.java
│           │   ├── GpuTier.java
│           │   ├── StorageType.java
│           │   └── WarrantyPlan.java
│           ├── legacy/
│           │   └── ComputerBuildLegacy.java
│           └── validation/
│               └── ComputerBuildValidationException.java
├── test/
│   └── VerifyEdgeCases.java
├── docs/
│   └── builder-uml.png
├── report.md
└── README.md
```

## How to Compile and RunRequirementsJava Development Kit (JDK 17 or higher):   
1. Build the ProjectCompile all Java source files into an out output folder:
```
javac -d out -sourcepath src src/com/pcbuilder/*/*.java test/VerifyEdgeCases.java
```
2. Run the Main Application
Executes director presets, custom fluent builder code, and error catching demonstrations:
```
java -cp out com.pcbuilder.app.Main
```

3. Run Automated Tests

Executes the 10 automated test scenarios:
```
java -cp out VerifyEdgeCases
```