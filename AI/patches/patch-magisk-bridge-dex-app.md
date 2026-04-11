# patch-magisk/bridge-dex-app

## Role

`patch-magisk/bridge-dex-app` exists to package the Magisk bridge code into an APK-derived DEX/JAR artifact that can be loaded by the native Zygisk bridge.

## Module Type

- Android application packaging module

## Key Build Facts

- Depends on `patch-magisk:bridge`.
- Disables most unnecessary Android build features.
- Includes a custom extraction task to turn the built APK output into the bridge artifact.

## Key Source Areas

- `patch-magisk/bridge-dex-app/build.gradle.kts`
- `patch-magisk/bridge-dex-app/src/main/AndroidManifest.xml`

## Important Entry Points

- Custom extraction task in the build script.
- The built APK itself, which acts as a container for the bridge code.

## How It Connects

This module is not runtime logic by itself. Its purpose is packaging the Java bridge so `patch-magisk/module` can load it at runtime.

## Reading Outcome

Read this when you need to understand artifact packaging on the Magisk path rather than business logic.
