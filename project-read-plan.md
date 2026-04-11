# ShortX Project Read Plan

This document is a practical reading plan for understanding the whole ShortX repository.

The goal is not to read every file line-by-line. The goal is to build a correct mental model of:

- what the product does
- how rules, actions, triggers, and hooks fit together
- which modules own API contracts versus runtime implementations
- where Android-version-specific behavior lives
- how Xposed and Magisk related pieces are packaged

## 1. What This Project Is

ShortX is an Android automation platform with system-level integration.

At a high level it provides:

- one-click commands: manually launched action pipelines
- automatic rules: trigger -> optional conditions -> actions
- scripting and expression evaluation through MVEL
- Xposed-based method hooks and system integration
- version-specific service implementations for different Android releases

Before reading code, read these generated docs first:

- `out/guide/fact.md`
- `out/guide/da.md`
- `out/guide/da_rule.md`
- `out/guide/fact_hook.md`
- `out/guide/usefull_mvel.md`
- `out/guide/install_guide.md`

These explain the product model better than starting from code.

## 2. Repo Map

### Entry and build structure

- `build.gradle.kts`: root build logic, versioning, shared Android/JVM config
- `settings.gradle.kts`: module inventory
- `buildSrc/`: custom Gradle conventions and helper plugins for this repo
- `gradle/libs.versions.toml`: dependency and plugin catalog
- `scripts/`: repo build/test helpers

### Product modules

- `app`: Android application packaging and primary app manifest
- `ui`: main app UI, editors, screens, flows, feature wiring
- `ui-base`: reusable UI/base components
- `core-api`: public/shared contracts, AIDL, manager APIs, proto-facing interfaces
- `core`: shared core logic, constants, API definitions, integration metadata
- `services`: main runtime service layer, rule/action execution, scripting, cross-version coordination
- `services-28` to `services-35`: Android-version-specific service implementations
- `services-jni`: native/JNI-backed support code
- `services-ui`: UI-facing service helper classes and packaged jars used by service-side features

### Patch and privilege modules

- `patch-xposed/patch-common`: common Xposed patch logic
- `patch-xposed/patch-main`: main Xposed module/patch implementation
- `patch-xposed/patch-systemui`: SystemUI-specific hooks
- `patch-magisk/module`: Magisk module packaging
- `patch-magisk/bridge`: bridge layer for Magisk-side integration
- `patch-magisk/bridge-dex-app`: dex/app bridge artifacts
- `patch-magisk/patch-framework`: framework patching support

### Support and external modules

- `prebuilts-gen`: generated prebuilts support
- `external/*`: vendored libraries or repo-maintained support libraries

## 3. Recommended Read Order

Read in this order. The order matters.

### Phase 1: Product and build mental model

Objective: understand what ships, how it is activated, and which modules matter most.

Read:

1. `settings.gradle.kts`
2. `build.gradle.kts`
3. `app/build.gradle.kts`
4. `app/src/main/AndroidManifest.xml`
5. `out/guide/*.md` files listed above

Questions to answer before moving on:

- Why is this an app and also an Xposed module?
- Which modules are Android apps/libs versus pure Kotlin/JVM modules?
- Which parts are runtime-critical and which are packaging/support?

### Phase 2: Contract layer and domain vocabulary

Objective: learn the nouns used everywhere else.

Read `core-api` first, then `core`.

Start with:

- `core-api/build.gradle.kts`
- `core-api/src/main/java/tornaco/apps/shortx/core/IShortX.aidl`
- `core-api/src/main/java/tornaco/apps/shortx/core/ShortXManager.kt`
- `core-api/src/main/java/tornaco/apps/shortx/core/rule/script/api/ScriptShortXApi.java`
- protobuf models under `core-api/src/main` and `core/src/main`
- `core/build.gradle.kts`

What to extract:

- the service boundary exposed through AIDL
- how actions are executed programmatically
- how rules, contexts, and variables are represented
- what script APIs are intentionally exposed to MVEL
- which build-time constants define external URLs, app IDs, and wiki/help endpoints

Checkpoint:

