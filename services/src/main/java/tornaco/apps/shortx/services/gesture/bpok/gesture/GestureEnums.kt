package tornaco.apps.shortx.services.gesture.bpok.gesture

enum class Direction {
    UNKNOWN, LEFT, RIGHT, UP, DOWN
}

enum class GestureType(val slotIndex: Int) {
    Tap(0),
    DoubleTap(1),
    LongPress(2),
    SwipeLeft(3),
    SwipeRight(4),
    SwipeUp(5),
    SwipeDown(6);

    companion object {
        fun fromSlotIndex(index: Int): GestureType? = entries.find { it.slotIndex == index }
    }
}
