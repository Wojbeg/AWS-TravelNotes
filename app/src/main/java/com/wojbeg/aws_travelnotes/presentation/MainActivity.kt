package com.wojbeg.aws_travelnotes.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wojbeg.aws_travelnotes.*
import com.wojbeg.aws_travelnotes.presentation.login.LoginScreen
import com.wojbeg.aws_travelnotes.presentation.note.NoteScreen
import com.wojbeg.aws_travelnotes.presentation.note.NoteState
import com.wojbeg.aws_travelnotes.presentation.profile.ProfileScreen
import com.wojbeg.aws_travelnotes.presentation.sign_up.SignUpScreen
import com.wojbeg.aws_travelnotes.presentation.splash.SplashScreen
import com.wojbeg.aws_travelnotes.presentation.ui.theme.AWSTravelNotesTheme
import com.wojbeg.aws_travelnotes.presentation.util.Screen
import com.wojbeg.aws_travelnotes.presentation.verify.VerificationScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private final val TAG = "MAIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AWSTravelNotesTheme {
                AppNavigator()
            }

        }

    }


    @Composable
    private fun AppNavigator() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
            composable(Screen.SplashScreen.route) {
                SplashScreen(navController = navController)
            }
            composable(Screen.LoginScreen.route) {
                LoginScreen(navController = navController)
            }
            composable(Screen.SignUpScreen.route) {
                SignUpScreen(navController = navController)
            }
            composable(
                "${Screen.VerifyScreen.route}/{username}",
                arguments = listOf(
                    navArgument("username") {
                        nullable = false
                        defaultValue = ""
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                VerificationScreen(
                    navController = navController,
                    username = backStackEntry.arguments?.getString("username") ?: ""
                )
            }
            composable(Screen.HomeScreen.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.ProfileScreen.route) {
                ProfileScreen(navController = navController)
            }
            composable(
                "${Screen.Note.route}?id={id}",
                arguments = listOf(
                    navArgument("id") {
                        nullable = true
                        defaultValue = null
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                NoteScreen(
                    navController = navController,
                    id = backStackEntry.arguments?.getString("id")
                )
            }
        }
    }
}
