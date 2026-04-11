# ShortX Project Read Tasks

This file breaks [project-read-plan.md](/home/bp/android/ShortX/project-read-plan.md) into small, easy-to-execute tasks.

Rule for each task:

- read only the listed files or folder
- write 2 to 5 lines of notes before moving on
- stop when the task question is answered

Recommended task size:

- 10 to 30 minutes each

## Phase 1: Product And Build Basics

- [ ] Task 1: Read `out/guide/fact.md` and write a 3-line summary of what a trigger, condition, and action mean in ShortX.
- [ ] Task 2: Read `out/guide/da.md` and list the difference between one-click commands and automatic rules.
- [ ] Task 3: Read `out/guide/da_rule.md` and note the execution strategy options and duplicate-trigger options.
- [ ] Task 4: Read `out/guide/fact_hook.md` and write down what `param.args`, `param.result`, and `param.thisObject` are used for.
- [ ] Task 5: Read `out/guide/usefull_mvel.md` and note 3 things MVEL can do in this project.
- [ ] Task 6: Read `out/guide/install_guide.md` and explain why the project needs root plus Xposed/LSPosed.
- [ ] Task 7: Read `settings.gradle.kts` and make a grouped list of modules: app, UI, core, services, patches, external.
- [ ] Task 8: Read `build.gradle.kts` and note where versioning, shared Android config, and common plugin setup live.
- [ ] Task 9: Read `app/build.gradle.kts` and write down what the `app` module directly depends on.
- [ ] Task 10: Read `app/src/main/AndroidManifest.xml` and explain why this app is also an Xposed module.

## Phase 2: Build Conventions

- [ ] Task 11: Read `buildSrc/build.gradle.kts` and identify what custom Gradle logic is built locally.
- [ ] Task 12: Read `buildSrc/src/main` and list the custom plugin or helper names you find.
- [ ] Task 13: Read `gradle/libs.versions.toml` and note the major dependency groups used by the repo.
- [ ] Task 14: Read `scripts/run_tests.sh` and list the main test or validation entry points exposed by the repo.

## Phase 3: Core API Layer

- [ ] Task 15: Read `core-api/build.gradle.kts` and note what kind of module `core-api` is.
- [ ] Task 16: Read `core-api/src/main/java/tornaco/apps/shortx/core/IShortX.aidl` and list the most important remote capabilities exposed through the service.
- [ ] Task 17: Read the top-level structure of `core-api/src/main/java/tornaco/apps/shortx/core/ShortXManager.kt` and describe what role the manager plays.
- [ ] Task 18: Find `executeActionBlocking` in `ShortXManager.kt` and note what inputs it needs.
- [ ] Task 19: Read `core-api/src/main/java/tornaco/apps/shortx/core/rule/script/api/ScriptShortXApi.java` and list the script APIs exposed to MVEL.
- [ ] Task 20: Skim protobuf definitions under `core-api/src/main` and list the main model groups you see.

## Phase 4: Core Shared Layer

- [ ] Task 21: Read `core/build.gradle.kts` and note what constants, URLs, or app metadata are generated here.
- [ ] Task 22: Read the top-level packages under `core/src/main` and list what responsibilities seem to belong to `core`.
- [ ] Task 23: Find where context or rule-related model helpers live in `core/src/main` and write a short note about them.
- [ ] Task 24: Compare `core-api` and `core` and write one paragraph explaining the difference.

## Phase 5: Services Runtime Overview

- [ ] Task 25: Read `services/build.gradle.kts` and list the modules and libraries that make the services layer powerful.
- [ ] Task 26: List the top-level packages under `services/src/main` and classify them as trigger, action, script, integration, or support.
- [ ] Task 27: Search for `executeAction` in `services/src/main` and identify the main runtime action execution path.
- [ ] Task 28: Search for `EvaluateContext` in `services/src/main` and note where execution context is created or updated.
- [ ] Task 29: Search for `Fact` and `Trigger` in `services/src/main` and list the packages that implement trigger detection.
- [ ] Task 30: Search for `Condition` in `services/src/main` and note where condition evaluation is handled.
- [ ] Task 31: Search for `MVEL` and `ScriptShortXApi` in `services/src/main` and write down where scripting is executed.
- [ ] Task 32: Search for `MethodHook` in `services/src/main` and note how hooks are represented on the services side.
- [ ] Task 33: Find the code path for one concrete trigger, such as notification or app foreground, and write a step-by-step trace.
- [ ] Task 34: Find the code path for one concrete action, such as shell command or dialog, and write a step-by-step trace.

## Phase 6: UI Overview

- [ ] Task 35: Read `ui/build.gradle.kts` and note which dependencies suggest the main UI technologies and features.
- [ ] Task 36: List the top-level packages under `ui/src/main` and identify where navigation and entry screens likely live.
- [ ] Task 37: Find the main activity or app navigation entry point in `ui/src/main` and write a short summary of the app startup UI flow.
- [ ] Task 38: Find the screen or package for creating/editing automatic rules and note the main files involved.
- [ ] Task 39: Find the screen or package for one-click commands and note the main files involved.
- [ ] Task 40: Find where the UI calls `ShortXManager` or related service APIs and record 3 representative call sites.
- [ ] Task 41: Find where import/export, sharing, or online repo features live in `ui/src/main`.
- [ ] Task 42: Read `ui-base/src/main` and list the reusable UI or base abstractions it provides.

