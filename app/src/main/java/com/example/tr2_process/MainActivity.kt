package com.example.tr2_process

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tr2_process.data.HostConfigEntity
import com.example.tr2_process.ui.theme.ServiceViewModel
import com.example.tr2_process.ui.theme.Tr2processTheme
import com.example.tr2_process.ui.theme.screens.HostScreen
import com.example.tr2_process.ui.theme.screens.ListProcess
import com.example.tr2_process.ui.theme.screens.LogsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Tr2processTheme {
                val navController = rememberNavController()
                val viewModel = remember { ServiceViewModel(application) }

                val hostConfig = HostConfigEntity(name = "Host1", host = "http://10.0.2.2", port = "3000")
                viewModel.insertHostConfig(hostConfig)

                NavHost(navController, startDestination = "list") {
                    composable("list") { ListProcess(navController, viewModel) }
                    composable("logs/{processId}") { backStackEntry ->
                        val processId = backStackEntry.arguments?.getString("processId") ?: return@composable

                        LogsScreen(processId, viewModel, navController)
                    }
                    composable("hosts") { HostScreen(navController, viewModel)}
                }

            }
        }
    }
}
