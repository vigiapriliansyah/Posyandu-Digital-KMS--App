package com.skripsi.posyandudigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.ui.anak.DaftarAnakScreen
import com.skripsi.posyandudigital.ui.anak.TambahAnakScreen
import com.skripsi.posyandudigital.ui.dashboard.DashboardScreen
import com.skripsi.posyandudigital.ui.kader.VerificationScreen
import com.skripsi.posyandudigital.ui.login.LoginScreen
import com.skripsi.posyandudigital.ui.orangtua.VerificationCodeDisplayScreen
import com.skripsi.posyandudigital.ui.register.RegisterScreen
import com.skripsi.posyandudigital.ui.theme.PosyanduDigitalTheme
import com.skripsi.posyandudigital.ui.usermanagement.UserManagementScreen
import kotlinx.coroutines.flow.firstOrNull

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PosyanduDigitalTheme {
                val context = LocalContext.current
                val sessionManager = remember { SessionManager(context) }
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val token = sessionManager.getToken().firstOrNull()
                    val role = sessionManager.getRole().firstOrNull()
                    if (!token.isNullOrBlank() && !role.isNullOrBlank()) {
                        startDestination = "dashboard/${role.lowercase()}"
                    } else {
                        startDestination = "login"
                    }
                }

                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = startDestination!!) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = { userRole ->
                                    navController.navigate("dashboard/${userRole.lowercase()}") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { navController.navigate("register") },
                                onNavigateToVerificationCode = { code -> navController.navigate("show_code/$code") }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToVerificationCode = { code ->
                                    navController.navigate("show_code/$code") { popUpTo("login") { inclusive = false } }
                                }
                            )
                        }

                        composable("show_code/{code}", arguments = listOf(navArgument("code") { type = NavType.StringType })) { backStackEntry ->
                            val code = backStackEntry.arguments?.getString("code") ?: "-"
                            VerificationCodeDisplayScreen(verificationCode = code, onBackToLogin = { navController.popBackStack() })
                        }

                        composable("dashboard/{role}", arguments = listOf(navArgument("role") { type = NavType.StringType })) { backStackEntry ->
                            val role = backStackEntry.arguments?.getString("role")
                                    DashboardScreen(
                                userRole = role ?: "kader",
                                onLogout = {
                                    navController.navigate("login") { popUpTo(navController.graph.findStartDestination().id) { inclusive = true } }
                                },
                                onNavigateToKelolaAdmin = { navController.navigate("user_management/admin") },
                                onNavigateToKelolaKader = { navController.navigate("user_management/kader") },
                                onNavigateToVerifikasi = { navController.navigate("verification_screen") },
                                onNavigateToDaftarBalita = { navController.navigate("daftar_balita") },
                                onNavigateToTambahAnak = { navController.navigate("tambah_balita") },
                                onNavigateToDetailAnak = { id, nama, umur, jk ->
                                    navController.navigate("detail_anak/$id/$nama/$umur/$jk")
                                }
                            )
                        }

                        composable("user_management/{role}", arguments = listOf(navArgument("role") { type = NavType.StringType })) { backStackEntry ->
                            val role = backStackEntry.arguments?.getString("role") ?: ""
                            UserManagementScreen(roleToDisplay = role, onNavigateBack = { navController.popBackStack() })
                        }

                        composable("verification_screen") { VerificationScreen(onNavigateBack = { navController.popBackStack() }) }
                        composable("tambah_balita") { TambahAnakScreen(onNavigateBack = { navController.popBackStack() }) }

                        composable("daftar_balita") {
                            DaftarAnakScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToTambah = { navController.navigate("tambah_balita") },
                                onNavigateToDetail = { id, nama, umur, jk ->
                                    navController.navigate("detail_anak/$id/$nama/$umur/$jk")
                                }
                            )
                        }

                        // PERBAIKAN: Rute Detail Anak wajib memuat Jenis Kelamin (jk)
                        composable(
                            route = "detail_anak/{anakId}/{namaAnak}/{umurBulan}/{jenisKelamin}",
                            arguments = listOf(
                                navArgument("anakId") { type = NavType.IntType },
                                navArgument("namaAnak") { type = NavType.StringType },
                                navArgument("umurBulan") { type = NavType.IntType },
                                navArgument("jenisKelamin") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val anakId = backStackEntry.arguments?.getInt("anakId") ?: 0
                            val namaAnak = backStackEntry.arguments?.getString("namaAnak") ?: ""
                            val umurBulan = backStackEntry.arguments?.getInt("umurBulan") ?: 0
                            val jenisKelamin = backStackEntry.arguments?.getString("jenisKelamin") ?: "L"

                            com.skripsi.posyandudigital.ui.pengukuran.DetailAnakScreen(
                                anakId = anakId, namaAnak = namaAnak, umurBulan = umurBulan, jenisKelamin = jenisKelamin,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToInput = { id, nama, umur, jk ->
                                    navController.navigate("input_pengukuran/$id/$nama/$umur/$jk")
                                }
                            )
                        }

                        // PERBAIKAN: Rute Input KMS wajib memuat Jenis Kelamin (jk)
                        composable(
                            route = "input_pengukuran/{anakId}/{namaAnak}/{umurBulan}/{jenisKelamin}",
                            arguments = listOf(
                                navArgument("anakId") { type = NavType.IntType },
                                navArgument("namaAnak") { type = NavType.StringType },
                                navArgument("umurBulan") { type = NavType.IntType },
                                navArgument("jenisKelamin") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val anakId = backStackEntry.arguments?.getInt("anakId") ?: 0
                            val namaAnak = backStackEntry.arguments?.getString("namaAnak") ?: ""
                            val umurBulan = backStackEntry.arguments?.getInt("umurBulan") ?: 0
                            val jenisKelamin = backStackEntry.arguments?.getString("jenisKelamin") ?: "L"

                            com.skripsi.posyandudigital.ui.pengukuran.InputPengukuranScreen(
                                anakId = anakId, namaAnak = namaAnak, umurBulan = umurBulan, jenisKelamin = jenisKelamin,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}