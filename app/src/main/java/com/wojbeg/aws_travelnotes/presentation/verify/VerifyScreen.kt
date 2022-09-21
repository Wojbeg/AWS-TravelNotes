package com.wojbeg.aws_travelnotes.presentation.verify

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainOrange
import com.wojbeg.aws_travelnotes.R
import com.wojbeg.aws_travelnotes.presentation.util.Screen


@Composable
fun VerificationScreen(
    viewModel: VerifyViewModel = hiltViewModel(),
    navController: NavController,
    username: String
) {

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(VerificationEvents.CheckUsername(username))
    }

    LaunchedEffect(key1 = true) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                VerifyViewModel.AuthUIEvents.ValidationSuccess -> {
                    navController.navigate(Screen.SplashScreen.route) {
                        popUpTo(0)
                    }
                }
                else -> {}
            }
        }
    }

    val state = viewModel.verificationCodeState

    val outlinedTextColor = TextFieldDefaults.outlinedTextFieldColors(
        focusedLabelColor = mainOrange,
        focusedBorderColor = mainOrange,
        cursorColor = mainOrange,
        unfocusedBorderColor = Color.White,
        unfocusedLabelColor = Color.White,
        textColor = Color.White
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {

            Icon(
                modifier = Modifier
                    .fillMaxWidth(0.6f),
                painter = painterResource(id = R.drawable.verify),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colors.error
                )
            }

            OutlinedTextField(
                value = state.code,
                onValueChange = {
                    viewModel.onEvent(VerificationEvents.UpdateVerificationCode(it))
                },
                label = { 
                    Text(text = stringResource(id = R.string.verification_code)) 
                },
                colors = outlinedTextColor
            )

            Button(
                onClick = {
                    if (!state.isLoading) {
                        viewModel.onEvent(VerificationEvents.VerifyCode)
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.verify))
            }

        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(Alignment.Center)
            )
        }
    }
}