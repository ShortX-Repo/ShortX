<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan:
- Plan: specs/010-upgrade-lsposed-api/plan.md
- Spec: specs/010-upgrade-lsposed-api/spec.md
- Research: specs/010-upgrade-lsposed-api/research.md
- Data Model: specs/010-upgrade-lsposed-api/data-model.md
- Contracts: specs/010-upgrade-lsposed-api/contracts/runtime-behavior.md
- Quickstart: specs/010-upgrade-lsposed-api/quickstart.md
<!-- SPECKIT END -->

# ShortX Development Guide

## What is ShortX

Android automation/shortcut app that also runs as Xposed/Magisk module. Multi-module Gradle project (~40 modules).

## Build Commands

**Full build + install (PRC debug, skip minify):**
```bash
./gradlew app:installPrcDebug -PnoMinify=true
```

**CI test command (what CI actually runs):**
```bash
./gradlew app:testPrcDebugUnitTest services:test
```

**Run a single module's unit tests:**
```bash
./gradlew :patch-xposed:patch-systemui:testDebugUnitTest
```

## Code Generation (Required Before Compile When AIDL/Proto Change)

If you edit AIDL or protobuf files in `core-api`, generated sources won't refresh from compile alone. Run in order:
```bash
./gradlew :core-api:generateAidlFiles
./gradlew :core-api:generateProto
```
Then validate with `./gradlew :core-api:compileKotlin` (or add affected modules).

## Architecture

Four layers, top to bottom:

```
ui ←→ core-api ←→ services ←→ patch-xposed
         ↕              ↕
    ShortXManager   AIDL(IShortX)
```

- **core-api**: Protobuf models, AIDL interfaces, `ShortXManager` client. All other layers depend on this.
- **services**: Rule engine runtime (Fact → Condition → Action). Runs in system_server via Xposed.
- **ui / ui-base**: Jetpack Compose UI, Fact/Condition/Action UI models and converters.
- **patch-xposed**: Xposed hook injection (legacy, API 100, API 101).
- **patch-magisk**: Native Zygisk integration.

Architecture docs: `AI/README.md` (start with `AI/architecture/cross-module-architecture-map.md`).

## i18n

ShortX uses JSON assets, not Android `strings.xml`, for product text. Translation files live in `ui/src/main/assets/i18n/<locale>/`. When adding user-facing text, update the checked-in JSON assets — do not rely on code-side fallbacks. Keep `en-US`, `zh-CN`, and `zh-TW` aligned.

## Code Quality

- **detekt** runs on all modules (config: `config/detekt/detekt.yml`, baseline: `config/detekt/baseline.xml`)
- Max line length: 120 chars, indent: 4 spaces
- Compose-specific rules are active (modifier naming, content emitters, etc.)
- Java source/target compatibility: **Java 21**

## Key Quirks

- Build flavor: `prc` (China) or `row` (rest of world). CI uses `prc`.
- Version code is time-based: `((currentTimeMillis - startOf2000) / 1000 / 60 / 10) + 100000`
- Build output goes to `out/`, not default `build/` dirs.
- `local.properties` and `github.properties` may contain secrets — never commit them.

## Development Workflow

For any code change: read relevant `AI/` docs first, find an existing similar implementation to use as a template, then implement. Run the full build command to validate. The `shortx-dev` skill in `.claude/skills/shortx-dev/SKILL.md` has detailed per-task guidance.
