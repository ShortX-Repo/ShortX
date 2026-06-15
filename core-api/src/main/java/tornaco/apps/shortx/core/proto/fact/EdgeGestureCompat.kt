package tornaco.apps.shortx.core.proto.fact

private const val CURRENT_EDGE_GESTURE_CHILD_STEP_LIMIT = 1

// Current product UI only supports one child step. Normalize older or malformed
// persisted rules so runtime and matcher behavior align with what the editor shows.
fun EdgeGesture.normalizedForSingleChildStep(): EdgeGesture {
    val normalizedChildSteps = childStepsList.asSequence()
        .filter { it.type != EdgeGestureStepType.UNRECOGNIZED }
        .take(CURRENT_EDGE_GESTURE_CHILD_STEP_LIMIT)
        .toList()
    val alreadyNormalized = childStepsList == normalizedChildSteps
    if (alreadyNormalized) return this

    return toBuilder()
        .clearChildSteps()
        .addAllChildSteps(normalizedChildSteps)
        .build()
}
