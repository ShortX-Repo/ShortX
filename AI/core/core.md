# core

## Role

`core` is the shared implementation-oriented layer that sits behind `core-api`. It carries internal build metadata, extension logic, and non-UI behavior that supports the larger runtime.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Depends on `core-api` and reuses its contracts and protobuf models.
- Generates `BuildPropInternal` with version values, app identifiers, signatures, download URLs, and internal constants.
- Compiles against Android framework stubs but is not itself an Android app module.

## Key Source Areas

- `core/build.gradle.kts`
- `core/src/main/kotlin/tornaco/apps/shortx/core/CustomContextMenuExt.kt`
- `core/src/main/kotlin/tornaco/apps/shortx/core/LogExt.kt`

## Important Entry Points

- `CustomContextMenuExt.kt`: maps context menu models to concrete activity targets.
- `LogExt.kt`: core logging helpers.
- Generated build config values: internal version and endpoint metadata.

## How It Connects

`core-api` defines the contracts. `core` packages internal logic and constants used by app, services, and patch layers. It is the shared implementation substrate rather than the public contract surface.

## Reading Outcome

Read this after `core-api`. The key idea is that `core-api` describes the system boundary, while `core` carries shared implementation support and internal metadata.
