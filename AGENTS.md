# OSGi Repackaged Bundles - Project Documentation

## Project Overview

**Repository:** BlackBeltTechnology/osgi-repackaged-bundles
**License:** Apache License 2.0
**Java Version:** JDK 21
**Build System:** Maven 3.9.4 (via Maven Wrapper)

1. This repository repackages third-party Java libraries as OSGi-compliant bundles so they can be deployed into OSGi containers (Apache Karaf) within the JudoNG ecosystem.
2. Each module wraps one or more upstream JARs using the Apache Felix `maven-bundle-plugin`, controlling exported/imported packages and embedding transitive dependencies.
3. Bundles are published to both an internal Nexus repository (judo.technology) and Maven Central.
4. There is no application source code — every module is a pure repackaging project consisting only of a `pom.xml`.

## Directory Structure

```
osgi-repackaged-bundles/
├── pom.xml                          # Root reactor POM listing all 44 modules
├── .mvn/                            # Maven Wrapper config, extensions, JVM settings
├── .github/workflows/               # CI/CD: release workflow, JIRA-to-PR integration
├── openspec/                        # OpenSpec specification-driven development
├── <vendor>.<library>-<version>/    # Individual bundle modules (44 total)
│   └── pom.xml                      #   Bundle packaging config
└── org.restlet-2.3.12/              # Example of a multi-module bundle (reactor + sub-modules)
    ├── pom.xml                      #   Reactor POM
    ├── org.restlet.parent/          #   Shared parent config
    ├── org.restlet/                 #   Core bundle
    ├── org.restlet.ext.osgi/        #   OSGi extension bundle
    └── org.restlet.ext.servlet/     #   Servlet extension bundle
```

## Core Modules

Modules are grouped by vendor/domain. Each wraps a third-party library as an OSGi bundle.

### Google Libraries

| Module | Version | Purpose |
|--------|---------|---------|
| `com.google.api-client-1.23.0` | 1.23.0 | Google API Client Library |
| `com.google.api-services-calendar-1.23.0` | 1.23.0 | Google Calendar API |
| `com.google.api-services-oauth2-1.23.0` | 1.23.0 | Google OAuth2 API |
| `com.google.guava-20.0` | 20.0 | Guava utilities (legacy version) |
| `com.google.guava-28.0` | 28.0 | Guava utilities (newer version) |

### Apache Libraries

| Module | Version | Purpose |
|--------|---------|---------|
| `org.apache.poi-3.13.1` through `5.2.3` | Multiple | Office document formats (Excel, Word, PowerPoint) |
| `org.apache.lucene-7.0.1` | 7.0.1 | Full-text search engine |
| `org.apache.solr-7.0.1` | 7.0.1 | Search platform built on Lucene |
| `org.apache.directory.api-1.0.0-RC2` | 1.0.0-RC2 | LDAP directory API |

### Eclipse / Modeling

| Module | Version | Purpose |
|--------|---------|---------|
| `org.eclipse.xtext-2.29.0` | 2.29.0 | Xtext language framework |
| `org.eclipse.xbase-2.29.0` | 2.29.0 | Xbase expression language |
| `org.eclipse.epsilon-2.4.0` | 2.4.0 | Epsilon model management |
| `org.eclipse.uml2-5.0.0` | 5.0.0 | UML2 modeling framework |
| `org.eclipse.jdt.ecj-3.21.0` | 3.21.0 | Eclipse Java Compiler |

### Document Processing

| Module | Version | Purpose |
|--------|---------|---------|
| `org.docx4j-2.1.5` | 2.1.5 | DOCX document manipulation |
| `fr.opensagres.xdocreport-1.0.6` | 1.0.6 | Template-based document generation |
| `com.lowagie.itext-2.1.7` | 2.1.7 | PDF generation |
| `org.jxls-2.6.0` / `2.10.0` / `3.0.0` | Multiple | Excel template engine |
| `org.odftoolkit.odfdom-java-0.8.7` | 0.8.7 | ODF document processing |

### Data & Utilities

| Module | Version | Purpose |
|--------|---------|---------|
| `io.vavr-0.10.4` | 0.10.4 | Functional programming for Java |
| `io.github.resilience4j-1.7.1` / `2.0.2` | Multiple | Fault tolerance library |
| `ma.glasnost.orika-1.5.4` | 1.5.4 | Java bean mapping |
| `com.tdunning.t-digest-3.2` | 3.2 | Quantile estimation |
| `org.functionaljava-4.8.1` | 4.8.1 | Functional programming primitives |
| `com.vdurmont.semver4j-3.1.0` | 3.1.0 | Semantic versioning |
| `com.pivovarit.throwing-function-1.5.1` | 1.5.1 | Checked-exception functional interfaces |

### API & Networking

