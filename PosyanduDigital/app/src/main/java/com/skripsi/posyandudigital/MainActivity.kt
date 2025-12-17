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
import com.skripsi.posyandudigital.ui.anak.DaftarAnakScreen
import com.skripsi.posyandudigital.ui.anak.TambahAnakScreen
import com.skripsi.posyandudigital.ui.dashboard.DashboardScreen
import com.skripsi.posyandudigital.ui.kader.VerificationScreen
import com.skripsi.posyandudigital.ui.login.LoginScreen
import com.skripsi.posyandudigital.ui.orangtua.VerificationCodeDisplayScreen
import com.skripsi.posyandudigital.ui.register.RegisterScreen
import com.skripsi.posyandudigital.ui.theme.PosyanduDigitalTheme
import com.skripsi.posyandudigital.ui.usermanagement.UserManagementScreen

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
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            },
                            onNavigateToVerificationCode = { code ->
                                navController.navigate("show_code/$code")
                            }
                        )
                    }

                    // --- PERBAIKAN DI SINI ---
                    composable("register") {
                        RegisterScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            // Menambahkan parameter navigasi ke layar kode verifikasi
                            onNavigateToVerificationCode = { code ->
                                navController.navigate("show_code/$code") {
                                    // Hapus halaman register dari backstack agar user tidak bisa kembali ke form register
                                    popUpTo("login") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable(
                        route = "show_code/{code}",
                        arguments = listOf(navArgument("code") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val code = backStackEntry.arguments?.getString("code") ?: "-"
                        VerificationCodeDisplayScreen(
                            verificationCode = code,
                            onBackToLogin = { navController.popBackStack() }
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
                            },
                            onNavigateToKelolaAdmin = { navController.navigate("user_management/admin") },
                            onNavigateToKelolaKader = { navController.navigate("user_management/kader") },
                            onNavigateToVerifikasi = { navController.navigate("verification_screen") },
                            onNavigateToDaftarBalita = { navController.navigate("daftar_balita") }
                        )
                    }

                    composable(
                        route = "user_management/{role}",
                        arguments = listOf(navArgument("role") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val role = backStackEntry.arguments?.getString("role") ?: ""
                        UserManagementScreen(
                            roleToDisplay = role,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("verification_screen") {
                        VerificationScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    composable("daftar_balita") {
                        DaftarAnakScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToTambah = { navController.navigate("tambah_balita") }
                        )
                    }

                    composable("tambah_balita") {
                        TambahAnakScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}