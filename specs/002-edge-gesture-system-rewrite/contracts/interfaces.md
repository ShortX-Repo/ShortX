# Interface Contracts: Edge Gesture System Rewrite

**Date**: 2026-04-30  
**Feature**: Edge Gesture System Rewrite

## EdgeGestureEngine

The core state machine that processes touch events for edge gesture recognition.

```kotlin
interface EdgeGestureEngine {
    /**
     * Main entry point. Processes a MotionEvent and determines if it's an edge gesture.
     * 
     * @param hookParam The system hook parameter (for setResult to consume events)
     * @param event The MotionEvent to process
     * @return true if the event was consumed by the edge gesture system, false if passed through
     */
    fun handleTouchSequence(hookParam: Any, event: MotionEvent): Boolean
    
    /**
     * Cancels any in-progress gesture and replays cached events.
     * Called when external conditions change (rotation, keyboard show, etc.)
     */
    fun cancelAndReplay()
    
    /**
     * Whether a gesture is currently being tracked.
     */
    fun isTracking(): Boolean
    
    /**
     * Registers a listener for gesture events (for hint/haptic integration).
     */
    fun setGestureListener(listener: GestureEngineListener?)
}

interface GestureEngineListener {
    fun onEdgeTouchDown(region: Int, x: Float, y: Float)
    fun onDirectionResolved(direction: Direction, action: Action?)
    fun onSwipeProgress(region: Int, direction: Direction, progress: Float)
    fun onGestureTriggered(region: Int, gestureType: GestureType, action: Action)
    fun onGestureCancelled()
}
```

## EdgeRegionResolver

Determines which edge region (if any) a touch point falls within.

```kotlin
interface EdgeRegionResolver {
    /**
     * Resolves the edge region for a touch point.
     * 
     * @param rawX Touch X coordinate in screen space
     * @param rawY Touch Y coordinate in screen space
     * @return Edge region ID (0-15), or -1 if not in any edge region
     */
    fun resolve(rawX: Float, rawY: Float): Int
    
    /**
     * Gets the parent edge region ID for a细分 region.
     * LeftTop/LeftCenter/LeftBottom → LeftAll
     * RightTop/RightCenter/RightBottom → RightAll
     * etc.
     * 
     * @param regionId 细分 region ID (0-15)
     * @return Parent edge region ID (3, 7, 11, or 15)
     */
    fun getParentEdgeRegion(regionId: Int): Int
    
    /**
     * Invalidates cached geometry (call on screen rotation or settings change).
     */
    fun invalidateCache()
}
```

## GestureDirectionResolver

Determines swipe direction from displacement.

```kotlin
interface GestureDirectionResolver {
    /**
     * Resolves the swipe direction from displacement.
     * Uses dominant-axis logic: whichever axis has greater displacement wins.
     * 
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param currentX Current X coordinate
     * @param currentY Current Y coordinate
     * @param minThreshold Minimum displacement before direction is determined
     * @return Direction enum value
     */
    fun resolve(startX: Float, startY: Float, currentX: Float, currentY: Float, minThreshold: Float): Direction
}

enum class Direction {
    UNKNOWN, LEFT, RIGHT, UP, DOWN
}
```

## GestureTimerController

Manages long-press and double-tap timers.

```kotlin
interface GestureTimerController {
    /**
     * Starts the long-press timer.
     * @param timeoutMs Timeout in milliseconds
     * @param onExpire Callback when timer expires
     */
    fun startLongPressTimer(timeoutMs: Long, onExpire: () -> Unit)
    
    /**
     * Starts the double-tap waiting timer.
     * @param timeoutMs Timeout in milliseconds
     * @param onExpire Callback when timer expires (treat first tap as single tap)
     */
    fun startDoubleTapTimer(timeoutMs: Long, onExpire: () -> Unit)
    
    /**
     * Cancels the long-press timer.
     */
    fun cancelLongPressTimer()
    
    /**
     * Cancels the double-tap timer.
     */
    fun cancelDoubleTapTimer()
    
    /**
     * Cancels all active timers.
     */
    fun cancelAll()
}
```

## GestureActionResolver

Looks up actions from the 16×7 configuration table.

