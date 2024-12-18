package com.example.tr2_process

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tr2_process.data.HostConfigDao
import com.example.tr2_process.data.HostConfigEntity
import com.example.tr2_process.ui.theme.ServiceViewModel
import com.example.tr2_process.ui.theme.Tr2processTheme
import com.example.tr2_process.ui.theme.screens.AddHostScreen
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
                val hostConfig1 = HostConfigEntity(name = "Host1", host = "http://10.0.2.2", port = "3000")
                val hostConfig2 = HostConfigEntity(name = "Host2", host = "http://10.0.2.2", port = "3001", enable = true)
                viewModel.insertHostConfig(hostConfig1)
                viewModel.insertHostConfig(hostConfig2)

                NavHost(navController, startDestination = "list") {
                    composable("list") { ListProcess(navController, viewModel) }
                    composable("logs/{processId}") { backStackEntry ->
                        val processId = backStackEntry.arguments?.getString("proces" +
                                "sId") ?: return@composable

                        LogsScreen(processId, viewModel, navController)
                    }
                    composable("hosts") { HostScreen(navController, viewModel)}
                    composable("addHost") { AddHostScreen(navController, viewModel) }
                }

            }
        }
    }
}
