# app

## Role

`app` is the Android application assembly module. It ties together UI, services, Xposed integration, flavor-specific setup, and startup initialization.

## Module Type

- Android application module

## Key Build Facts

- Declares product flavors `row` and `prc`.
- Applies Google services and Firebase only for the `row` flavor.
- Packages all current Xposed runtime modules directly: `patch-xposed:patch-legacy`, `patch-xposed:patch-100`, `patch-xposed:patch-101`, plus the shared `patch-xposed:patch-main` layer.
- Adds compile-only visibility for legacy Xposed API 82, libxposed API 100, and libxposed API 101 so app compilation and shrink steps can resolve Xposed-facing types.
- Generates dynamic ProGuard rules and random repackaging names during build.

## Key Source Areas

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/tornaco/apps/shortx/ShortXApp.kt`
- `app/src/main/java/tornaco/apps/shortx/FakeServiceInstaller.kt`
- `app/src/main/java/tornaco/apps/shortx/LogInit.kt`

## Important Entry Points

- `ShortXApp`: process-level initialization.
- `AndroidManifest.xml`: Xposed module metadata and app bootstrap declarations.
- `FakeServiceInstaller.kt`: debug/service stub support.
- `LogInit.kt`: logging setup.

## How It Connects

This module owns packaging and startup, not most feature logic. It initializes `ui` entry points, brings in `services`, and packages the full multi-runtime Xposed path into the installable app.

## Reading Outcome

Read this module to understand how ShortX is shipped as both an Android app and a package that currently contains legacy, API 100, and API 101 Xposed entry paths.
