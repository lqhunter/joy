package com.lq.joy.ui.page.common

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset


enum class JumpAnimationType {
    DEFAULT,
    NORMAL
}

class PageAnimation(private val type: JumpAnimationType) {
    private val springSpec = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    private val tweenSpec = tween<IntOffset>(durationMillis = 500)


    val enterTransition: EnterTransition?
        get() {
            return when (type) {
                JumpAnimationType.DEFAULT -> null
                JumpAnimationType.NORMAL -> {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tweenSpec
                    )
                }
            }
        }

    val exitTransition: ExitTransition?
        get() {
            return when (type) {
                JumpAnimationType.DEFAULT -> null
                JumpAnimationType.NORMAL -> {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tweenSpec
                    )
                }
            }
        }

    val popEnterTransition: EnterTransition?
        get() {
            return when (type) {
                JumpAnimationType.DEFAULT -> null
                JumpAnimationType.NORMAL -> {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tweenSpec
                    )
                }
            }
        }

    val popExitTransition: ExitTransition?
        get() {
            return when (type) {
                JumpAnimationType.DEFAULT -> null
                JumpAnimationType.NORMAL -> {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tweenSpec
                    )
                }
            }
        }
}