| Module | Version | Purpose |
|--------|---------|---------|
| `io.swagger.swagger-parser-1.0.47` | 1.0.47 | Swagger/OpenAPI 2.0 parser |
| `io.swagger.v3.swagger-parser-2.0.19` | 2.0.19 | OpenAPI 3.0 parser |
| `org.openapitools.openapi-generator-4.3.1` | 4.3.1 | OpenAPI code generator |
| `org.restlet-2.3.12` | 2.3.12 | RESTful web framework (multi-module) |
| `io.moquette-0.11` | 0.11 | MQTT broker |
| `org.subethamail.subethasmtp-3.1.7` | 3.1.7 | Embeddable SMTP server |

### Other

| Module | Version | Purpose |
|--------|---------|---------|
| `com.diogonunes.jcolor-5.2.0` | 5.2.0 | ANSI terminal colors |
| `com.github.albfernandez.juniversalchardet-2.3.0` | 2.3.0 | Character encoding detection |
| `net.sf.jsignature.io-tools.easystream-1.2.15` | 1.2.15 | Stream utilities |
| `org.tinyjee.jgraphx.mxgraph-3.4.1.3` | 3.4.1.3 | Graph visualization |

## Technology Stack

### Core Technologies
- **Apache Felix maven-bundle-plugin 5.1.8** — generates OSGi bundle manifests and embeds dependencies
- **OSGi R7** — target runtime framework specification
- **Apache Karaf** — target OSGi container in the JudoNG ecosystem

### Build & Quality
- **Maven 3.9.4** via Maven Wrapper (`./mvnw`)
- **JDK 21** (Zulu distribution in CI)
- **Maven extensions:** wagon-file 3.5.2, wagon-webdav-jackrabbit 3.5.2, maven-buildtime-extension 3.0.3, profile-activator-extension 1.3
- No unit tests — bundle packaging correctness is verified by successful build

## Build Commands

```bash
# Build all modules
./mvnw clean install

# Build a single module
./mvnw -f io.vavr-0.10.4/pom.xml clean install

# Release a module to Judo NG Nexus (requires credentials)
./mvnw -Drevision=<version> -Psign-artifacts -Prelease-judong \
  -f <module>/pom.xml clean deploy

# Release a module to Maven Central (requires credentials + GPG)
./mvnw -Drevision=<version> -Psign-artifacts -Prelease-central \
  -f <module>/pom.xml clean deploy
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `sign-artifacts` | GPG-signs JARs using sign-maven-plugin 1.0.1 |
| `release-judong` | Deploys to Judo NG Nexus at `nexus.judo.technology` |
| `release-central` | Deploys to Maven Central via Sonatype OSSRH (nexus-staging-maven-plugin 1.7.0) |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Root reactor POM — lists all 44 modules |
| `<module>/pom.xml` | Bundle definition — dependencies, Export/Import-Package, Embed-Dependency |
| `.mvn/jvm.config` | JVM memory settings: `-Xms1024m -Xmx2048m` |
| `.mvn/extensions.xml` | Maven extensions for wagon protocols and build timing |
| `.mvn/wrapper/maven-wrapper.properties` | Maven Wrapper version pin (3.9.4) |
| `.github/workflows/build-release.yml` | Manual release workflow (dispatched per-module with version input) |
| `.github/workflows/jira-description-to-pr.yml` | Auto-populates PR body from JIRA ticket description |

## Development Environment

**Required:**
- Java 21 JDK
- Maven 3.9.4+ (or use included `./mvnw` wrapper)

**No additional tooling needed** — modules are pure POM-based repackaging projects with no source code.

## Git Workflow

- **Main Branch:** `develop`
- **Versioning:** `${revision}` property, default `1.0.0-SNAPSHOT`; individual bundles use `<upstream-version>_<increment>` (e.g., `0.10.4_0`)
- **PR Titles:** Must include JIRA key (`JNG-*`)
- **Releases:** Triggered manually via GitHub Actions workflow dispatch, specifying the module directory and version

## Important Notes

1. **No source code exists in any module** — each module contains only a `pom.xml` that configures how upstream JARs are repackaged as OSGi bundles.
2. **Bundle version suffix convention:** Append `_<increment>` to the upstream version (e.g., `5.2.3_1`). Bump the increment when changing bundle packaging without an upstream version change.
3. **Multi-version modules are intentional** — some libraries (POI, Guava, Resilience4j, JXLS) ship multiple versions to support different downstream consumers.
4. **Import-Package optionality matters** — mark imports as `resolution:=optional` only for truly optional runtime dependencies. Incorrect optionality breaks OSGi resolution.
5. **Embed-Dependency inline modes:** Use `inline=true` to merge classes into the bundle JAR (smaller bundles), or `inline=false` to embed dependency JARs as nested archives (preserves JAR boundaries).
6. **Dual deployment:** Every release goes to both Judo NG Nexus (internal) and Maven Central (public), in that order.
7. **The `org.restlet-2.3.12` module is unique** — it is the only multi-module (reactor) bundle, containing a parent POM and three sub-bundles (core, osgi extension, servlet extension).

## Related Documentation

- [README.md](README.md) — Project title
