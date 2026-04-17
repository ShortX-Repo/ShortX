# ShortX Edge Gesture FilterInput Migration Plan

## 1. Goal

Migrate ShortX edge gesture interception from the current SystemUI-side InputMonitor path to an InputManagerService filterInputEvent hook path in the android process, so edge gestures can:

1. Observe a broader set of global touch streams.
2. Consume events before they reach app targets.
3. Replay buffered events back to the system when the gesture is rejected or cancelled.
4. Keep the existing ShortX rule engine, proto model, and edge child-step semantics.

This plan is based on:

1. External reference design in [AI/EDGE_GESTURE_TECHNICAL_DOC.md](AI/EDGE_GESTURE_TECHNICAL_DOC.md).
2. Current ShortX system-server hook entry in [patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedEntry.kt](patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedEntry.kt).
3. Current InputManagerService hook holder in [patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/InputManagerHook.kt](patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/InputManagerHook.kt).
4. Existing placeholder input filter installer in [services/src/main/java/tornaco/apps/shortx/services/input/InputFilterInstaller.kt](services/src/main/java/tornaco/apps/shortx/services/input/InputFilterInstaller.kt).
5. Existing SystemUI gesture runtime in [patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/Detector.kt](patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/Detector.kt) and [patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/EdgeGestureSequenceDetector.kt](patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/EdgeGestureSequenceDetector.kt).

## 2. Why Migrate

The current SystemUI InputMonitor architecture is useful but has hard limits:

1. It only sees the streams that reach the monitored InputChannel path.
2. It relies on pilferPointers after detection has already progressed.
3. Some gestures cannot be intercepted globally or early enough.
4. Failure recovery is weaker because the current design is not built around full event buffering and replay.

The IMS filterInputEvent path is better suited when the goal is full-stream interception plus replay.

## 3. Current Reusable Assets In ShortX

ShortX already has several pieces needed for this migration.

### 3.1 System-server entry already exists

ShortX already hooks the android package and runs system-server hooks from [patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedEntry.kt](patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedEntry.kt).

This means the migration does not need a new Xposed entry model.

### 3.2 InputManagerService instance is already captured

[patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/InputManagerHook.kt](patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/InputManagerHook.kt) already caches the live InputManagerService instance into ServiceHolder.

This is enough to:

1. Install an input filter.
2. Access filter host when available.
3. Re-inject events through native or host-backed APIs.

### 3.3 Input filter scaffolding already exists

[services/src/main/java/tornaco/apps/shortx/services/input/InputFilterInstaller.kt](services/src/main/java/tornaco/apps/shortx/services/input/InputFilterInstaller.kt) already contains:

1. An installer that calls setInputFilter.
2. A placeholder InputFilterImpl.

Today that implementation intentionally throws if filterInputEvent is called, so it is only a stub. But the structural seam is already present.

### 3.4 Event injection utilities already exist

[services/src/main/java/tornaco/apps/shortx/services/input/IMSFunc.kt](services/src/main/java/tornaco/apps/shortx/services/input/IMSFunc.kt) already supports:

1. InputFilterHost.sendInputEvent when host is available.
2. nativeInjectInputEvent on older Android versions.
3. mNative.injectInputEvent on Android 13+.

This is the main building block for replay.

### 3.5 Edge fact pipeline should be preserved

The existing rule pipeline is already better than the external reference implementation in one major way: it is fact-based instead of hard-wired action-slot based.

Relevant anchors:

1. [services/src/main/java/tornaco/apps/shortx/services/ShortXService.kt](services/src/main/java/tornaco/apps/shortx/services/ShortXService.kt)
2. [services/src/main/java/tornaco/apps/shortx/services/rule/fact/FactMatcherImpl.kt](services/src/main/java/tornaco/apps/shortx/services/rule/fact/FactMatcherImpl.kt)
3. [ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt](ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt)

The migration should replace the runtime interception path, not replace the rule model.

### 3.6 Historical Evidence From ShortX Commits

The old IMS path and the later SystemUI migration are visible in the repository history.

