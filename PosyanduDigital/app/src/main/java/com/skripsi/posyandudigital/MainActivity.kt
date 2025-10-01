package com.skripsi.posyandudigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skripsi.posyandudigital.ui.dashboard.DashboardScreen
import com.skripsi.posyandudigital.ui.login.LoginScreen
import com.skripsi.posyandudigital.ui.theme.PosyanduDigitalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PosyanduDigitalTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { userRole ->
                                navController.navigate("dashboard/${userRole.lowercase()}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(
                        route = "dashboard/{role}",
                        arguments = listOf(navArgument("role") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val role = backStackEntry.arguments?.getString("role")

                        DashboardScreen(
                            userRole = role ?: "kader",
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

