# services-31

## Role

`services-31` contains Android 12 specific compatibility bindings used by the main `services` module.

## Module Type

- Kotlin/JVM compatibility module

## Key Build Facts

- Compiles against `android-sdk/31/android-31.jar`.
- Keeps the same narrow adapter shape as `services-28` through `services-30`.

## Key Source Areas

- `services-31/src/main/java/tornaco/apps/shortx/services31/pm/`
- `services-31/src/main/java/tornaco/apps/shortx/services31/app/`

## Important Entry Points

- `PMV31.kt`: package manager compatibility wrapper.
- `UidObserver.kt`: UID/app observation for API 31 behavior.

## How It Connects

Acts as one of the runtime-selected framework shims behind the base `services` module.

## Reading Outcome

Use this module to understand Android 12-specific package and UID observation differences.
