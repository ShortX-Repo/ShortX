# Feature Specification: Continuous Adjustment Actions

**Feature Branch**: `feature/007-continuous-adjustment`  
**Created**: 2026-04-30  
**Status**: Draft  
**Input**: User description: "我要新增连续调节功能，包括UI、proto、手势检测等一系列完整功能；你必须参考AI下面的技术文档了解完整开发流程。"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Continuous Brightness Adjustment (Priority: P1)

As a user, I want to swipe from an edge and drag to continuously adjust screen brightness, so that I can fine-tune brightness without opening settings.

**Why this priority**: Brightness adjustment is the most common continuous adjustment use case and demonstrates the core pattern.

**Independent Test**: Configure a continuous brightness adjustment action on an edge, swipe and drag, verify brightness changes continuously.

**Acceptance Scenarios**:

1. **Given** a continuous brightness action is configured on the right edge, **When** the user swipes left from the right edge and holds, **Then** brightness adjustment mode activates and dragging up/down changes brightness.
2. **Given** brightness adjustment is active, **When** the user moves their finger up, **Then** brightness increases proportionally.
3. **Given** brightness adjustment is active, **When** the user moves their finger down, **Then** brightness decreases proportionally.
4. **Given** brightness adjustment is active, **When** the user lifts their finger, **Then** the adjustment is finalized and the overlay disappears.

---

### User Story 2 - Continuous Volume Adjustment (Priority: P1)

As a user, I want to swipe from an edge and drag to continuously adjust volume, so that I can fine-tune volume without pressing hardware buttons.

**Why this priority**: Volume adjustment is another primary use case for continuous adjustment.

**Independent Test**: Configure a continuous volume action on an edge, swipe and drag, verify volume changes continuously.

**Acceptance Scenarios**:

1. **Given** a continuous volume action is configured, **When** the user performs the gesture, **Then** volume adjustment mode activates.
2. **Given** volume adjustment is active, **When** the user drags, **Then** volume changes in real-time.
3. **Given** volume adjustment is active, **When** the user lifts their finger, **Then** the adjustment is finalized.

---

### User Story 3 - Visual Feedback During Adjustment (Priority: P2)

As a user, I want to see a visual overlay showing the current adjustment level during continuous adjustment, so that I can see the effect of my gesture in real-time.

**Why this priority**: Visual feedback is essential for usability but the core adjustment works without it.

**Independent Test**: Perform a continuous adjustment, observe the overlay shows the current level.

**Acceptance Scenarios**:

1. **Given** continuous adjustment is active, **When** the user drags, **Then** an overlay appears showing the current value (brightness percentage or volume level).
2. **Given** the overlay is visible, **When** the value changes, **Then** the overlay updates in real-time.
3. **Given** the adjustment is finalized, **When** the user lifts their finger, **Then** the overlay fades out.

---

### User Story 4 - Configuration in Rule Editor (Priority: P2)

As a user, I want to configure continuous adjustment actions in the rule editor, so that I can bind brightness or volume adjustment to specific edge gestures.

**Why this priority**: Without configuration UI, users can't set up continuous adjustment rules.

**Independent Test**: Open rule editor, add a continuous brightness action, configure it, verify it works.

**Acceptance Scenarios**:

1. **Given** the user opens the rule editor, **When** they add a new action, **Then** continuous adjustment options (brightness, volume) are available.
2. **Given** the user selects continuous brightness, **When** they save the rule, **Then** the rule is saved with the continuous adjustment action.
3. **Given** a continuous adjustment rule is configured, **When** the user performs the gesture, **Then** the continuous adjustment activates.

---

### User Story 5 - Cancel and Revert (Priority: P3)

As a user, when I cancel a continuous adjustment gesture (e.g., multi-touch), the system should revert to the original value.

**Why this priority**: Cancel/revert is important for correctness but rarely triggered.

**Independent Test**: Start a continuous adjustment, cancel it (e.g., second finger touch), verify value reverts.

**Acceptance Scenarios**:

1. **Given** continuous adjustment is active, **When** the gesture is cancelled, **Then** the value reverts to the pre-gesture state.
2. **Given** continuous adjustment is active, **When** a second finger touches the screen, **Then** the adjustment is cancelled and the value reverts.

---

### Edge Cases