You should be able to explain the difference between `core-api` and `core` in one paragraph.

### Phase 3: Rule engine and runtime execution

Objective: understand where triggers, conditions, actions, scripts, and execution strategies actually run.

Read:

- `services/build.gradle.kts`
- `services/src/main/...` packages related to rule execution
- `services/src/main/...` packages related to actions
- `services/src/main/...` packages related to facts/triggers
- `services/src/main/...` packages related to script/MVEL execution
- `services/src/main/...` packages related to window manager, automation, shell, notifications, and HTTP

Suggested search terms while reading:

- `executeAction`
- `Action`
- `Condition`
- `Fact`
- `Trigger`
- `EvaluateContext`
- `MVEL`
- `ScriptShortXApi`
- `MethodHook`
- `UiAutomation`

What to learn here:

- rule execution pipeline
- context propagation
- cancellation, sync/async strategies, duplicate-trigger behavior
- how actions call platform services or external capabilities
- where supported trigger types are implemented

Checkpoint:

You should be able to trace one full path from trigger detection to action execution.

### Phase 4: UI and feature wiring

Objective: understand how the user creates and manages rules/commands.

Read `ui` after you understand the backend model.

Start with:

- `ui/build.gradle.kts`
- app entry activity and main navigation in `ui/src/main`
- rule editor screens
- action picker/editor flows
- trigger/fact picker/editor flows
- settings, repo/discovery, import/export, and shortcut related screens

Also read:

- `ui-base/src/main`

What to extract:

- how UI models map to proto/domain models
- where Compose screens are organized
- how feature state is managed
- where the app calls `ShortXManager` or related service APIs
- how online repo, sharing, import/export, and shortcuts are surfaced

Checkpoint:

You should be able to point to the UI flow for creating an automatic rule and a one-click command.

### Phase 5: App assembly and bootstrapping

Objective: understand how everything is packaged and started.

Read:

- `app/src/main/java/tornaco/apps/shortx/*`
- `app/src/main/AndroidManifest.xml`
- flavor-specific source sets under `app/src/`

Focus on:

- application startup
- service binding/bootstrap
- debug-only integrations
- market flavor differences (`row` vs `prc`)
- any wiring between app, UI, services, and patch modules

Checkpoint:

You should know what the `app` module owns that `ui` does not.

### Phase 6: Android-version-specific services

Objective: understand how platform drift is handled.

Read in this order:

1. `services`
2. one representative version module, preferably `services-34` or `services-35`
3. compare against one older module such as `services-29`
4. skim the remaining version modules for pattern differences

What to look for:

- interfaces or adapters implemented per Android version
- hidden API or framework structure differences
- reflection/service compatibility workarounds
- why generic logic stays in `services` while platform-specific logic moves out

Checkpoint:

You should be able to describe the extension point shared by `services-*` modules.

### Phase 7: Xposed and hook path

Objective: understand how ShortX gains deep system visibility and app/process hooks.

Read:

1. `patch-xposed/patch-common`
2. `patch-xposed/patch-main`
3. `patch-xposed/patch-systemui`

Use these search terms:

- `IXposed`
- `libxposed`
- `handleLoadPackage`
- `hook`
- `SystemUI`
- `MethodHookParam`

What to extract:

- package load entry points
- scope and target process selection
- how method hooks are registered
- how hook events are bridged back into the ShortX rule/runtime layer

Checkpoint:

You should be able to explain how a user-defined method hook in the product turns into runtime behavior in code.

### Phase 8: Magisk and framework patch path

Objective: understand the non-app packaging and privileged deployment side.

Read:

- `patch-magisk/module`
- `patch-magisk/bridge`
- `patch-magisk/bridge-dex-app`
- `patch-magisk/patch-framework`

This phase is lower priority unless you need release engineering or platform patch behavior.

What to extract:

- what gets packaged as a Magisk module
- how bridge components communicate with the app/runtime
- whether this path supplements or replaces some Xposed functionality

### Phase 9: JNI and prebuilts

Objective: understand native dependencies only after the Java/Kotlin architecture is clear.

