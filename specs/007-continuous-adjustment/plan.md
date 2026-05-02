# Implementation Plan: Continuous Adjustment Actions

**Branch**: `feature/007-continuous-adjustment` | **Date**: 2026-04-30 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/007-continuous-adjustment/spec.md`

## Summary

Implement a complete continuous adjustment system for edge gestures, enabling users to swipe from an edge and drag to continuously adjust brightness or volume. This feature spans proto definitions, gesture detection integration, real system API control, visual feedback overlay, and rule editor UI.

Key changes:
- Define proto messages for `ContinuousAdjustBrightness` and `ContinuousAdjustVolume`
- Modify `EdgeGestureEngine.startExecution()` to detect continuous action types and create handlers
- Implement real brightness control in `BrightnessContinuousHandler` (read/write `Settings.System.SCREEN_BRIGHTNESS`)
- Implement real volume control in `VolumeContinuousHandler` (use `AudioManager.setStreamVolume`)
- Create a continuous adjustment overlay UI showing current level
- Add UI selectors in the rule editor for configuring continuous actions

## Technical Context

**Language/Version**: Kotlin (primary), Java (legacy interop), targeting JVM/Android  
**Primary Dependencies**: Android SDK (Settings.System, AudioManager, WindowManager), Protobuf (core-api Actions.proto), Xposed framework  
**Storage**: Protobuf-based configuration via `ShortXManager` (AIDL binder service)  
**Testing**: JUnit 5 + Kotest + MockK  
**Target Platform**: Android 10+ (API 29+)  
**Project Type**: Android system service module  
**Performance Goals**: Real-time adjustment with no perceptible lag  
**Constraints**: Must work in privileged system context; must not break existing one-shot actions  
**Scale/Scope**: ~10 files modified/created, ~500 lines of new code

## Constitution Check

The project constitution is a template (not filled in). No gates to evaluate.

## Project Structure

### Source Code

```text
core-api/src/main/proto/shortx/
├── Actions.proto                           # MODIFY: Add ContinuousAdjustBrightness, ContinuousAdjustVolume

services/src/main/java/tornaco/apps/shortx/services/gesture/
├── bpok/gesture/
│   ├── EdgeGestureEngine.kt                # MODIFY: Detect continuous actions in startExecution()
│   ├── GestureBridge.kt                    # MODIFY: Add createContinuousActionHandler() to interface
│   ├── GestureBridgeImpl.kt                # MODIFY: Implement createContinuousActionHandler() with real logic
│   ├── ContinuousActionHandler.kt          # PRESERVE: Interface already correct
│   └── handlers/
│       ├── BrightnessContinuousHandler.kt  # MODIFY: Implement real brightness control
│       └── VolumeContinuousHandler.kt      # MODIFY: Implement real volume control

services-ui/src/main/java/tornaco/apps/shortx/ui/
├── overlay/
│   └── ContinuousAdjustOverlayView.kt      # NEW: System overlay for adjustment feedback