## Phase 7: App Bootstrapping

- [ ] Task 43: List the top-level Kotlin files under `app/src/main/java/tornaco/apps/shortx` and identify likely startup files.
- [ ] Task 44: Read the application class and write down what gets initialized at process start.
- [ ] Task 45: Find how the app binds to or installs the ShortX service layer and summarize the mechanism.
- [ ] Task 46: Check `app/src/row` and `app/src/prc` source/resource differences and note the flavor-specific behavior.
- [ ] Task 47: Explain in 3 to 5 lines what the `app` module owns that `ui` does not.

## Phase 8: Version-Specific Services

- [ ] Task 48: Read `services-35/build.gradle.kts` or `services-34/build.gradle.kts` and note what it depends on.
- [ ] Task 49: List top-level packages in one representative module, such as `services-35/src/main`, and note what platform-specific code lives there.
- [ ] Task 50: Read `services-29/build.gradle.kts` and compare it with a newer services module.
- [ ] Task 51: Compare one matching class or package between `services-29` and `services-35` and write down the platform difference.
- [ ] Task 52: Skim the remaining `services-*` modules and list any obvious patterns shared by all of them.

## Phase 9: Xposed Patch Path

- [ ] Task 53: Read `patch-xposed/patch-common/build.gradle.kts` and note its role.
- [ ] Task 54: List the top-level packages in `patch-xposed/patch-common/src/main` and note the shared hook abstractions.
- [ ] Task 55: Read `patch-xposed/patch-main/build.gradle.kts` and note what makes it the main Xposed integration module.
- [ ] Task 56: Search for `IXposed`, `libxposed`, or `handleLoadPackage` inside `patch-xposed/patch-main/src/main` and identify the runtime entry point.
- [ ] Task 57: Find where package filtering or scope selection happens for Xposed hooks.
- [ ] Task 58: Find where method hooks are registered and write a short trace from hook registration to event handling.
- [ ] Task 59: Read `patch-xposed/patch-systemui/src/main` and note what is specific to SystemUI.

## Phase 10: Magisk Patch Path

- [ ] Task 60: Read `patch-magisk/module/build.gradle.kts` and note what gets packaged for Magisk deployment.
- [ ] Task 61: Read `patch-magisk/bridge/build.gradle.kts` and list what the bridge depends on.
- [ ] Task 62: Read `patch-magisk/bridge-dex-app/build.gradle.kts` and note why a dex/app bridge may exist.
- [ ] Task 63: Read `patch-magisk/patch-framework/build.gradle.kts` and summarize its purpose.
- [ ] Task 64: Write 3 to 5 lines comparing the likely roles of the Xposed path and the Magisk path.

## Phase 11: Native And Prebuilt Support

- [ ] Task 65: Read `services-jni/build.gradle.kts` and note how native code is included.
- [ ] Task 66: List the top-level contents of `services-jni/src` and describe what part of the system likely uses JNI.
- [ ] Task 67: Read `prebuilts-gen/build.gradle.kts` and note what artifacts it appears to generate.

## Phase 12: External Modules

- [ ] Task 68: Read the build files for all `external/*` modules and classify each as vendored, support, or product-adjacent.
- [ ] Task 69: Read `external/utils` and note what common helper categories it provides.
- [ ] Task 70: Read `external/rhino-android` and note whether it is used directly for scripting or embedded execution.
- [ ] Task 71: Read `external/dex-maker` and note why runtime code generation may be needed.
- [ ] Task 72: Read `external/sec_net` and summarize what security/networking role it appears to have.
- [ ] Task 73: Read `external/codeditor` and `external/icons` only enough to explain why they are bundled with the app.

## Phase 13: Final Synthesis Tasks

- [ ] Task 74: Write a one-page architecture summary covering app startup, service binding, rule execution, hooks, and version-specific services.
- [ ] Task 75: Create a module ownership table with columns: module, type, owns, key entry points, depends on, read depth.
- [ ] Task 76: Write an end-to-end trace for notification trigger -> rule evaluation -> action execution.
- [ ] Task 77: Write an end-to-end trace for method hook trigger -> context creation -> script/action execution.
- [ ] Task 78: Write an end-to-end trace for one-click command launch -> action pipeline.
- [ ] Task 79: List 5 modules you now understand well and 5 modules that still need deeper reading.
- [ ] Task 80: Write a final paragraph answering: where should a new contributor start if they want to change rule execution behavior?

## Suggested Stop Points

- Stop after Task 10 if you only need product and build context.
- Stop after Task 24 if you need architecture vocabulary before coding.
- Stop after Task 47 if you need enough context for most app or UI changes.
- Stop after Task 59 if you need to work on Xposed hook behavior.
- Complete all tasks only if you need full-repo understanding.