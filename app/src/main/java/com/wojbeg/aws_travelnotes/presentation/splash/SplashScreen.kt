package com.wojbeg.aws_travelnotes.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainBlue
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainOrange
import com.wojbeg.aws_travelnotes.R
import com.wojbeg.aws_travelnotes.common.Constants.SPLASH_TEXT_DURATION
import com.wojbeg.aws_travelnotes.common.Constants.SPLASH_VECTOR_DURATION
import com.wojbeg.aws_travelnotes.presentation.components.LoadingAnimation
import com.wojbeg.aws_travelnotes.presentation.splash.SplashViewModel.SplashUIEvents
import com.wojbeg.aws_travelnotes.presentation.util.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navController: NavController
) {
    val campingVector = remember {
        Animatable(0f)
    }

    val textAnimation = remember {
        Animatable(0f)
    }

    val textOffsetAnimation = remember {
        Animatable(1f)
    }

    LaunchedEffect(key1 = true) {

        campingVector.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = SPLASH_VECTOR_DURATION,
                easing = FastOutSlowInEasing
            )
        )

        textOffsetAnimation.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = SPLASH_TEXT_DURATION,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(key1 = true) {

        textAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = SPLASH_TEXT_DURATION,
                delayMillis = SPLASH_VECTOR_DURATION,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.splashEvents.collect { event ->
            when(event) {
                SplashUIEvents.UserLoggedIn -> {
                    delay(3000)
                    navController.popBackStack()
                    navController.navigate(Screen.HomeScreen.route)
                }
                SplashUIEvents.UserNotFound -> {
                    delay(3000)
                    navController.popBackStack()
                    navController.navigate(Screen.LoginScreen.route)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .background(mainBlue)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .scale(campingVector.value),
                painter = painterResource(id = R.drawable.campfire),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Text(
                text = stringResource(id = R.string.splash_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
                    .alpha(textAnimation.value)
                    .offset(y = (textOffsetAnimation.value * 250).toInt().dp),
                color = mainOrange,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

        }

        LoadingAnimation(
            dotColor = mainOrange,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.BottomCenter)
        )
    }

}