- What happens when brightness/volume reaches min/max during adjustment?
  → The adjustment should clamp at the boundary and not wrap around.
- What happens when auto-brightness is enabled and the user adjusts brightness manually?
  → The system should disable auto-brightness when manual adjustment starts.
- What happens when the screen rotates during continuous adjustment?
  → The adjustment should be cancelled and the value reverted.
- What happens when the user starts a continuous adjustment but doesn't move?
  → The overlay should appear but the value should not change.

## Requirements *(mandatory)*

### Functional Requirements

#### Proto Definitions

- **FR-001**: System MUST define a `ContinuousAdjustBrightness` proto message for brightness adjustment actions.
- **FR-002**: System MUST define a `ContinuousAdjustVolume` proto message for volume adjustment actions.
- **FR-003**: Proto messages MUST include sensitivity scale, min/max value range, and standard action fields.

#### Gesture Detection Integration

- **FR-004**: System MUST detect continuous adjustment action types in `EdgeGestureEngine.startExecution()`.
- **FR-005**: System MUST create a `ContinuousActionHandler` when a continuous adjustment action is detected.
- **FR-006**: System MUST forward MOVE events to the continuous handler during the gesture lifecycle.
- **FR-007**: System MUST call `onFinish` when the gesture ends and `onCancel` when cancelled.

#### Brightness Control

- **FR-008**: System MUST read the current brightness value on gesture start.
- **FR-009**: System MUST adjust brightness based on finger displacement (up = brighter, down = dimmer).
- **FR-010**: System MUST clamp brightness to the configured min/max range (default 0-255).
- **FR-011**: System MUST disable auto-brightness when manual adjustment starts.

#### Volume Control

- **FR-012**: System MUST read the current volume value on gesture start.
- **FR-013**: System MUST adjust volume based on finger displacement.
- **FR-014**: System MUST clamp volume to the stream's min/max range.
- **FR-015**: System MUST support configurable stream type (music, ring, etc.).

#### Visual Feedback

- **FR-016**: System MUST display an overlay showing the current adjustment level during continuous adjustment.
- **FR-017**: System MUST update the overlay in real-time as the value changes.
- **FR-018**: System MUST fade out the overlay when the adjustment is finalized.

#### Configuration

- **FR-019**: System MUST provide UI selectors for configuring continuous brightness actions in the rule editor.
- **FR-020**: System MUST provide UI selectors for configuring continuous volume actions in the rule editor.
- **FR-021**: System MUST support sensitivity scale configuration for continuous actions.

### Key Entities

- **ContinuousAdjustBrightness**: Proto message for brightness adjustment. Key fields: sensitivityScale, minValue, maxValue.
- **ContinuousAdjustVolume**: Proto message for volume adjustment. Key fields: streamType, sensitivityScale.
- **ContinuousActionHandler**: Interface for continuous adjustment lifecycle. Methods: onStart, onMove, onFinish, onCancel.
- **BrightnessContinuousHandler**: Implementation for brightness control. Reads/writes `Settings.System.SCREEN_BRIGHTNESS`.
- **VolumeContinuousHandler**: Implementation for volume control. Uses `AudioManager.setStreamVolume`.
- **ContinuousAdjustOverlay**: System overlay view showing current adjustment level.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can adjust brightness by swiping from an edge and dragging, with the value changing in real-time.
- **SC-002**: Users can adjust volume by swiping from an edge and dragging, with the value changing in real-time.
- **SC-003**: Visual overlay appears within one frame of the adjustment starting and updates smoothly.
- **SC-004**: Cancelled adjustments revert to the original value without any residual change.
- **SC-005**: Users can configure continuous adjustment actions in the rule editor.

## Assumptions

- `ContinuousActionHandler` interface is already defined.
- `BrightnessContinuousHandler` and `VolumeContinuousHandler` exist as skeletons (log only).
- `GestureBridgeImpl.createContinuousActionHandler()` factory exists but is never called.
- `EdgeGestureEngine` has continuous handler lifecycle code (fields + calls) but the handler is never populated.
- The one-shot `SetBrightness` and `SetVolume` actors exist and can be referenced for system API usage.
- The `GestureHintView` and `GestureHintController` can be extended or a new overlay can be created.
- The rule editor UI exists and can be extended with new action type selectors.