Key commits:

1. `e44bda0f3e1a2cd73a8c13aa1db6fd8304d35cad`: enabled the first SystemUI-side InputMonitor path.
2. `9cc62ded2492958527aa5cd891e2d2e257de9980`: added the first SystemUI detector and swipe listener.
3. `7ff92d2bb5bc09bad3c33a4b1a56525d105fb3c6`: moved gesture detection from `services` into `patch-systemui` and introduced a callback bridge.
4. `8eded395d1c782d5fd52b99d5978f06dd88a1726`: completed the bridge so SystemUI detection could actually fire edge gestures.
5. `a601fc1acd41ad58fcf98f8f5f3f9e04012c30d6`: removed the old motion-oriented IMS filter hook path and kept InputManagerService hooks focused on key events.

Historical findings that matter for the refactor:

1. Before `7ff92d2b`, `InputManagerHook` directly hooked `InputManagerService.filterInputEvent` and forwarded events to `ShortXService.shortXContext.gesture.filterInput(...)`.
2. The old `GestureService.filterInput(...)` did run edge-motion interception, but it was not a full buffered replay engine.
3. The old motion path used three pieces together:
	1. `filterInputEvent` entry in `InputManagerHook`.
	2. `GestureService.filterMotionEvent(...)` for edge-rect gating plus detector dispatch.
	3. `IMSFunc.injectInputEvent(...)` to inject a synthetic `ACTION_CANCEL` after ShortX decided to take over.
4. The old implementation did **not** buffer and replay the original down/move/up sequence back to the app. It mostly worked by early blocking plus synthetic cancel.
5. This means the historical IMS code is useful as a gateway/reference, but it is not yet the complete replay-capable architecture now desired.

## 4. Target Architecture

The target design should split the current gesture stack into five layers.

### 4.1 SystemInputGateway

Lives in the android process and is attached to InputManagerService.filterInputEvent.

Responsibilities:

1. Receive MotionEvent before app dispatch.
2. Filter out unsupported displays, devices, sources, and replayed events.
3. Maintain per-touch-stream buffering state.
4. Decide whether to pass through immediately, keep buffering, consume, or replay.

### 4.2 EdgeGestureEngine

Shared runtime state machine for:

1. Primary edge gesture recognition.
2. Child-step sequence recognition.
3. Hover-on-release semantics.
4. Cancel, failure, success, and replay decisions.

This should become the single source of gesture-state truth.

### 4.3 EdgeGestureRuntimeService

Configuration and fact publication bridge.

Responsibilities:

1. Read enabled edge gesture rules.
2. Resolve matching candidates.
3. Publish final runtime edge facts.
4. Expose runtime thresholds.

This is conceptually the successor of the current DetectorServiceImpl, but it must be usable from the android process instead of being tied to the SystemUI InputMonitor path.

### 4.4 InputReplayBridge

Encapsulates event replay.

Responsibilities:

1. Clone and buffer original MotionEvent sequence pieces.
2. Replay them in-order on rejection or cancellation.
3. Prefer InputFilterHost.sendInputEvent when possible.
4. Fall back to native injection when host-backed replay is unavailable.

Important historical note:

ShortX previously had `IMSFunc.injectInputEvent(...)`, but the old gesture path only used it to inject synthetic cancel in the interception handoff path. The new refactor must extend this into a true replay bridge instead of assuming the old code already solved full replay.

### 4.5 FeedbackHost

Separates interception from visualization.

Responsibilities:

1. Receive high-level gesture feedback events from EdgeGestureEngine.
2. Render progress/waiting/success/cancel UI.
3. Remain replaceable.

Important: the current hint implementation is SystemUI-oriented, so the new runtime should not make the android-process engine depend directly on the current GestureHintController.

## 5. What Should Be Reused vs Rewritten

### 5.1 Reuse

Reuse these parts with adaptation:

1. Edge fact proto and UI model.
2. Fact matching and publication path.
3. Child-step sequence semantics.
4. Hover threshold configuration.
5. Existing hint vocabulary and visual language.
6. The old IMS hook seam in historical `InputManagerHook` where `filterInputEvent` already forwarded events into ShortX.
7. The old `GestureService.filterMotionEvent(...)` idea of doing a fast edge-rect precheck before deeper detector work.
8. `IMSFunc.injectInputEvent(...)` and host/native injection fallback.

### 5.2 Rewrite

Rewrite these parts:

1. The MotionEvent interception entry path.
2. The per-stream state machine owner.
3. The current Detector and InputMonitor-specific flow.
4. The ownership boundary of DetectorServiceImpl.
5. The old `GestureService.filterMotionEvent(...)` interception contract, because it returned immediate consume/pass results and synthetic cancel, not buffered replay decisions.

### 5.3 Do Not Copy Directly

Do not directly copy these parts from the reference design:

1. The 16x7 static action-slot table.
2. The action id container model.
3. The tight coupling between state machine and action execution.

ShortX should keep its current rule-engine-based fact architecture.

## 6. Main Technical Risks

### 6.1 Replay recursion

The new filterInputEvent hook will see re-injected events unless replay is carefully marked or routed.

Mitigation:

1. Prefer host.sendInputEvent when available.
2. Add replay suppression metadata keyed by downTime, deviceId, source, and action sequence.
3. Ignore streams already known to be ShortX replay.

### 6.2 Input filter ownership conflicts

Accessibility and WindowManager may replace or toggle input filter state.

This is already visible in [services/src/main/java/tornaco/apps/shortx/services/input/InputService.kt](services/src/main/java/tornaco/apps/shortx/services/input/InputService.kt).

Mitigation:

1. Keep observing input filter enable/disable transitions.
2. Reinstall only when safe.
3. Avoid endless reinstall loops.

Historical evidence:

The old `InputService.onInputFilterStateChanged(...)` already contains stack traces showing `setInputFilterEnabled(false)` could be triggered by system components such as accessibility. This was a real issue in the previous IMS path, not a hypothetical one.

### 6.3 Feedback host relocation

Current feedback code is in the SystemUI patch path, not in the android-process runtime.

Mitigation options:

1. Preferred: keep a SystemUI-hosted feedback renderer and push high-level events to it.
2. Alternative: rebuild overlay rendering in system_server-compatible form.

The preferred option is lower risk because it reuses the existing hint code.

### 6.4 Multi-pointer and device scoping

The filter path sees all device input. If stream ownership is not strict, gestures will corrupt normal input.

Mitigation:

1. Track deviceId, displayId, source, pointerId, and downTime.
2. Only buffer one active touch stream per engine instance.
3. Cancel on pointer count changes unless explicitly supported.

### 6.5 Performance and ANR sensitivity

filterInputEvent sits on a hot system input path.

Mitigation:

1. Keep matching fast and mostly allocation-free.
2. Snapshot rules ahead of time.
3. Avoid binder calls per event.
4. Avoid logging on every move in release mode.

## 7. Recommended Migration Phases

### Phase 0: Architecture freeze

Deliverables:

1. Final runtime layer boundaries.
2. Class ownership map.
3. Replay policy definition.
4. Feedback-host decision.

### Phase 1: Build the IMS input gateway without gesture logic

Tasks:

1. Extend InputManagerHook to hook filterInputEvent.
2. Turn InputFilterImpl from stub into a real delegator.
3. Add event source filtering and loop suppression.
4. Add debug-only tracing for down/move/up stream ownership.
5. Reproduce the useful part of the historical path first: fast edge-rect gating before full detector evaluation.

Success criteria:

1. MotionEvent reaches ShortX from the android process.
2. No gesture behavior change yet.
3. No boot loop or input freeze.

### Phase 2: Extract reusable gesture core from the current SystemUI detector

Tasks:

1. Move edge-region resolution and sequence state logic behind process-agnostic interfaces.
2. Split UI feedback calls out of the engine.
3. Replace direct detector-to-hint coupling with event callbacks.

Success criteria:

1. The engine can run without SystemUI dependencies.
2. Existing unit tests for sequence behavior still pass.

### Phase 3: Add buffering and replay

Tasks:

1. Add per-stream MotionEvent buffer.
2. Keep events buffered until accept/reject decision.
3. Replay buffered sequence on rejection or cancellation.
4. Consume without replay on success.

Historical migration note:

This phase is the main point where the new design goes beyond the old IMS implementation. The previous code path injected cancel, but it did not implement full original-stream replay.

Success criteria:

1. Non-matching edge touches still reach apps correctly.
2. Matching edge gestures are fully intercepted.
3. Replay does not recurse.

### Phase 4: Port primary edge gesture recognition

Tasks:

1. Move primary edge gesture detection from the SystemUI InputMonitor path into the new engine.
2. Publish the same runtime facts as today.
3. Keep the old SystemUI path behind a feature flag for fallback.

Success criteria:

1. Primary edge gestures work from the IMS gateway.
2. Existing rule execution remains unchanged.

### Phase 5: Port child-step sequence recognition

Tasks:

1. Port EdgeGestureSequenceDetector logic into the shared engine.
2. Preserve current no-timeout waiting behavior.
3. Preserve current hover-on-release semantics.
4. Preserve single-child-step editor semantics at the UI layer.

Success criteria:

1. Existing sequence detector tests still describe the behavior.
2. Global interception path works for sequence gestures too.

### Phase 6: Reconnect feedback UI

Tasks:

1. Define a feedback event protocol from android-process engine to SystemUI renderer.
2. Adapt GestureHintController integration to the new protocol.
3. Cover progress, waiting, cancel, success, and hover waiting states.

Success criteria:

1. Users still get waiting hints and success/cancel feedback.
2. Feedback does not block input processing.

### Phase 7: Remove or demote the old InputMonitor path

Tasks:

1. Keep InputMonitor path only as debug fallback if still valuable.
2. Otherwise delete the old motion-interception path after the new one is proven stable.

Success criteria:

1. Only one production motion interception path remains.
2. Maintenance burden is reduced instead of increased.

## 8. Recommended New Classes

Suggested naming for the new structure:

1. EdgeGestureFilterGateway
2. EdgeGestureMotionStream
3. EdgeGestureReplayBuffer
4. EdgeGestureEngine
5. EdgeGestureRuntimeService
6. EdgeGestureFeedbackDispatcher
7. EdgeGestureFeedbackEvent
8. EdgeGestureFilterPolicy

Potential ownership:

1. patch-main: gateway and android-process hook glue.
2. patch-common or a new shared patch runtime package: engine, replay, interfaces.
3. patch-systemui: feedback renderer only.

## 9. Recommended First Implementation Slice

The safest first real slice is not the whole migration.

Build this first:

1. Hook filterInputEvent in InputManagerHook.
2. Log and identify MotionEvent streams.
3. Forward them into a no-op gateway.
4. Add replay-safe pass-through behavior.

Then do this second:

1. Port only primary edge gesture matching.
2. Keep child-step sequence on the old path temporarily.

Concrete historical shortcut for this slice:

1. Recreate the old `InputManagerHook -> GestureService.filterInput/filterMotionEvent -> Detector` call shape first, but behind a new gateway interface.
2. Do not bring over the old synthetic-cancel-only contract as the final design.
3. Use it only as a boot-safe stepping stone before adding true replay buffering.

This two-step rollout sharply reduces boot-risk and makes debugging practical.

## 10. Bottom-line Recommendation

ShortX should migrate to the IMS filterInputEvent architecture if the product goal is true global touch interception and replay.

However, the migration should:

1. Reuse the existing rule engine and fact model.
2. Replace the runtime interception path incrementally.
3. Split gesture engine from feedback UI.
4. Treat replay suppression and feedback-host relocation as first-class design problems.

The most important rule is this:

Do not move the current SystemUI detector into filterInputEvent unchanged.

Instead, build a new android-process input gateway and migrate the gesture engine into a shared runtime core, then reconnect SystemUI as a renderer rather than as the source of truth.