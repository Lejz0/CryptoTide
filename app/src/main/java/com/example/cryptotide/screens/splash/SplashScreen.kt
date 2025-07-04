package com.example.cryptotide.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay

private const val SPLASH_TIMEOUT = 1000L

@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }

    LaunchedEffect(true) {
        delay(SPLASH_TIMEOUT)
        viewModel.onAppStart(
            { navController.navigate("HOME_SCREEN") { popUpTo(0) } },
            { navController.navigate("SIGN_IN_SCREEN") { popUpTo(0) } })
    }
}