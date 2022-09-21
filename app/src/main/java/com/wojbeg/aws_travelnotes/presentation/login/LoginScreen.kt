package com.wojbeg.aws_travelnotes.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainOrange
import com.wojbeg.aws_travelnotes.R
import com.wojbeg.aws_travelnotes.presentation.sign_up.SignUpViewModel
import com.wojbeg.aws_travelnotes.presentation.util.Screen


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.loginState

    val outlinedTextColor = TextFieldDefaults.outlinedTextFieldColors(
        focusedLabelColor = mainOrange,
        focusedBorderColor = mainOrange,
        cursorColor = mainOrange,
        unfocusedBorderColor = Color.White,
        unfocusedLabelColor = Color.White,
        textColor = Color.White
    )

    LaunchedEffect(key1 = true) {
        viewModel.validationEvents.collect { event ->
            when(event) {
                LoginViewModel.LoginUIEvents.LoginSuccess -> {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(0)
                    }
                }
                LoginViewModel.LoginUIEvents.LoginError -> {
                    navController.navigate("${Screen.VerifyScreen.route}/${viewModel.loginState.username}")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {

            Icon(
                modifier = Modifier
                    .fillMaxWidth(0.6f),
                painter = painterResource(id = R.drawable.login),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.username,
                onValueChange = {
                    viewModel.onEvent(LoginEvents.UpdateLoginUsername(it))
                },
                label = { Text(
                    text= stringResource(id = R.string.username)
                ) },
                colors = outlinedTextColor,
                isError = state.emailError != null,
            )

            if (state.emailError != null) {
                Text(
                    text = state.emailError!!,
                    color = MaterialTheme.colors.error
                )
            }

            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    viewModel.onEvent(LoginEvents.UpdateLoginPassword(it))
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                label = {
                    Text(text= stringResource(id = R.string.password))
                },
                colors = outlinedTextColor,
                isError = state.passwordError != null
            )

            if (state.passwordError != null) {
                Text(
                    text = state.passwordError!!,
                    color = MaterialTheme.colors.error
                )
            }

            Button(
                onClick = {
                    if (!state.isLoading) {
                        viewModel.onEvent(LoginEvents.Login)
                    }
                },
            ) {
                Text(text= stringResource(id = R.string.sign_in))
            }

            TextButton(
                onClick = {
                    navController.navigate(Screen.SignUpScreen.route)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.no_account),
                    color = mainOrange
                )
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