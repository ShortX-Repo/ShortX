# services-35

## Role

`services-35` contains Android 15 specific compatibility bindings and represents the newest framework adapter layer in the repo.

## Module Type

- Kotlin/JVM compatibility module

## Key Build Facts

- Compiles against `android-sdk/35/android-35.jar`.
- Includes package manager, UID, status bar, and window manager related adapters.

## Key Source Areas

- `services-35/src/main/java/tornaco/apps/shortx/services35/pm/`
- `services-35/src/main/java/tornaco/apps/shortx/services35/app/`
- `services-35/src/main/java/tornaco/apps/shortx/services35/systemui/`
- `services-35/src/main/java/tornaco/apps/shortx/services35/wm/`

## Important Entry Points

- `PMV35.kt`: package manager compatibility wrapper.
- `UidObserver.kt`: UID/app observation.
- `StatusBarIconCreator.kt`: status bar icon support for API 35.
- `WindowManagerV35.kt`: window manager compatibility support.

## How It Connects

This module is the newest version-specific endpoint currently consumed by the base `services` layer.

## Reading Outcome

Use this module as the newest comparison target when diagnosing Android-version-specific runtime bugs.
