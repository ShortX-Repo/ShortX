# patch-magisk/bridge

## Role

`patch-magisk/bridge` is the Java-side bridge invoked by the native Magisk module. It is a thin entry layer for process events coming from Zygisk.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Minimal module with very small surface area.
- Depends mainly on `core`.

## Key Source Areas

- `patch-magisk/bridge/src/main/java/github/tornaco/android/shortx/magisk/bridge/ShortXBridge.java`

## Important Entry Points

- `ShortXBridge.java`: static main entry called by the native loader.

## How It Connects

Loaded by `patch-magisk/module`, then packaged into a DEX/JAR path through `patch-magisk/bridge-dex-app`.

## Reading Outcome

This module is the Java control handoff for the Magisk path, but it appears lighter and less mature than the Xposed integration path.
