package com.example.cryptotide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cryptotide.screens.coin_detail.CoinDetailScreen
import com.example.cryptotide.screens.home.HomeScreen
import com.example.cryptotide.screens.sign_in.SignInScreen
import com.example.cryptotide.screens.sign_up.SignUpScreen
import com.example.cryptotide.screens.splash.SplashScreen
import com.example.cryptotide.screens.wallet.WalletsScreen
import com.example.cryptotide.ui.theme.CryptoTideTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTideTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "SPLASH_SCREEN",
                    builder = {
                        composable("SPLASH_SCREEN") {
                            SplashScreen(navController = navController)
                        }
                        composable("SIGN_UP_SCREEN") {
                            SignUpScreen(navController = navController)
                        }
                        composable("SIGN_IN_SCREEN") {
                            SignInScreen(navController = navController)
                        }
                        composable("HOME_SCREEN") {
                            HomeScreen(navController = navController)
                        }
                        composable("COIN_DETAIL/{coinId}") {
                            backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                            CoinDetailScreen(navController = navController, coinId = coinId)
                        }

                        composable(
                            route = "wallet_screen/{coinId}",
                            arguments = listOf(navArgument("coinId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                            WalletsScreen(coinId = coinId, navController = navController)
                        }
                    })
            }
        }
    }
}

