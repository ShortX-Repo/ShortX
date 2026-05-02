# Data Model: Continuous Adjustment Actions

**Date**: 2026-04-30  
**Feature**: Continuous Adjustment Actions

## New Entities

### ContinuousAdjustBrightness

Proto message for brightness adjustment actions.

| Field | Type | Description |
|-------|------|-------------|
| sensitivityScale | Float | Adjustment speed multiplier (default 1.0) |
| minValue | Int | Minimum brightness value (default 0) |
| maxValue | Int | Maximum brightness value (default 255) |
| id | String | Action identifier |
| note | String | User note |
| isDisabled | Boolean | Whether action is disabled |

### ContinuousAdjustVolume

Proto message for volume adjustment actions.

| Field | Type | Description |
|-------|------|-------------|
| streamType | Int | Audio stream type (0=music, 1=ring, 2=alarm, 3=notification) |
| sensitivityScale | Float | Adjustment speed multiplier (default 1.0) |
| id | String | Action identifier |
| note | String | User note |
| isDisabled | Boolean | Whether action is disabled |

## Modified Entities

### BrightnessContinuousHandler

Implementation for brightness control.

| Field | Type | Description |
|-------|------|-------------|
| context | Context | Android context for system API access |
| startY | Float | Initial Y position on gesture start |
| startBrightness | Int | Brightness value when gesture started |
| autoBrightnessWasEnabled | Boolean | Whether auto-brightness was enabled before adjustment |
| sensitivityScale | Float | Adjustment speed multiplier |
| minValue | Int | Minimum brightness value |
| maxValue | Int | Maximum brightness value |

### VolumeContinuousHandler

Implementation for volume control.

| Field | Type | Description |
|-------|------|-------------|
| context | Context | Android context for system API access |
| startY | Float | Initial Y position on gesture start |
| startVolume | Int | Volume value when gesture started |
| streamType | Int | Audio stream type |
| maxVolume | Int | Maximum volume for the stream |
| sensitivityScale | Float | Adjustment speed multiplier |

## State Transitions

### ContinuousActionHandler Lifecycle

```
IDLE → ACTIVE (on onStart)
ACTIVE → ACTIVE (on onMove - value updates)
ACTIVE → FINALIZED (on onFinish)
ACTIVE → REVERTED (on onCancel)
FINALIZED → IDLE (cleanup)
REVERTED → IDLE (cleanup)
```

## Validation Rules

1. sensitivityScale must be > 0
2. minValue must be >= 0 and < maxValue
3. maxValue must be <= 255 (for brightness) or stream max (for volume)
4. streamType must be a valid AudioManager stream type

## Derived Data

- **Brightness percentage**: `(brightness - minValue) / (maxValue - minValue) * 100`
- **Volume percentage**: `(volume - 0) / maxVolume * 100`
- **Adjustment delta**: `(currentY - startY) * sensitivityScale * (maxValue - minValue) / screenHeight`
