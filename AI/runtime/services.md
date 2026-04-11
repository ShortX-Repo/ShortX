# services

## Role

`services` is the main runtime engine of ShortX. It hosts the rule engine, action execution, scripting support, automation services, overlay/service integration, and most of the Android-facing operational logic.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Depends on `core`, `core-api`, all `services-*` version modules, and several external libraries including MVEL, jsoup, zxing, and scripting/runtime helpers.
- Uses compile-only Android framework jars plus packaged jars from `services-ui` and `services-jni`.
- Acts as the compatibility aggregator for Android 28 through 35.

## Key Source Areas

- `services/src/main/java/tornaco/apps/shortx/services/rule/`
- `services/src/main/java/tornaco/apps/shortx/services/wm/`
- `services/src/main/java/tornaco/apps/shortx/services/overlay/`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/`
- `services/src/main/java/tornaco/apps/shortx/services/app/`

## Important Entry Points

- `ShortXContext.kt`: service locator for runtime subsystems.
- `DefaultRuleEngine.kt`: core rule evaluation path.
- `WindowManagerService.kt`: view and automation integration.
- `OverlayService.kt`: runtime overlay behavior.
- Action actor packages under `rule/actor/`: concrete action execution logic.

## How It Connects

This module is the operational center of the product. UI and patch layers eventually rely on it to execute actions, evaluate rules, expose runtime services, and bridge to Android-version-specific helpers.

## Rule Execution Path

This is the core runtime path for automatic rules inside `services`.

### 1. A system-facing service detects an event

Specialized services produce facts when Android state changes.

Examples:

- `NotificationService` publishes `NotificationPosted`, `NotificationUpdated`, and `NotificationRemoved`.
- network, media, input, audio, and trigger packages publish their own fact protos.
- trigger implementations under `services/rule/fact/trigger/` call into `shortXContext.rule.publishFact(...)` or `publishFactChecked(...)`.

This means the rule engine does not poll for everything itself. Other services detect state changes and push facts into `RuleService`.

### 2. `RuleService` is the fact ingress point

`RuleService` is the runtime coordinator for rule storage, trigger registration, fact publication, job management, and engine creation.

Important responsibilities:

- builds and owns the engine instance through `RuleEngineFactory.newRuleEngine(...)`
- registers all triggers during `systemReady()`
- stores and exposes enabled rules and direct actions
- receives facts through `publishFact(...)` and `publishFactChecked(...)`
- records fact publication, condition evaluation, and action evaluation results

### 3. `FactPublishInterceptor` filters unnecessary facts

Before publishing, `publishFactChecked(...)` asks `FactPublishInterceptor` whether a fact type is currently relevant.

The interceptor builds a cache of enabled proto type URLs by scanning:

- all rules
- all direct actions
- all toggles

If a fact type is not referenced anywhere and is not one of the always-published facts like `AnyFact` or `SystemReady`, publication can be skipped early.

This is an important performance optimization: many Android signals exist, but only a subset matter for the currently configured automation set.

### 4. `RuleService.publishFact(...)` hands the fact to the engine

`publishFact(...)` posts the work onto the service handler thread and then calls:

- `engine.fire(fact, rules, das, initialContextDataLazy)`

At this stage, `RuleService` supplies:

- the current fact proto
- all enabled rules
- all direct actions
- lazily built initial context data for the event

If any automation matched, publication is recorded through `FactPublishRecorder`.

### 5. `RuleEngineFactory` assembles the execution stack

`RuleEngineFactory` wires three collaborators into `DefaultRuleEngine`:

- `FactMatcherImpl`
- `ConditionEvaluatorImpl`
- `ActionEvaluatorImpl`

That composition is the core architecture of rule execution:

- fact matching decides whether the event matches the configured trigger facts
- condition evaluation decides whether extra conditions pass
- action evaluation executes the selected action models

### 6. `DefaultRuleEngine.fire(...)` matches facts to rules

`DefaultRuleEngine.fire(...)` is the main runtime entry for automatic execution.

Its high-level logic is:

1. reject execution if the rule feature is disabled
2. reject most facts until `SystemReady` has been published
3. loop through enabled rules
4. use `factMatcher.match(fact, rule.factsList)` to determine whether the rule's facts match
5. if matched, build context and evaluate the rule condition set
6. if conditions pass, run the rule actions
7. if the main rule facts do not match, also check rule quit facts when enabled
8. separately evaluate direct-action quit flows

So the engine handles both normal rule execution and quit/stop logic in the same central component.

### 7. `FactMatcherImpl` matches the incoming fact against rule facts

`FactMatcherImpl` contains a large registry of per-fact-type matching logic.

Examples of matching behavior include:

- comparing key codes for key events
- matching app/package sets for app foreground/background facts
- matching activity component lists for activity lifecycle facts
- matching status enums for wifi, bluetooth, NFC, and similar state changes

The matcher is not a generic reflection-only compare. It uses type-specific logic for many fact classes so rule facts behave semantically rather than as raw protobuf equality checks.

### 8. The engine builds `EvaluateContext` and runtime data

Once a rule matches, the engine constructs an `EvaluateContext` containing:

- source id
- source event type
- source title
- a serial number

It also prepares:

- `ContextData` from the incoming event payload
- `LocalVars` for per-run temporary data
- parameter inputs derived from rule parameters

If the fact match produced a fact tag, the engine stores that in context data as well.

This is the data that later conditions and actions read from.

### 9. Conflict strategy is checked before actions run

Before starting a new job, `DefaultRuleEngine` checks whether another job for the same rule instance is already running.

It then applies the rule's conflict strategy:

- execute both
- replace old
- skip new

This is how ShortX controls repeated triggers while an earlier run is still active.

### 10. `ConditionEvaluatorImpl` evaluates enabled conditions

If a rule has conditions, the engine calls `conditionEvaluator.evaluate(...)` with:

- the `EvaluateContext`
- the configured condition operator
- the list of conditions
- current `ContextData`
- `LocalVars`
- parameter inputs

`ConditionEvaluatorImpl` does two important things:

- filters out disabled conditions
- evaluates conditions according to `ALL`, `ANY`, `NONE`, or MVEL-based boolean composition

Single condition evaluation is delegated to prebuilt condition implementations through `prebuiltCondFor(...)`.

### 11. `runActionsJobLocked(...)` enforces execution policy

If conditions pass, the engine calls `runActionsJobLocked(...)`.

This method applies the rule-level async policy:

- async: run immediately without extra locking
- sync in rule: lock per rule instance through a rule-specific mutex
- global sync: serialize through a global mutex

This is separate from action-level async mode. One policy controls rule/job concurrency, and the other controls how actions inside the job are executed.

### 12. `realRunActions(...)` executes the action list

The engine then chooses one of two action execution modes:

- `realRunActionsInAsyncMode(...)`: launches each action concurrently and awaits all
- `realRunActionsInSyncMode(...)`: executes actions in order and can stop on failure or break

In sync mode:

- disabled actions are skipped
- `Fail` results may stop execution unless `shouldContinue` is true
- `Break` results stop the current action chain immediately

### 13. `ActionEvaluatorImpl` dispatches each action to an actor

Each action proto is resolved to a prebuilt actor with `prebuiltActorFor(action)`.

For each action evaluation, `ActionEvaluatorImpl`:

1. unpacks the proto into the expected action model
2. extracts common action attributes like id, disabled state, and error policy
3. optionally acquires a wakelock if the actor requires it
4. calls `actor.handle(...)` with the full execution environment
5. converts the result into `Success`, `Fail`, `Break`, or `Disabled`
6. notifies listeners and recorders on completion

This is the point where abstract action models become concrete behavior.

### 14. Observability is built into the path

`RuleService` wires listeners and recorders around the engine so the system can track:

- job start/completion/cancellation
- action evaluation start/completion
- condition evaluation results
- fact publication records

That means rule execution is designed to be observable by the app UI and diagnostics, not just fire-and-forget.

## Concrete Example

One real path looks like this:

1. `NotificationService.registerPostedByApp(...)` receives a notification event.
2. It calls `shortXContext.rule.publishFact(...)` with `NotificationPosted` and a context map.
3. `RuleService.publishFact(...)` posts the event to the handler and calls `engine.fire(...)`.
4. `DefaultRuleEngine.fire(...)` loops through enabled rules and uses `FactMatcherImpl` to check `NotificationPosted` against each rule's configured facts.
5. For each matching rule, it builds `EvaluateContext`, `ContextData`, `LocalVars`, and parameter inputs.
6. `ConditionEvaluatorImpl` evaluates the rule's condition list.
7. If conditions pass, `runActionsJobLocked(...)` selects the execution policy.
8. `ActionEvaluatorImpl` dispatches each action to its actor implementation.
9. `RuleService` records the fact publication and action/condition results for listeners and history.

## Quit Execution Path

The same engine also handles quit logic for both rules and direct actions.

### Rule quit flow

When an incoming fact does not match a rule's normal trigger facts, `DefaultRuleEngine.fire(...)` still checks whether that rule has quit logic enabled.

The high-level flow is:

1. normal rule fact matching fails
2. engine checks `rule.quit.isEnabled`
3. engine checks the quit evaluation policy for that rule
4. `factMatcher.match(...)` evaluates the incoming fact against `rule.quit.factsList`
5. if matched, the engine builds a quit `EvaluateContext` using `ContextSrc_RuleQuit`
6. quit conditions are evaluated through `ConditionEvaluatorImpl`
7. if quit conditions pass, existing running jobs for the same rule instance are cancelled
8. quit actions are executed through `runActionsJobLocked(...)`
9. the rule id is removed from the one-time quit evaluation tracking set when appropriate

This means quit logic is not a separate subsystem. It is a second branch inside the main engine, evaluated only after normal rule matching fails.

### Direct-action quit flow

Direct actions also support quit logic, and the engine handles them after processing rules.

The flow is:

1. engine filters direct actions that have quit enabled
2. it applies direct-action quit evaluation policy filtering
3. for each candidate DA, it matches the incoming fact against `da.quit.factsList`
4. if matched, it builds an `EvaluateContext` using `ContextSrc_DirectActionQuit`
5. it evaluates quit conditions
6. it cancels existing matching jobs when required
7. it runs the quit action list through the same action execution path used by normal jobs

So rule quit and direct-action quit share most of the same primitives:

- fact matching
- condition evaluation
- job cancellation
- action execution
- quit-evaluation policy tracking

### Why this matters

If you are debugging why a task stops, restarts, or runs cleanup actions unexpectedly, you usually need to inspect:

- the incoming fact type
- the rule or DA quit facts
- the quit evaluation policy
- the current active jobs for that source id

This is the main control path for automation shutdown and cleanup behavior.

## Key Files For This Trace

- `services/src/main/java/tornaco/apps/shortx/services/rule/RuleService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/DefaultRuleEngine.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/RuleEngine.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/fact/FactMatcherImpl.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/cond/ConditionEvaluatorImpl.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/actor/ActionEvaluatorImpl.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/FactPublishInterceptor.kt`
- `services/src/main/java/tornaco/apps/shortx/services/notification/NotificationService.kt`

## What To Read Next Inside `services`

After this trace, the next high-value deep reads are:

- `services/rule/actor/` to understand how specific actions are implemented
- `services/rule/fact/trigger/` to understand fact production sources
- `services/wm/` for UI automation and view search flows
- `services/overlay/` for runtime overlay behavior

## Reading Outcome

If you want to change rule execution behavior, this is the first implementation module to read deeply.
