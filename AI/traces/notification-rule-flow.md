# Notification Rule Flow

## Purpose

This document traces one concrete end-to-end path: a notification event becomes a ShortX fact, gets matched against rules, and may result in action execution.

## Scope

This trace follows the runtime path through the main `services` module.

## Entry Point

One concrete producer is `NotificationService`.

Relevant methods include:

- `registerPostedByApp(...)`
- `registerUpdatedByApp(...)`
- `registerDismissedByUser(...)`
- `registerRemovedByApp(...)`

These methods convert Android notification state into ShortX notification fact protos such as:

- `NotificationPosted`
- `NotificationUpdated`
- `NotificationRemoved`

## Step-By-Step Flow

### 1. Notification event is observed

`NotificationService` receives or reconstructs a notification event and turns it into a ShortX notification model plus a fact proto.

### 2. The event is published to the rule layer

`NotificationService` calls:

- `shortXContext.rule.publishFact(...)`

The service also passes a notification-derived context map so later conditions and actions can access data such as app, title, content, or tag.

### 3. `RuleService` accepts the fact

`RuleService.publishFact(...)` posts work to the service handler thread and delegates to:

- `engine.fire(...)`

At this point, `RuleService` supplies:

- the incoming notification fact
- all enabled rules
- all direct actions
- lazy initial context data

### 4. `DefaultRuleEngine` begins matching

`DefaultRuleEngine.fire(...)` first checks global runtime conditions:

- whether rule execution is enabled
- whether the system is considered ready

If those checks pass, it iterates through rules and hands the notification fact to `FactMatcherImpl`.

### 5. `FactMatcherImpl` evaluates notification rules

The matcher determines whether the incoming `NotificationPosted` or related notification fact matches the rule's configured fact list.

Notification matching may depend on:

- package or app selection
- title/content text or regex
- notification tag
- other fact-specific rule fields

### 6. Context is built for a matching rule

When a rule matches, the engine builds:

- `EvaluateContext`
- `ContextData`
- `LocalVars`
- parameter inputs from the rule definition

This is the runtime state that downstream conditions and actions will consume.

### 7. Conditions are evaluated

If the rule has conditions, `ConditionEvaluatorImpl` evaluates them with the current notification-derived context.

Condition composition can be:

- `ALL`
- `ANY`
- `NONE`
- MVEL-driven boolean composition

### 8. Conflict policy is applied

Before actions run, the engine checks whether another instance of the same rule is active and applies the rule conflict strategy:

- execute both
- replace old
- skip new

### 9. The action list is executed

If conditions pass, the engine calls `runActionsJobLocked(...)`, which then routes into:

- `realRunActionsInAsyncMode(...)`, or
- `realRunActionsInSyncMode(...)`

Each action is evaluated by `ActionEvaluatorImpl`, which resolves the action proto to a concrete actor and executes it.

### 10. Results are recorded

`RuleService` records:

- fact publication
- condition evaluation
- action evaluation
- job lifecycle events

This makes the notification-driven rule execution path observable from UI and diagnostics.

## Main Files

- `services/src/main/java/tornaco/apps/shortx/services/notification/NotificationService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/RuleService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/DefaultRuleEngine.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/fact/FactMatcherImpl.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/cond/ConditionEvaluatorImpl.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/actor/ActionEvaluatorImpl.kt`

## Debugging Checklist

If a notification rule does not fire, check these in order:

1. whether the notification event reached `NotificationService`
2. whether the correct notification fact proto was published
3. whether `RuleService.publishFact(...)` ran after system ready
4. whether the rule's fact definition actually matches the incoming notification fields
5. whether conditions rejected the run
6. whether conflict policy suppressed the new execution
