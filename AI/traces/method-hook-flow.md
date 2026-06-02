# Method Hook Flow

## Purpose

This document traces one advanced end-to-end path: a user-defined Method Hook fact becomes a live Xposed hook, and a hooked method call is turned back into a ShortX fact that can trigger rules or other automation.

## Scope

This trace spans:

- `ui` for hook configuration origin
- `core-api` for IPC bridge APIs
- `patch-xposed` for hook installation and callback capture
- `services` for fact publication and rule execution

## High-Level Idea

ShortX lets a user define a `MethodHook` fact in a rule. At runtime, the Xposed path scans rule facts, installs matching hooks into the target process, evaluates optional MVEL expressions against `ShortXHookParam`, and sends the resulting context back into the main rule engine as a `MethodHook` fact.

## Configuration Origin

The UI side includes method-hook selector flows under `ui`, such as:

- `ui/.../MethodHookSelectorViewModel.kt`
- `ui/.../MethodHookSelectorActivity.kt`

These build a `MethodHook` fact model containing fields such as:

- package name
- class name
- method name
- lifecycle
- before or after hook mode
- optional parameter/return constraints
- MVEL expressions whose outputs become context variables

## Hook Installation Flow

### 1. Xposed entry points are invoked

The Xposed path starts from entry classes such as:

- `XposedEntry.kt`
- `ShortXLSPosedHook100Entry.kt`
- `ShortXLSPosedHook101Entry.kt`

These initialize `XposedRuntime` with a concrete runtime adapter and then route into `HookStub2`.

### 2. `HookStub2` chooses the lifecycle branch

`HookStub2` drives two relevant method-hook installation paths:

- `initZygote()` -> `CustomHookApp.hookApp(..., MethodHookLifecycle.InitZygote)`
- `packageLoaded(...)` -> `CustomHookApp.hookApp(..., MethodHookLifecycle.LoadedPackage)` for non-SystemUI packages
- `systemServerLoaded(...)` -> `CustomHookSystemServer.hookAndroidSystem(...)`

This maps the user-selected hook lifecycle to actual Xposed lifecycle points.

### 3. `CustomHookUtil.installHookForRules(...)` scans configured rules

Both `CustomHookApp` and `CustomHookSystemServer` call:

- `CustomHookUtil.installHookForRules(...)`

That function scans:

- enabled rules
- direct-action quit facts

It looks for facts that are `MethodHook` protos, then filters them by:

- current package name
- lifecycle

### 4. The hook target method is resolved

For each candidate `MethodHook`, `CustomHookUtil`:

1. finds the target class through the provided class loader
2. filters methods by method name
3. applies extra constraints such as parameter count, parameter types, or return type
4. selects the first matching method

### 5. Xposed hook registration happens

Once a target method is chosen, `CustomHookUtil` installs either:

- a before hook, or
- an after hook

This is controlled by the `beforeMethod` field in the `MethodHook` proto.

## Hook Callback Flow

### 6. The hooked method is called in the target process

When the target method runs, the runtime-specific Xposed hook callback is normalized into `ShortXHookParam`.

That normalized object exposes the runtime values users care about:

- `param.args`
- `param.result`
- `param.thisObject`

The runtime-specific registration mechanism is different per module:

- `patch-legacy` uses legacy `XC_MethodHook`
- `patch-100` uses API 100 `@XposedHooker` with `@BeforeInvocation` / `@AfterInvocation`
- `patch-101` uses chain-based `Hooker.intercept(...)`

### 7. MVEL expressions are evaluated against the hook param

`CustomHookUtil.compileExpressions(...)` evaluates the user-defined expression list against a variable map containing at least:

- `param`
- `helper`

Each expression produces a `StringPair` whose key becomes a context variable name and whose value becomes the runtime value passed back into ShortX.

### 8. Hook result is bridged back to ShortX

There are two main branches.

#### App process branch

`CustomHookApp` sends the hook event through:

- `shortXManager.onHookedMethodCalled(hook, contextDataList)`

That call is bridged through `ShortXManager` into the `IShortX` binder API.

#### System server branch

`CustomHookSystemServer` publishes directly through:

- `ShortXService.shortXContext.rule.publishFactChecked(...)`

In both branches, the resulting fact type is still `MethodHook`, and the compiled expression results become context data.

### 9. Binder entry re-publishes the fact inside the services runtime

On the services side, `ShortXService` implements:

- `onHookedMethodCalled(...)`

That method parses the `MethodHook` and its context pairs, then calls:

- `shortXContext.rule.publishFact("App hook", MethodHook.newBuilder(hook).build()) { ... }`

This turns the Xposed callback back into a normal ShortX fact publication event.

### 10. The standard rule engine takes over

From that point onward, the path is the normal rule execution path:

1. `RuleService.publishFact(...)`
2. `DefaultRuleEngine.fire(...)`
3. `FactMatcherImpl` matches the `MethodHook` fact against rules
4. conditions are evaluated
5. actions are executed

So method hooks are not a separate action engine. They are a specialized fact source feeding the same rule runtime as notifications, package events, or system state changes.

## Main Files

- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/HookStub2.kt`
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookSystemServer.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookApp.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookUtil.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedAdapter.kt`
- `patch-xposed/patch-100/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Api100XposedAdapter.kt`
- `patch-xposed/patch-101/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Api101XposedAdapter.kt`
- `patch-xposed/patch-legacy/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/LegacyXposedAdapter.kt`
- `core-api/src/main/java/tornaco/apps/shortx/core/ShortXManager.kt`
- `core-api/src/main/java/tornaco/apps/shortx/core/IShortX.aidl`
- `services/src/main/java/tornaco/apps/shortx/services/ShortXService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/RuleService.kt`

## Key Architecture Insight

The method-hook feature is best understood as:

- user-configured hook definition in rule facts
- runtime-specific Xposed hook installer in target processes
- normalization into `ShortXHookParam`
- binder or direct callback bridge into `services`
- standard fact-driven rule execution after that

That design keeps the hook path integrated with the main rule engine instead of creating a completely separate automation runtime.

## Debugging Checklist

If a method hook does not trigger automation, check these in order:

1. whether the hook fact exists in an enabled rule
2. whether package name and lifecycle match the target process
3. whether the target class and method were found
4. whether method constraints filtered out the intended overload
5. whether the callback actually ran in before or after mode
6. whether MVEL expressions evaluated successfully
7. whether `onHookedMethodCalled(...)` reached the ShortX service binder
8. whether the resulting `MethodHook` fact matched the intended rule
