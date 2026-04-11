# services-33

## Role

`services-33` contains Android 13 specific compatibility bindings. It begins to expand beyond package and UID observation by adding newer input-related integration points.

## Module Type

- Kotlin/JVM compatibility module

## Key Build Facts

- Compiles against `android-sdk/33/android-33.jar`.
- Adds input-related compatibility logic beyond the older minimal pattern.

## Key Source Areas

- `services-33/src/main/java/tornaco/apps/shortx/services33/pm/`
- `services-33/src/main/java/tornaco/apps/shortx/services33/app/`
- `services-33/src/main/java/tornaco/apps/shortx/services33/input/`

## Important Entry Points

- `PMV33.kt`: package manager compatibility wrapper.
- `UidObserver.kt`: UID/app observation.
- `InputContextV33.kt`: Android 13 input compatibility support.

## How It Connects

This module marks the point where versioned services start carrying more specialized framework adapters, especially around input handling.

## Reading Outcome

Compare this module with `services-32` to see where the compatibility surface begins to widen.
