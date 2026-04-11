# services-32

## Role

`services-32` contains Android 12L specific compatibility bindings used by the main `services` module.

## Module Type

- Kotlin/JVM compatibility module

## Key Build Facts

- Compiles against `android-sdk/32/android-32.jar`.
- Still follows the compact compatibility-adapter structure of earlier versions.

## Key Source Areas

- `services-32/src/main/java/tornaco/apps/shortx/services32/pm/`
- `services-32/src/main/java/tornaco/apps/shortx/services32/app/`

## Important Entry Points

- `PMV32.kt`: package manager compatibility wrapper.
- `UidObserver.kt`: UID/app observation for API 32 behavior.

## How It Connects

This module exists so the runtime can isolate API 32 framework differences without polluting the main `services` logic.

## Reading Outcome

Read this only if the affected behavior is version-specific on 12L devices.
