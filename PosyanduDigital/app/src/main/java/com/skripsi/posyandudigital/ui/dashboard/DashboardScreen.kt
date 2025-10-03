package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.ui.theme.BackgroundLight

@Composable
fun DashboardScreen(
    userRole: String,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToKelolaAdmin: () -> Unit,
    onNavigateToKelolaKader: () -> Unit
) {
    val logoutCompleted = viewModel.logoutCompleted.value

    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            onLogout()
            viewModel.resetLogoutState()
        }
    }

    LaunchedEffect(key1 = userRole) {
        viewModel.loadDashboardData(userRole)
    }

    val state = viewModel.dashboardState.value

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundLight) {
        when (state) {
            is DashboardState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DashboardState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
            is DashboardState.SuperAdminData -> {
                SuperAdminDashboardScreen(
                    data = state.data,
                    onLogout = { viewModel.logout() },
                    onNavigateToKelolaAdmin = onNavigateToKelolaAdmin,
                    onNavigateToKelolaKader = onNavigateToKelolaKader
                )
            }
            is DashboardState.AdminData -> AdminDesaDashboardScreen(data = state.data, onLogout = { viewModel.logout() })
            is DashboardState.KaderData -> KaderDashboardScreen(data = state.data, onLogout = { viewModel.logout() })
            is DashboardState.OrangTuaData -> OrangTuaDashboardScreen(data = state.data, onLogout = { viewModel.logout() })
        }
    }
}

