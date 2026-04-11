# i18n Architecture

## Purpose

This document explains how internationalization works in ShortX across the UI and service layers.

## Short Summary

ShortX does not rely only on Android `strings.xml` resources for feature text. Instead, it uses a custom JSON-based i18n system layered on top of Android locale selection.

The flow is:

1. Android resource qualifiers choose a locale name through `ui_i18n_name`
2. ShortX loads one or more JSON translation files from assets under `ui/src/main/assets/i18n/<locale>/`
3. translations are merged into an in-memory map
4. both UI and services read strings through a shared `I18N` interface

## Core Interface

The shared contract is defined in:

- `core-api/src/main/java/tornaco/apps/shortx/core/I18N.kt`

It exposes a single lookup method:

- key
- named args
- optional fallback

This keeps callers simple and lets both UI and services use the same access pattern.

## Locale Selection

Locale selection starts with Android resources, not JSON.

Relevant files:

- `ui/src/main/res/values/i18n.xml`
- `ui/src/main/res/values-zh-rCN/i18n.xml`
- `ui/src/main/res/values-zh-rTW/i18n.xml`

These define `ui_i18n_name`, for example:

- `en-US`
- `zh-CN`
- `zh-TW`

Android selects the correct resource based on device locale. ShortX then uses that selected value to decide which asset directory to load.

## Translation Storage

Translations are stored as JSON asset files under:

- `ui/src/main/assets/i18n/en-US/`
- `ui/src/main/assets/i18n/zh-CN/`
- `ui/src/main/assets/i18n/zh-TW/`

Examples:

- `ui/src/main/assets/i18n/en-US/i18n-en-US.json`
- `ui/src/main/assets/i18n/en-US/i18n-en-US2.json`

Important behavior:

- all `.json` files in the locale directory are loaded
- files are sorted before loading
- later-loaded files can override earlier keys

Development note:

- when adding new user-facing i18n keys, put them in the checked-in files under `ui/src/main/assets/i18n/`
- do not rely on fallback strings in code as the final source of truth for product text
- keep the locale files aligned at least for `en-US`, `zh-CN`, and `zh-TW`

This means the translation set can be split across multiple JSON files instead of one giant file.

## UI-Side Implementation

The UI implementation lives in:

- `ui/src/main/java/tornaco/apps/shortx/ui/base/I18NImpl.kt`

Key pieces:

- `i18NIns`: singleton `I18N` implementation used by the UI
- `I18NCache`: in-memory translation store
- `I18NCache.init(context)`: loads the locale directory based on `R.string.ui_i18n_name`

Lookup behavior:

- if the key exists, it uses ICU `MessageFormat` to render named arguments
- if the key does not exist, it returns the provided fallback or the key itself

Notable detail:

- UI-side keys are normalized to lowercase when loaded

## Service-Side Implementation

The service implementation lives in:

- `services/src/main/java/tornaco/apps/shortx/services/ui/I18NImpl.kt`

This is similar to the UI-side implementation but initialized differently.

During `ShortXService.systemReady()`:

1. service code builds `AppResources`
2. it reads `ui_i18n_name`
3. it calls `services.ui.I18NCache.init(context, localeName)`

This allows service-driven UI such as:

- overlays
- dialogs
- notifications
- watchdog messages

to use the same translation keys as the main app UI.

## Compose Integration

In the Compose UI layer, translations are exposed through:

- `ui-base/src/main/java/tornaco/apps/shortx/ui/base/CompositionLocals.kt`

Specifically:

- `LocalI18N`

This lets composables call:

- `LocalI18N.current["ui.some.key"]`

View models and base context models also carry `i18NIns`, so non-composable UI logic can use the same lookup path.

## Formatting Behavior

Formatting uses ICU `MessageFormat`.

This enables strings like:

- `"Search {count} items"`

to be rendered with a named argument map.

This is more flexible than plain string replacement and fits the JSON-based translation system well.

## Fallback Behavior

There are two fallback layers.

### Locale fallback

If the requested locale directory cannot be loaded, the implementation falls back to English assets:

- `i18n/en-US/`
- and finally `i18n/en-US/i18n-en-US.json`

### Key fallback

If a specific key is missing:

- return the provided fallback string, or
- return the key itself

This makes missing translations obvious in development because untranslated keys appear directly on screen.

## Build-Time Translation Sync

There is a helper task in:

- `buildSrc/src/main/kotlin/tornaco/project/android/shortx/GetI18N.kt`

It can download translation files from:

- `https://github.com/ShortX-Repo/ShortX-i18n`

Important detail:

- the `ui` module imports this helper, but `addI18NTask()` is currently commented out in `ui/build.gradle.kts`

So today, the repo appears to rely on checked-in asset files rather than refreshing translations automatically every build.

## Strengths Of This Design

- same translation keys can be used in both UI and services
- JSON files are easier to maintain outside classic Android resource XML
- translations can be split into multiple files per locale
- missing keys degrade safely instead of crashing

## Weak Spots / Notes

- UI and services maintain separate caches with similar logic
- UI lowercases keys on load, services do not, which is inconsistent
- the build sync task is present but not active in normal builds
- because the main strings are not classic Android string resources, Android tooling support is weaker than with `strings.xml`

## Practical Reading Guide

If you want to understand or change i18n behavior, read these files in order:

1. `core-api/src/main/java/tornaco/apps/shortx/core/I18N.kt`
2. `ui/src/main/java/tornaco/apps/shortx/ui/base/I18NImpl.kt`
3. `services/src/main/java/tornaco/apps/shortx/services/ui/I18NImpl.kt`
4. `ui/src/main/res/values*/i18n.xml`
5. `ui/src/main/assets/i18n/**`
6. `buildSrc/src/main/kotlin/tornaco/project/android/shortx/GetI18N.kt`

## Key Insight

ShortX uses Android only to choose the locale, but not to store most product text. The real translation content lives in JSON assets and is consumed through a shared `I18N` abstraction across both the app UI and the runtime service layer.
