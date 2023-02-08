package tornaco.apps.shortx.ui.base.dropdown

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.Easing
import androidx.compose.ui.unit.IntOffset

/*[EnterAnimation] will return enter animation instance ont he basis of enum selection */
@OptIn(ExperimentalAnimationApi::class)
@JvmSynthetic
@PublishedApi
internal fun getMetaphorEnterAnimation(
    animationProp: AnimationProp,
    enterAnimation: EnterAnimation
): EnterTransition {
    when (enterAnimation) {
        EnterAnimation.FadeIn -> {
            return fadeIn(
                animationSpec = tween(
                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                ),

                )
        }
        EnterAnimation.SharedAxisXForward -> {

            return slideInHorizontally(
                animationSpec = tween(
                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                ),

                )
        }

        EnterAnimation.SharedAxisYForward -> {

            return slideInVertically(
                animationSpec = tween(
                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        EnterAnimation.SharedAxisZForward -> {

            return fadeIn(
                animationSpec = tween(
                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            ) + scaleIn(
                animationSpec = tween(
                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        EnterAnimation.ElevationScale -> {
            return fadeIn(
                animationSpec = tween(
                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            ) + scaleIn(
                animationSpec = tween(
                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        EnterAnimation.SlideIn -> {
            return slideIn(
                animationSpec = tween(

                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)

                ),
                initialOffset = { fullSize ->
                    IntOffset(
                        fullSize.width / 2,
                        fullSize.height / 2
                    )
                }

            )
        }

        EnterAnimation.SlideInHorizontally -> {
            return slideInHorizontally(
                animationSpec = tween(

                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)

                )

            )
        }

        EnterAnimation.SlideInVertically -> {
            return slideInVertically(
                animationSpec = tween(

                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)

                )

            )
        }

        EnterAnimation.ScaleIn -> {
            return scaleIn(
                animationSpec = tween(

                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)

                )

            )
        }

        EnterAnimation.ExpandIn -> {
            return expandIn(
                animationSpec = tween(

                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)

                )

            )
        }

        EnterAnimation.ExpandHorizontally -> {
            return expandHorizontally(
                animationSpec = tween(

                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)

                )

            )
        }

        EnterAnimation.ExpandVertically -> {
            return expandVertically(
                animationSpec = tween(

                    durationMillis = animationProp.enterDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)

                )

            )
        }
    }
}

/*[ExitAnimation] will return exit animation instance ont he basis of enum selection */
@OptIn(ExperimentalAnimationApi::class)
@JvmSynthetic
@PublishedApi
internal fun getMetaphorExitAnimation(
    animationProp: AnimationProp,
    enterAnimation: ExitAnimation
): ExitTransition {

    when (enterAnimation) {

        ExitAnimation.FadeOut -> {
            return fadeOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }
        ExitAnimation.SharedAxisXBackward -> {

            return slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.SharedAxisYBackward -> {
            return slideOutVertically(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.SharedAxisZBackward -> {

            return fadeOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            ) + scaleOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.ElevationScale -> {
            return fadeOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            ) + scaleOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.SlideOut -> {
            return slideOut(

                targetOffset = { fullSize ->
                    IntOffset(
                        -fullSize.width / 2,
                        fullSize.height / 2
                    )
                },

                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.SlideOutHorizontally -> {
            return slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }
        ExitAnimation.SlideOutVertically -> {
            return slideOutVertically(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.ScaleOut -> {
            return scaleOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.ShrinkOut -> {
            return shrinkOut(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.ShrinkHorizontally -> {
            return shrinkHorizontally(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }

        ExitAnimation.ShrinkVertically -> {
            return shrinkVertically(
                animationSpec = tween(
                    durationMillis = animationProp.exitDuration,
                    delayMillis = animationProp.delay,
                    easing = getEasing(animationProp.easing)
                )
            )
        }
    }
}

/*[Easing] will return easing instance to perform animation  on the  basis of enum selection */
@OptIn(ExperimentalAnimationApi::class)
@JvmSynthetic
@PublishedApi
internal fun getEasing(enterAnimation: tornaco.apps.shortx.ui.base.dropdown.Easing): Easing {

    when (enterAnimation) {

        tornaco.apps.shortx.ui.base.dropdown.Easing.FastOutSlowInEasing -> {

            return FastOutSlowInEasing
        }

        tornaco.apps.shortx.ui.base.dropdown.Easing.LinearEasing -> {
            return LinearEasing
        }

        tornaco.apps.shortx.ui.base.dropdown.Easing.FastOutLinearInEasing -> {

            return FastOutLinearInEasing
        }

        tornaco.apps.shortx.ui.base.dropdown.Easing.LinearOutSlowInEasing -> {
            return LinearOutSlowInEasing
        }
    }
}
