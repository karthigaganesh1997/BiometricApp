package com.example.biometricapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biometricapp.Navigation.NavigationRoutes
import com.example.biometricapp.Utill.BiometricHelper
import com.example.biometricapp.ui.theme.BiometricAppTheme
import com.example.biometricauthenticationcompose.signin.SignInScreen
import com.example.biometricauthenticationcompose.signup.SignUpScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BiometricAppTheme {
               Surface(modifier = Modifier.fillMaxSize(),
                   color = MaterialTheme.colorScheme.background) {
                   val navController = rememberNavController()
                   NavHost(navController = navController,
                       startDestination = NavigationRoutes.SIGN_IN,
                       builder = {
                           composable(NavigationRoutes.SIGN_IN) {
                               SignInScreen(navController)
                           }
                           composable(NavigationRoutes.SIGN_UP) {
                               SignUpScreen()
                           }
                       })
               }
            }
        }
    }
}

