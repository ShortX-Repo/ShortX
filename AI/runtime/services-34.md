# services-34

## Role

`services-34` contains Android 14 specific compatibility bindings. It adds more specialized adapters for input and SystemUI-related behavior.

## Module Type

- Kotlin/JVM compatibility module

## Key Build Facts

- Compiles against `android-sdk/34/android-34.jar`.
- Expands the version layer with input manager and status bar related helpers.

## Key Source Areas

- `services-34/src/main/java/tornaco/apps/shortx/services34/pm/`
- `services-34/src/main/java/tornaco/apps/shortx/services34/app/`
- `services-34/src/main/java/tornaco/apps/shortx/services34/input/`
- `services-34/src/main/java/tornaco/apps/shortx/services34/systemui/`
- `services-34/src/main/java/tornaco/apps/shortx/services34/wm/`

## Important Entry Points

- `PMV34.kt`: package manager compatibility wrapper.
- `UidObserver.kt`: UID/app observation.
- `InputConnectionV34.kt`: newer input connection support.
- `InputManagerGlobalV34.kt`: newer input manager global support.
- `StatusBarIconCreator.kt`: SystemUI icon compatibility.

## How It Connects

The base `services` module uses this module when Android 14 and nearby framework APIs require dedicated wrappers.

## Reading Outcome

This is a good representative modern version module because it shows how the simple adapter pattern evolves into broader framework integration.
