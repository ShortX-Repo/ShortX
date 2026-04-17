# Legacy IMS Edge Gesture Source

This directory contains reference copies of the older ShortX edge gesture implementation that used the `android` process `InputManagerService.filterInputEvent` hook plus `setInputFilter` plumbing.

Source commit:

- `6cad2d45d7ef7cb3cd2d825105e45bbc6b2172a2`
  - This is `7ff92d2bb5bc09bad3c33a4b1a56525d105fb3c6^`, the parent commit immediately before gestures were moved out of `services` and into `patch-systemui`.

Recovered core files:

- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/InputManagerHook.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/Area.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/Detector.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/GesturePlayer.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/GestureRecorder.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/GestureService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/PointerEventDispatcher.java`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/SystemGesturesPointerEventListener.java`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/WithPointerLocation.kt`
- `services/src/main/java/tornaco/apps/shortx/services/input/InputFilterInstaller.kt`
- `services/src/main/java/tornaco/apps/shortx/services/input/InputChannelCompat.java`
- `services/src/main/java/tornaco/apps/shortx/services/input/InputConnectionCompat.kt`
- `services/src/main/java/tornaco/apps/shortx/services/input/InputContextCompat.kt`
- `services/src/main/java/tornaco/apps/shortx/services/input/InputManagerGlobalFunc.kt`
- `services/src/main/java/tornaco/apps/shortx/services/input/InputMonitorCompat.java`
- `services/src/main/java/tornaco/apps/shortx/services/input/InputService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/input/InputShellCommand.kt`
- `services/src/main/java/tornaco/apps/shortx/services/input/IMSFunc.kt`
- `services/src/main/java/tornaco/apps/shortx/services/input/opls/OplusInputConsumer.java`
- `services/src/main/java/tornaco/apps/shortx/services/input/opls/OplusInputConsumerManager.java`
- `services/src/main/java/tornaco/apps/shortx/services/input/opls/OplusInputMonitorHelper.java`

Notes:

- These files are restored as historical reference only.
- They are intentionally placed under `AI/` so they do not join the active Gradle build.
- This recovered set captures the old IMS/filterInput gesture entry path, edge detector, and input-filter/injection seam.
- It does not yet include every ancillary class from the same era. If needed, more historical files can be restored into this reference tree in a second pass.
