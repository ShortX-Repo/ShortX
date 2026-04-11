# services-30

## Role

`services-30` contains Android 11 specific compatibility bindings used by the main `services` module.

## Module Type

- Kotlin/JVM compatibility module

## Key Build Facts

- Compiles against `android-sdk/30/android-30.jar`.
- Continues the same small version-adapter pattern as earlier service modules.

## Key Source Areas

- `services-30/src/main/java/tornaco/apps/shortx/services30/pm/`
- `services-30/src/main/java/tornaco/apps/shortx/services30/app/`

## Important Entry Points

- `PMV30.kt`: package manager compatibility wrapper.
- `UidObserver.kt`: UID/app observation for API 30 behavior.

## How It Connects

Selected by runtime compatibility logic inside the main `services` module.

## Reading Outcome

Read this when a behavior differs specifically on Android 11.
