package com.example.biometricauthenticationcompose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biometricapp.Utill.BiometricClass
import com.example.biometricauthenticationcompose.preferences.BiometricPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val preferences: BiometricPreferences
) : ViewModel() {

    val isBiometricEnabled = MutableStateFlow(false)
    val biometricClass = MutableStateFlow<BiometricClass>(BiometricClass.BIOMETRIC_STRONG)

    init {
        viewModelScope.launch {

            isBiometricEnabled.value = preferences.isBiometricEnabled()

            val biometricClassVal = preferences.getBiometricClass()

            biometricClassVal?.let { biometricClass.tryEmit(it) }

        }
    }

    fun setBiometricEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            preferences.setBiometricEnabled(isEnabled)

            isBiometricEnabled.value = isEnabled


        }
    }
}