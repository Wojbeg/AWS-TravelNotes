package com.wojbeg.aws_travelnotes.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun LoadingAnimation (
    modifier: Modifier = Modifier,
    dotSize: Dp = 15.dp,
    dotColor: Color = MaterialTheme.colors.primary,
    spaceBetween: Dp = 10.dp,
    travelDistance: Dp = 20.dp,
    numberOfDots: Int = 3,
    animationDuration: Int = 900,
    delayBetween: Long = 100L
) {

    val dots = List(numberOfDots) {
        remember { Animatable(initialValue = 0f) }
    }

    val dotsValues = dots.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * delayBetween)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    repeatMode = RepeatMode.Reverse,
                    animation = keyframes {
                        durationMillis = animationDuration
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at animationDuration/numberOfDots with LinearOutSlowInEasing
                    }
                )
            )
        }
    }

    Row(modifier = modifier) {
        dotsValues.forEachIndexed { index, value ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        translationY = -value * distance
                    }
                    .background(
                        color = dotColor,
                        shape = CircleShape
                    )
            )
            if (dotsValues.lastIndex != index) {
                Spacer(modifier = Modifier.width(spaceBetween))
            }
        }
    }
}