```kotlin
interface GestureActionResolver {
    /**
     * Resolves the action for a given edge region and gesture type.
     * Implements parent-edge fallback: checks细分 region first, then parent edge.
     * 
     * @param regionId Edge region ID (0-15)
     * @param gestureType Gesture type (tap/double-tap/long-press/swipe directions)
     * @param resolveConditional Whether to resolve condition-based actions
     * @return Action if configured, null otherwise
     */
    fun resolve(regionId: Int, gestureType: GestureType, resolveConditional: Boolean = true): Action?
    
    /**
     * Checks if a gesture type has any action configured for a region (including parent fallback).
     * 
     * @param regionId Edge region ID (0-15)
     * @param gestureType Gesture type
     * @return true if an action exists
     */
    fun hasAction(regionId: Int, gestureType: GestureType): Boolean
}

enum class GestureType(val slotIndex: Int) {
    Tap(0), DoubleTap(1), LongPress(2),
    SwipeLeft(3), SwipeRight(4), SwipeUp(5), SwipeDown(6)
}
```

## ContinuousActionHandler

Interface for actions that span the gesture lifecycle (brightness, volume, etc.).

```kotlin
interface ContinuousActionHandler {
    /**
     * Called when the continuous action starts (gesture threshold reached).
     * @param edgeGroup Parent edge group (for context)
     * @param x Starting X coordinate
     * @param y Starting Y coordinate
     * @param time Event timestamp
     */
    fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long)
    
    /**
     * Called on each move event during the continuous action.
     * @param x Current X coordinate
     * @param y Current Y coordinate
     * @param time Event timestamp
     */
    fun onMove(x: Float, y: Float, time: Long)
    
    /**
     * Called when the finger lifts, finalizing the action.
     * @param x Final X coordinate
     * @param y Final Y coordinate
     * @param time Event timestamp
     */
    fun onFinish(x: Float, y: Float, time: Long)
    
    /**
     * Called when the action is cancelled (multi-touch, rotation, etc.)
     */
    fun onCancel()
}
```

## InputInjectionBridge

Abstracts input injection across Android versions.

```kotlin
interface InputInjectionBridge {
    /**
     * Injects an input event into the system input pipeline.
     * Used to replay cached down events when a gesture is cancelled.
     * 
     * @param event The InputEvent to inject
     */
    fun injectEvent(event: InputEvent)
    
    /**
     * Enables or disables the system input filter.
     * 
     * @param enabled true to enable, false to disable
     */
    fun setFilterEnabled(enabled: Boolean)
}
```

## Preserved Interfaces

The following existing interfaces are preserved unchanged:

### DetectorService (existing)

```kotlin
interface DetectorService {
    fun handleEdgeGestureEvent(areas: List<DetectedArea>, gestureEvent: Gesture, downX: Int, downY: Int): Boolean
    fun findMatchingEdgeGestureSequences(areas: List<DetectedArea>, gestureEvent: Gesture): List<EdgeGestureSequenceCandidate>
    fun fireEdgeGestureSequence(area: DetectedArea, gesture: Gesture, childSteps: List<EdgeGestureStep>, downX: Int, downY: Int): Boolean
    fun handleCornerSwipeToCenterEvent(corner: ScreenCorner, downX: Int, downY: Int): Boolean
    
    // Settings getters
    val swipeDistanceThresholdDp: Float
    val gestureAreaWidthDp: Float
    val gestureLengthScale: Float
    val cornerGestureRegionRadiusRatio: Float
    val cornerSwipeMinDistanceDp: Float
    val edgeGestureSequenceHoverDurationMs: Long
    val edgeGestureSequenceRadiusDp: Float
    val edgeGestureSequenceChildSwipeMinDistanceDp: Float
    val edgeGestureSequenceChildSwipeMaxAngleDeviationDeg: Float
    
    // Observer registrars
    fun obsGestureSettingsChanged(observer: () -> Unit)
    fun obsConfigChanged(observer: () -> Unit)
    fun obsUserDataChanged(observer: () -> Unit)
    
    // Enabled gesture sets
    val enabledEdgeGestures: Set<EdgeGesture>
    val enabledCornerSwipeToCenterFacts: Set<CornerSwipeToCenter>
}
```

### GestureEngineListener (new, for hint/haptic integration)

```kotlin
interface GestureEngineListener {
    fun onEdgeTouchDown(region: Int, x: Float, y: Float)
    fun onDirectionResolved(direction: Direction, action: Action?)
    fun onSwipeProgress(region: Int, direction: Direction, progress: Float)
    fun onGestureTriggered(region: Int, gestureType: GestureType, action: Action)
    fun onGestureCancelled()
}
```
