# services-29

## Role

`services-29` contains Android 10 specific compatibility bindings used by the main `services` module.

## Module Type

- Kotlin/JVM compatibility module

## Key Build Facts

- Compiles against `android-sdk/29/android-29.jar`.
- Mirrors the minimal compatibility pattern established in `services-28`.

## Key Source Areas

- `services-29/src/main/java/tornaco/apps/shortx/services29/pm/`
- `services-29/src/main/java/tornaco/apps/shortx/services29/app/`

## Important Entry Points

- `PMV29.kt`: package manager compatibility wrapper.
- `UidObserver.kt`: UID/app observation for API 29 behavior.

## How It Connects

Used through the base `services` layer when API 29 differences matter.

## Reading Outcome

Compare this module to `services-28` and `services-35` to see how the compatibility surface grows over Android releases.
