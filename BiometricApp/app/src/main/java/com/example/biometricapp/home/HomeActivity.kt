package com.example.biometricauthenticationcompose.home

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.biometricapp.R
import com.example.biometricapp.Utill.BiometricClass
import com.example.biometricapp.Utill.BiometricHelper
import com.example.biometricapp.Utill.ENCRYPTED_FILE_NAME
import com.example.biometricapp.Utill.PREF_BIOMETRIC
import com.example.biometricapp.ui.theme.BiometricAppTheme
import com.example.biometricauthenticationcompose.manager.CryptoManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricAppTheme   {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeActivityViewModel>()


    val isBioMetricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val context = LocalContext.current as FragmentActivity
    val isBiometricAvailable = remember { BiometricHelper.isBiometricAvailable(context) }
    val biometricClass  by viewModel.biometricClass.collectAsState()


    LaunchedEffect(key1 = isBioMetricEnabled) {
        if (isBioMetricEnabled) {
            val cryptoManager = CryptoManager()
            val encryptedData = cryptoManager.getFromPrefs(
                context,
                ENCRYPTED_FILE_NAME,
                Context.MODE_PRIVATE,
                PREF_BIOMETRIC
            )
            if (encryptedData == null) {
                if (biometricClass != BiometricClass.DEVICE_CREDENTIAL && biometricClass != BiometricClass.BIOMETRIC_WEAK) {
                    BiometricHelper.registerUserBiometrics(context)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_screen_top_bar_title_text),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray.copy(0.4f)
                )
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onSecondaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            if (isBiometricAvailable) {
                Row(
                    modifier = Modifier
                        .padding(paddingValues = it)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_switch_view_text),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = isBioMetricEnabled,
                        onCheckedChange = {
                            viewModel.setBiometricEnabled(it)
                        }
                    )
                }
            }
        }
    }
}