package com.example.biometricapp.Utill

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.biometricapp.R
import com.example.biometricapp.domain.BiometricResult
import com.example.biometricauthenticationcompose.manager.CryptoManager
import java.util.UUID

/*
 * BiometricHelper is a utility object that simplifies the implementation of biometric authentication
 * functionalities in Android apps. It provides methods to check biometric availability, register user
 * biometrics, and authenticate users using biometric authentication.
 *
 * This object encapsulates the logic for interacting with the BiometricPrompt API and integrates
 * seamlessly with the CryptoManager to encrypt and decrypt sensitive data for secure storage.
 */

object BiometricHelper {


    // Check if biometric authentication is available on the device
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("BiometricHelper", "No biometrics enrolled")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("BiometricHelper", "Device has no biometric hardware.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("BiometricHelper", "Biometric hardware is currently unavailable.")
                false
            }

            else -> false
        }
    }

    // Retrieve a BiometricPrompt instance with a predefined callback
    private fun getBiometricPrompt(
        context: FragmentActivity,
        onAuthSucceed: (BiometricPrompt.AuthenticationResult) -> Unit
    ): BiometricPrompt {
        val biometricPrompt = BiometricPrompt(
            context,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    Log.e("TAG", "Authentication Succeeded: ${result.cryptoObject}")
                    onAuthSucceed(result)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(
                        context,
                        "Authentication failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        )
        return biometricPrompt
    }


    // Create BiometricPrompt.PromptInfo with customized display text
    private fun getPromptInfo(
        context: FragmentActivity,
        isCryptoRequired: Boolean
    ): BiometricPrompt.PromptInfo {


        val authenticators = BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL

        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_prompt_title_text))
            .setSubtitle(context.getString(R.string.biometric_prompt_subtitle_text))
            .setDescription(context.getString(R.string.biometric_prompt_description_text))
            .setAllowedAuthenticators(authenticators)
            .setDeviceCredentialAllowed(!isCryptoRequired)
            /* .setDeviceCredentialAllowed(true) // Use biometrics or device PIN
             .setConfirmationRequired(false)
             .setNegativeButtonText(context.getString(R.string.biometric_prompt_use_password_instead_text))*/
            .build()
    }

    // Register user biometrics by encrypting a randomly generated token
    fun registerUserBiometrics(
        context: FragmentActivity,
        onSuccess: (biometricResult: BiometricResult) -> Unit = {}
    ) {
        val cryptoManager = CryptoManager()
        val cipher = cryptoManager.initEncryptionCipher(SECRET_KEY)


        val biometricPrompt = getBiometricPrompt(context) { authResult ->

            val biometricClass = when {
                authResult.cryptoObject != null -> BiometricClass.BIOMETRIC_STRONG
                !isCryptoRequired(context) -> BiometricClass.DEVICE_CREDENTIAL
                else -> BiometricClass.BIOMETRIC_WEAK
            }



            if (biometricClass == BiometricClass.BIOMETRIC_STRONG) {
                authResult.cryptoObject?.cipher?.let { authenticatedCipher ->
                    val token = UUID.randomUUID().toString()
                    val encryptedToken = cryptoManager.encrypt(token, authenticatedCipher)
                    cryptoManager.saveToPrefs(
                        encryptedToken,
                        context,
                        ENCRYPTED_FILE_NAME,
                        Context.MODE_PRIVATE,
                        PREF_BIOMETRIC
                    )

                }
            }

            onSuccess(BiometricResult(biometricClass, authResult))

        }


        val promptInfo = getPromptInfo(context, isCryptoRequired(context))
        if (isCryptoRequired(context)) {
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        } else {
            biometricPrompt.authenticate(promptInfo)
        }


    }


    // Authenticate user using biometrics by decrypting stored token
    fun authenticateUser(
        context: FragmentActivity,
        onSuccess: (plainText: String) -> Unit
    ) {
        val cryptoManager = CryptoManager()
        val encryptedData = cryptoManager.getFromPrefs(
            context,
            ENCRYPTED_FILE_NAME,
            Context.MODE_PRIVATE,
            PREF_BIOMETRIC
        )


        encryptedData?.let { data ->
            val cipher = cryptoManager.initDecryptionCipher(SECRET_KEY, data.initializationVector)
            val biometricPrompt = getBiometricPrompt(context) { authResult ->
                authResult.cryptoObject?.cipher?.let { cipher ->
                    val plainText = cryptoManager.decrypt(data.ciphertext, cipher)
                    // Execute custom action on successful authentication
                    onSuccess(plainText)
                }
            }
            val promptInfo = getPromptInfo(context, isCryptoRequired(context))

            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))


        }
    }

    private fun isCryptoRequired(context: Context): Boolean {
        return context.getSystemService(BiometricManager::class.java)
            ?.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }


}


