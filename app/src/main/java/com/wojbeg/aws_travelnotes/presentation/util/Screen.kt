package com.wojbeg.aws_travelnotes.presentation.util

sealed class Screen(val route: String) {
    object SplashScreen: Screen("splash")
    object LoginScreen: Screen("login")
    object SignUpScreen: Screen("signUp")
    object VerifyScreen: Screen("verify")
    object HomeScreen: Screen("home")
    object ProfileScreen: Screen("profile")
    object Note: Screen("note")
}