ui/src/main/java/tornaco/apps/shortx/ui/
├── selector/
│   ├── ContinuousBrightnessSelectDialog.kt # NEW: Rule editor selector for brightness
│   └── ContinuousVolumeSelectDialog.kt     # NEW: Rule editor selector for volume
```

## Implementation Phases

### Phase 1: Proto Definitions

**Goal**: Define proto messages for continuous adjustment actions.

#### Step 1.1: Add ContinuousAdjustBrightness to Actions.proto

```protobuf
message ContinuousAdjustBrightness {
  float sensitivityScale = 1;
  int32 minValue = 2;
  int32 maxValue = 3;
  // Standard action fields...
}
```

#### Step 1.2: Add ContinuousAdjustVolume to Actions.proto

```protobuf
message ContinuousAdjustVolume {
  int32 streamType = 1;
  float sensitivityScale = 2;
  // Standard action fields...
}
```

### Phase 2: Gesture Detection Integration

**Goal**: Wire continuous action detection into the engine.

#### Step 2.1: Modify EdgeGestureEngine.startExecution()

In `startExecution()`, after resolving the action, check if it's a continuous adjustment type. If so:
- Create a `ContinuousActionHandler` via `bridge.createContinuousActionHandler()`
- Store it in the `continuousHandler` field
- Call `continuousHandler.onStart()`
- Do NOT fire the normal edge gesture fact (or fire with a "continuous start" flag)

#### Step 2.2: Update GestureBridge interface

Add `createContinuousActionHandler(actionType: String): ContinuousActionHandler?` to the interface.

#### Step 2.3: Update GestureBridgeImpl

Implement the factory with real logic:
- Map proto action types to handler types
- Pass context (for Settings.System, AudioManager access)

### Phase 3: Real Brightness Control

**Goal**: Implement real brightness adjustment in BrightnessContinuousHandler.

#### Step 3.1: Read current brightness on start

Use `Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)` to read current brightness (0-255).

#### Step 3.2: Adjust brightness on move

Compute delta from finger displacement, apply to baseline, write via `Settings.System.putInt`.

#### Step 3.3: Handle auto-brightness

Check if auto-brightness is enabled. If so, disable it when manual adjustment starts.

#### Step 3.4: Clamp to range

Ensure brightness stays within min/max range (default 0-255).

### Phase 4: Real Volume Control

**Goal**: Implement real volume adjustment in VolumeContinuousHandler.

#### Step 4.1: Read current volume on start

Use `AudioManager.getStreamVolume(streamType)` to read current volume.

#### Step 4.2: Adjust volume on move

Compute delta from finger displacement, apply to baseline, set via `AudioManager.setStreamVolume`.

#### Step 4.3: Clamp to range

Use `AudioManager.getStreamMaxVolume(streamType)` for max bound.

### Phase 5: Visual Feedback Overlay

**Goal**: Create a system overlay showing current adjustment level.

#### Step 5.1: Create ContinuousAdjustOverlayView

A custom View that renders:
- A vertical progress bar
- Current value text (percentage or level)
- Icon (brightness sun or volume speaker)

#### Step 5.2: Create overlay controller

Manage overlay lifecycle (attach/detach/update) similar to `GestureHintController`.

#### Step 5.3: Wire to ContinuousActionHandler

Update handlers to call overlay methods on start/move/finish/cancel.

### Phase 6: Rule Editor UI

**Goal**: Add configuration selectors for continuous actions.

#### Step 6.1: Create ContinuousBrightnessSelectDialog

Dialog for configuring continuous brightness action with sensitivity and range settings.

#### Step 6.2: Create ContinuousVolumeSelectDialog

Dialog for configuring continuous volume action with stream type and sensitivity settings.

#### Step 6.3: Register in action selector

Add the new action types to the rule editor's action type list.

## Research

### Decision: Action type detection mechanism

**Decision**: Check the proto action type in `startExecution()` to determine if it's continuous.

**Rationale**: The current architecture resolves actions via `actionResolver.resolve()` which returns an `EdgeGesture` proto. The action type is determined by the proto message type. We need to check if the resolved action is a `ContinuousAdjustBrightness` or `ContinuousAdjustVolume` message.

**Alternative**: Add a flag to the `EdgeGesture` proto — rejected because it would require modifying the existing proto structure.

### Decision: Overlay architecture

**Decision**: Create a new `ContinuousAdjustOverlayView` similar to `GestureHintView`, managed by a controller similar to `GestureHintController`.

**Rationale**: The existing hint system is designed for gesture detection feedback, not continuous adjustment feedback. A separate overlay provides cleaner separation and allows different visual design (slider vs circle).

**Alternative**: Extend `GestureHintView` — rejected because the visual design is fundamentally different.

### Decision: Handler receives context

**Decision**: Pass `Context` to handlers via the factory method for system API access.

**Rationale**: Handlers need `ContentResolver` (for Settings.System) and `AudioManager`. These are obtained from `Context`.

**Alternative**: Pass individual system service references — rejected because it's less flexible.

## Contracts

### GestureBridge (modified)

```kotlin
interface GestureBridge {
    // Existing methods...
    
    // NEW: Create continuous action handler
    fun createContinuousActionHandler(actionType: String): ContinuousActionHandler?
}
```

### EdgeGestureEngine.startExecution() (modified)

```kotlin
private fun startExecution(action: Any?, direction: Direction, eventTime: Long, currentX: Float, currentY: Float) {
    // ... existing logic ...
    
    // NEW: Check if action is continuous
    if (action is ContinuousAdjustBrightness) {
        continuousHandler = bridge.createContinuousActionHandler("brightness")
        continuousHandler?.onStart(edgeGroup, downX, downY, eventTime)
        executionStarted = true
        consumeCachedEvent()
        return
    }
    
    // ... existing one-shot logic ...
}
```

### BrightnessContinuousHandler (modified)

```kotlin
class BrightnessContinuousHandler(private val context: Context) : ContinuousActionHandler {
    private var startY = 0f
    private var startBrightness = 0
    private var autoBrightnessWasEnabled = false
    
    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        startY = y
        startBrightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        autoBrightnessWasEnabled = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        if (autoBrightnessWasEnabled) {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        }
    }
    
    override fun onMove(x: Float, y: Float, time: Long) {
        val dy = y - startY
        val delta = (-dy / 500f * 255f).toInt()
        val newBrightness = (startBrightness + delta).coerceIn(0, 255)
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, newBrightness)
    }
    
    override fun onFinish(x: Float, y: Float, time: Long) {
        // Final value already set by last onMove
    }
    
    override fun onCancel() {
        // Revert to original brightness
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, startBrightness)
        if (autoBrightnessWasEnabled) {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
        }
    }
}
```

## Quickstart

### Prerequisites
- `ContinuousActionHandler` interface already defined
- `BrightnessContinuousHandler` and `VolumeContinuousHandler` exist as skeletons
- `GestureBridgeImpl.createContinuousActionHandler()` factory exists
- `EdgeGestureEngine` has continuous handler lifecycle code

### Verification
1. Define proto messages in Actions.proto
2. Implement real brightness control in BrightnessContinuousHandler
3. Wire detection in EdgeGestureEngine.startExecution()
4. Configure a continuous brightness action in rule editor
5. Swipe from edge and drag — verify brightness changes
6. Cancel gesture — verify brightness reverts

### Running Tests
```bash
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.*"
```