Read:

- `services-jni`
- `prebuilts-gen`

Look for:

- native entry points exposed upward
- why native code is needed
- generated/prebuilt artifacts that affect runtime compatibility

### Phase 10: External modules and utilities

Objective: separate core product code from vendored or support code.

Classify each `external/*` module as one of:

- vendored third-party library
- lightly modified dependency
- repo-owned support library used by ShortX

Read deeply only for modules that ShortX relies on directly for product behavior, such as:

- `external:utils`
- `external:rhino-android`
- `external:dex-maker`
- `external:sec_net`
- `external:codeditor`
- `external:icons`

Skim-only candidates unless they appear in a call path you care about:

- `external:picasso`
- `external:dateformatter`
- `external:compose-color-picker`
- `external:reorderable`
- `external:drawbox`
- `external:sardine-android`

## 4. Module-by-Module Reading Checklist

Use this checklist while reading. For each module, write down the answers.

### For every module

- What responsibility does it own?
- Is it API, implementation, packaging, compatibility, or UI?
- Who depends on it?
- What are its most important entry points?
- What data types cross its boundaries?
- What code in this module is Android-version-specific or privileged?

### For rule-related modules

- Where are triggers represented?
- Where are conditions evaluated?
- Where are actions defined and dispatched?
- How is execution context created and mutated?
- How are cancellation, retries, and deduplication handled?

### For UI modules

- Which screen edits which model?
- Where is navigation defined?
- Where are persisted settings/state stored?
- How does the UI translate user input into proto/domain objects?

### For patch modules

- What process does the patch run in?
- What framework APIs or hidden APIs does it rely on?
- How does it pass information back to core runtime or services?

## 5. Fast Search Strategy

Do not browse randomly. Use targeted search.

Recommended ripgrep queries:

```bash
rg "IShortX|ShortXManager|executeActionBlocking|EvaluateContext" core-api core app ui services
rg "Fact|Trigger|Condition|Action" services core ui
rg "MVEL|ScriptShortXApi|writeGlobalVarWithOp|readGlobalVar" services core core-api ui
rg "IXposed|libxposed|handleLoadPackage|MethodHookParam|hook" patch-xposed app services
rg "services-2[89]|services-3[0-5]" services services-28 services-29 services-30 services-31 services-32 services-33 services-34 services-35
```

## 6. Recommended Output Artifacts While Reading

Create these notes as you go.

### A. One-page architecture summary

Include:

- app startup path
- UI -> manager/service path
- trigger/condition/action execution path
- Xposed hook integration path
- version-specific service resolution path

### B. Module ownership table

Columns:

- module
- type
- owns
- key entry points
- depends on
- read depth

### C. End-to-end traces

Write at least three:

1. notification trigger -> rule evaluation -> action execution
2. method hook trigger -> context creation -> script/action execution
3. one-click command launch -> action pipeline

## 7. Suggested Time Allocation

If you want a practical schedule:

- Day 1: product docs + root build + `core-api` + `core`
- Day 2: `services`
- Day 3: `ui` + `app`
- Day 4: `services-*` version modules
- Day 5: `patch-xposed` + `patch-magisk` + JNI/prebuilts

If you only need enough context to start coding safely:

- Must-read: `core-api`, `core`, `services`, `ui`, `app`
- Read-on-demand: `services-*`, `patch-*`, `services-jni`, most `external/*`

## 8. Minimum Understanding Bar

You understand the project well enough to modify it safely when you can answer these questions without guessing:

- How is a user-defined rule stored and executed?
- Which layer owns the public service/API contract?
- Which layer owns business logic and runtime execution?
- Which layer owns Compose UI and editor flows?
- Where does Xposed enter the system?
- Why are there separate `services-*` modules?
- Which modules are product code versus packaging/integration support?

## 9. Final Advice

Read top-down by architecture, not alphabetically by folder.

The common failure mode in this repo is to jump into `ui` or a patch module too early. Start with the product model, then contracts, then the runtime engine, and only then the UI and patch layers. That order gives the fastest path to a correct mental model.