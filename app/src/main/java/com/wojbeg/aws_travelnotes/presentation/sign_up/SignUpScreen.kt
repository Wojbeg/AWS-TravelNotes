package com.wojbeg.aws_travelnotes.presentation.sign_up

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.wojbeg.aws_travelnotes.presentation.util.Screen

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.signUpState
    val scrollState = rememberScrollState()

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
                SignUpViewModel.SignUpUIEvents.RegisterSuccess -> {
                    navController.navigate("${Screen.VerifyScreen.route}/${viewModel.signUpState.username}")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                10.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {

            Icon(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                painter = painterResource(id = R.drawable.register),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.otherError != null) {
                Text(
                    text = state.otherError!!,
                    color = MaterialTheme.colors.error
                )
            }

            OutlinedTextField(
                value = state.username,
                onValueChange = {
                    viewModel.onEvent(SignUpEvents.UpdateUsername(it))
                },
                label = { 
                    Text(text = stringResource(id = R.string.username)) 
                },
                colors = outlinedTextColor,
                singleLine = true
            )

            if (state.usernameError != null) {
                Text(
                    text = state.usernameError!!,
                    color = MaterialTheme.colors.error
                )
            }

            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    viewModel.onEvent(SignUpEvents.UpdateEmail(it))
                },
                label = { 
                    Text(text = stringResource(id = R.string.email)) 
                },
                colors = outlinedTextColor,
                singleLine = true
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
                    viewModel.onEvent(SignUpEvents.UpdatePassword(it))
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                label = { 
                    Text(text = stringResource(id = R.string.password)) 
                },
                colors = outlinedTextColor,
                singleLine = true
            )

            if (state.passwordError != null) {
                Text(
                    text = state.passwordError!!,
                    color = MaterialTheme.colors.error
                )
            }

            OutlinedTextField(
                value = state.repeatPassword,
                onValueChange = {
                    viewModel.onEvent(SignUpEvents.UpdateRepeatPassword(it))
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                label = { Text(text = stringResource(id = R.string.repeat_password)) },
                colors = outlinedTextColor,
                singleLine = true
            )

            if (state.repeatPasswordError != null) {
                Text(
                    text = state.repeatPasswordError!!,
                    color = MaterialTheme.colors.error
                )
            }

            Button(
                onClick = {
                    if (!state.isLoading) {
                        viewModel.onEvent(SignUpEvents.SignUp)
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.signup))
            }

            TextButton(onClick = {
                if (!state.isLoading) {
                    navController.navigate(Screen.LoginScreen.route)
                }
            }) {
                Text(text = stringResource(id = R.string.already_registered))
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
