# patch-magisk/module

## Role

`patch-magisk/module` is the native Zygisk integration layer. It receives early process events from Magisk and bridges them into Java-side ShortX logic.

## Module Type

- Android native/C++ module

## Key Build Facts

- Built with CMake and targets multiple ABIs.
- Uses Zygisk lifecycle hooks for app and server specialization.
- Loads a Java bridge DEX at runtime.

## Key Source Areas

- `patch-magisk/module/src/main/cpp/zygisk/shortx.cpp`
- `patch-magisk/module/src/main/cpp/core/Bridge.cpp`
- `patch-magisk/module/src/main/cpp/core/Bridge.h`

## Important Entry Points

- `shortx.cpp`: Zygisk module entry and specialization hooks.
- `Bridge.cpp`: native-to-Java bridge loader.
- `Bridge.h`: JNI bridge declarations.

## How It Connects

This module is the native front door of the Magisk path. It eventually loads the Java bridge packaged by `patch-magisk/bridge-dex-app`.

## Reading Outcome

Read this only if you are working on the Magisk or Zygisk execution path.
