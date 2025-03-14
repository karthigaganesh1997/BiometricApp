package com.example.biometricapp.Screen.signin

sealed class SignInState {
    data object InvalidEmailId : SignInState()
    data object InvalidPassword : SignInState()
    data object InvalidCredentials : SignInState()
    data object Success : SignInState()
}