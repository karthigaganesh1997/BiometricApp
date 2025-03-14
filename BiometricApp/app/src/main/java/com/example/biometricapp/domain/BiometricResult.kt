package com.example.biometricapp.domain

import androidx.biometric.BiometricPrompt
import com.example.biometricapp.Utill.BiometricClass

data class BiometricResult(
    val biometricClass: BiometricClass,
    val authResult: BiometricPrompt.AuthenticationResult
)