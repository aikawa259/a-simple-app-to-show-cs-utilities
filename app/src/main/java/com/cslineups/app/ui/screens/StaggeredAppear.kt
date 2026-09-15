package com.cslineups.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * 进入页面时让内容依次"抬"起来，比整块出现更有节奏。
 */
@Composable
fun StaggeredAppear(
    index: Int,
    stepMillis: Long = 70L,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * stepMillis)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(260)) + slideInVertically(
            animationSpec = spring(
                dampingRatio = 0.72f,
                stiffness = Spring.StiffnessLow,
            ),
            initialOffsetY = { it / 4 },
        ),
    ) {
        content()
    }
}
