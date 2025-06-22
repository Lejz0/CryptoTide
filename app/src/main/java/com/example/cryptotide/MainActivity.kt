package com.example.cryptotide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cryptotide.screens.coin_detail.CoinDetailScreen
import com.example.cryptotide.screens.favorites.FavoritesScreen
import com.example.cryptotide.screens.home.HomeScreen
import com.example.cryptotide.screens.profile.ProfileScreen
import com.example.cryptotide.screens.sign_in.SignInScreen
import com.example.cryptotide.screens.sign_up.SignUpScreen
import com.example.cryptotide.screens.splash.SplashScreen
import com.example.cryptotide.screens.wallet.WalletsScreen
import com.example.cryptotide.ui.theme.CryptoTideTheme
import dagger.hilt.android.AndroidEntryPoint

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem("HOME_SCREEN", "Crypto", Icons.Default.Home),
    BottomNavItem("FAVORITES", "Favorites", Icons.Default.Favorite),
    BottomNavItem("PROFILE", "Profile", Icons.Default.AccountCircle)
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTideTheme {
                val navController = rememberNavController()
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry.value?.destination
                val currentRoute = currentDestination?.route

                val routesWithBottomBar = listOf(
                    "HOME_SCREEN", "FAVORITES", "PROFILE",
                    "COIN_DETAIL/{coinId}", "wallet_screen/{coinId}"
                )

                Scaffold(
                    bottomBar = {
                        if (routesWithBottomBar.any { currentRoute?.startsWith(it.substringBefore("/{")) == true }) {
                            BottomNavigation {
                                bottomNavItems.forEach { item ->
                                    BottomNavigationItem(
                                        icon = { Icon(item.icon, contentDescription = null) },
                                        label = { Text(item.label) },
                                        selected = currentRoute == item.route,
                                        onClick = {
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo("HOME_SCREEN") { inclusive = false }
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                        navController = navController,
                        startDestination = "SPLASH_SCREEN",
                    ) {
                        composable("SPLASH_SCREEN") {
                            SplashScreen(navController = navController)
                        }
                        composable("SIGN_UP_SCREEN") {
                            SignUpScreen(navController = navController)
                        }
                        composable("SIGN_IN_SCREEN") {
                            SignInScreen(navController = navController)
                        }
                        navigation(startDestination = "HOME_SCREEN", route = "MAIN") {
                            composable("HOME_SCREEN") {
                                HomeScreen(navController = navController)
                            }
                            composable("FAVORITES") {
                                FavoritesScreen()
                            }
                            composable("PROFILE") {
                                ProfileScreen(navController = navController)
                            }
                        }
                        composable("COIN_DETAIL/{coinId}") { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                            CoinDetailScreen(navController = navController, coinId = coinId)
                        }
                        composable(
                            route = "wallet_screen/{coinId}",
                            arguments = listOf(navArgument("coinId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                            WalletsScreen(coinId = coinId, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

