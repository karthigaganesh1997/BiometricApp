package com.example.biometricapp.Utill

const val PREF_BIOMETRIC = "biometric_preferences"
const val ENCRYPTED_FILE_NAME = "encrypted_data_store"
const val SECRET_KEY = "biometric_secret_key"


enum class BiometricClass{
    BIOMETRIC_STRONG , DEVICE_CREDENTIAL,BIOMETRIC_WEAK
}
