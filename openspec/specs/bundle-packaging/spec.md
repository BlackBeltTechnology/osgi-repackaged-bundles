# Bundle Packaging Specification

## Purpose
Defines how third-party Java libraries are repackaged as OSGi-compliant bundles using the Apache Felix maven-bundle-plugin, enabling deployment into OSGi containers like Apache Karaf.

## Architecture
Each bundle module consists of a single `pom.xml` with `<packaging>bundle</packaging>`. The maven-bundle-plugin (v5.1.8) generates the OSGi manifest by processing `Export-Package`, `Import-Package`, and `Embed-Dependency` instructions. Dependencies are pulled from Maven Central and embedded into the resulting bundle JAR. GroupIds follow the pattern `hu.blackbelt.bundles.<vendor>.<library>`.

## Requirements

### Requirement: Bundle directory naming convention
Each bundle module directory SHALL be named `<vendor>.<library>-<version>` matching the upstream library coordinates.

#### Scenario: New bundle creation
- **GIVEN** a third-party library `io.vavr:vavr` at version `0.10.4`
- **WHEN** a new bundle module is created
- **THEN** the directory is named `io.vavr-0.10.4`

### Requirement: Bundle version suffix
Bundle artifact versions SHALL use the format `<upstream-version>_<increment>` where increment starts at `0` and is bumped for packaging-only changes.

#### Scenario: Initial bundle version
- **WHEN** a new bundle is created for upstream version `0.10.4`
- **THEN** the bundle version is `0.10.4_0`

#### Scenario: Packaging change without upstream update
- **GIVEN** bundle `io.vavr` at version `0.10.4_0`
- **WHEN** the Import-Package or Export-Package instructions are modified without changing the upstream library version
- **THEN** the bundle version becomes `0.10.4_1`

### Requirement: Dependency embedding
All compile-scoped dependencies SHALL be embedded into the bundle JAR via the `Embed-Dependency` instruction.

#### Scenario: Compile dependency embedding
- **GIVEN** a bundle with compile-scoped dependency `io.vavr:vavr:0.10.4`
- **WHEN** the bundle is built
- **THEN** the vavr classes are included in the output bundle JAR

### Requirement: Package export versioning
Exported packages SHALL include version metadata matching the upstream library version.

#### Scenario: Export-Package version
- **GIVEN** a bundle wrapping vavr 0.10.4
- **WHEN** Export-Package is configured
- **THEN** it specifies `io.vavr*;version="0.10.4"`

### Requirement: Optional import resolution
Import-Package entries for dependencies that are not required at runtime SHALL be marked with `resolution:=optional`.

#### Scenario: Optional dependency import
- **GIVEN** a bundle that can function without `javax.annotation.meta`
- **WHEN** Import-Package is configured
- **THEN** the entry reads `javax.annotation.meta;resolution:=optional`

### Requirement: Root reactor registration
Every new bundle module SHALL be registered as a `<module>` entry in the root `pom.xml`.

#### Scenario: Module registration
- **GIVEN** a new bundle directory `io.vavr-0.10.4`
- **WHEN** the bundle is added to the project
- **THEN** `<module>io.vavr-0.10.4</module>` is present in the root `pom.xml` `<modules>` section
