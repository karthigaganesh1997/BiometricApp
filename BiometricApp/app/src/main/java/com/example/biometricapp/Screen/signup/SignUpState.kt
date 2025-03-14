package com.example.biometricapp.Screen.signup

sealed class SignUpState {
    data object SUCCESS : SignUpState()
    data object InvalidEmailId : SignUpState()
    data object InvalidPassword : SignUpState()
    data object InvalidConfirmPassword : SignUpState()
}