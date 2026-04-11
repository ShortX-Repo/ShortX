# core-api

## Role

`core-api` is the shared contract layer for ShortX. It defines AIDL service interfaces, observers, protobuf-based domain models, and the main manager abstraction used by higher layers.

## Module Type

- Kotlin/JVM library with Java, AIDL, and protobuf inputs

## Key Build Facts

- Generates `BuildProp` constants for URLs, service names, and help links.
- Uses custom AIDL generation from `buildSrc`.
- Compiles protobuf models for actions, rules, facts, settings, and related data.

## Key Source Areas

- `core-api/src/main/java/tornaco/apps/shortx/core/IShortX.aidl`
- `core-api/src/main/java/tornaco/apps/shortx/core/ShortXManager.kt`
- `core-api/src/main/java/tornaco/apps/shortx/core/rule/script/api/ScriptShortXApi.java`
- `core-api/src/main/proto/shortx/`

## Important Entry Points

- `IShortX.aidl`: central IPC contract.
- `ShortXManager.kt`: high-level client API for rules, actions, toggles, settings, and many service operations.
- `ScriptShortXApi.java`: script-facing API exposed into MVEL execution.
- Observer AIDL files: change notifications for rules, packages, globals, and toggles.

## How It Connects

Every major runtime or UI-facing module depends on this layer directly or indirectly. It is the vocabulary of the system: rule models, action models, and service entry points all start here.

## Reading Outcome

If you only read one shared module first, read this one. It tells you what the system can do before you inspect how it is implemented.
