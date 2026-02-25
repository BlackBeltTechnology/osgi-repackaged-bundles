# Release Management Specification

## Purpose
Defines the process for releasing OSGi bundle artifacts to both the internal Judo NG Nexus repository and public Maven Central, including artifact signing and CI/CD automation.

## Architecture
Releases are driven by a GitHub Actions workflow (`build-release.yml`) triggered via manual dispatch. The workflow accepts a module directory and version as inputs, then performs a two-phase deployment: first to Judo NG Nexus (`nexus.judo.technology`), then to Maven Central (via Sonatype OSSRH). Three Maven profiles control this process: `sign-artifacts` (GPG signing via sign-maven-plugin 1.0.1), `release-judong` (Nexus deployment), and `release-central` (Sonatype staging via nexus-staging-maven-plugin 1.7.0).

## Requirements

### Requirement: Dual repository deployment
Every release SHALL be deployed to both the Judo NG Nexus repository and Maven Central, in that order.

#### Scenario: Standard release
- **GIVEN** a bundle `io.vavr` at version `0.10.4_0` is ready for release
- **WHEN** the release workflow is triggered with project `io.vavr-0.10.4` and version `0.10.4_0`
- **THEN** the artifact is first deployed to `nexus.judo.technology/repository/maven-judong/`
- **AND** then deployed to Maven Central via Sonatype OSSRH

### Requirement: Artifact signing
All released artifacts SHALL be signed using GPG via the `sign-artifacts` Maven profile.

#### Scenario: Signed release
- **GIVEN** the `sign-artifacts` profile is activated
- **WHEN** the bundle is built and deployed
- **THEN** GPG signature files (`.asc`) are generated and published alongside the artifacts

### Requirement: Version override via revision property
The release version SHALL be set via the `-Drevision=<version>` Maven property, overriding the default `1.0.0-SNAPSHOT`.

#### Scenario: Release version injection
- **GIVEN** the root POM defines `<revision>1.0.0-SNAPSHOT</revision>`
- **WHEN** the release workflow passes `-Drevision=0.10.4_0`
- **THEN** the built artifact uses version `0.10.4_0`

### Requirement: Per-module release granularity
The release workflow SHALL support deploying a single module without building the entire reactor.

#### Scenario: Single module deployment
- **WHEN** the workflow is dispatched with project `io.vavr-0.10.4`
- **THEN** only the `io.vavr-0.10.4/pom.xml` is built and deployed
- **AND** other modules are not affected

### Requirement: JDK 21 build environment
The CI build environment SHALL use JDK 21 for all release builds.

#### Scenario: CI environment setup
- **WHEN** the release workflow runs
- **THEN** JDK 21 (Zulu distribution) is configured before the Maven build

### Requirement: PR JIRA integration
Pull request titles SHALL contain a JIRA issue key matching the pattern `JNG-*`.

#### Scenario: PR title validation
- **GIVEN** a pull request is opened against a non-release branch
- **WHEN** the PR title is `JNG-6321 Add Google BigQuery API`
- **THEN** the JIRA description is fetched and appended to the PR body
