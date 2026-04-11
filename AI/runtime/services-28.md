# services-28

## Role

`services-28` contains Android 9 specific compatibility bindings used by the main `services` module.

## Module Type

- Kotlin/JVM compatibility module

## Key Build Facts

- Compiles against `android-sdk/28/android-28.jar`.
- Keeps dependencies minimal: `core`, `core-api`, and utility support.

## Key Source Areas

- `services-28/src/main/java/tornaco/apps/shortx/services28/pm/`
- `services-28/src/main/java/tornaco/apps/shortx/services28/app/`

## Important Entry Points

- `PMV28.kt`: package manager compatibility wrapper.
- `UidObserver.kt`: UID/app observation for API 28 behavior.

## How It Connects

Loaded by the main `services` layer when running on Android 28 behavior boundaries.

## Reading Outcome

This module is small and best read as a version adapter, not as core business logic.
