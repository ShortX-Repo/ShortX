# patch-xposed/patch-systemui

## Role

`patch-xposed/patch-systemui` contains SystemUI-specific hook logic. It focuses on quick settings, gestures, view interception, status bar behavior, and OCR-related integrations.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Depends on `patch-common`, `core`, `core-api`, and selected runtime/UI support libraries.
- Is narrower than `patch-main`, but deeper in SystemUI-specific behavior.

## Key Source Areas

- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/SystemUIHook.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookApp.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/ocr/`

## Important Entry Points

- `SystemUIHook.kt`: package-specific SystemUI entry.
- `hookApp.kt`: SystemUI process setup.
- Gesture detector packages: specialized input monitoring.
- OCR implementations: text extraction support inside SystemUI-related paths.

## How It Connects

This module is invoked from `patch-main` when the loaded package is SystemUI. It is not generic app-hook logic; it is the SystemUI specialization layer.

## Reading Outcome

Read this module for quick settings, status bar, gesture, or SystemUI customization work.
