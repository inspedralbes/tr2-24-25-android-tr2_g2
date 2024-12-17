package com.example.tr2_process.ui.theme.screens

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tr2_process.ui.theme.ServiceViewModel
import com.example.tr2_process.data.HostConfigEntity
import kotlinx.coroutines.launch


@Composable
fun HostScreen(navController: NavController, viewModel: ServiceViewModel) {
    val hostList = viewModel.hostState.collectAsState().value.hostConfigList

    LaunchedEffect(Unit) {
        viewModel.getAllProcess()
    }

    if (hostList.isEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("No hosts available")
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Back")
                }
                Button(
                    onClick = { navController.navigate("addHost") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Add")
                }
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Back")
                }
                Button(
                    onClick = { navController.navigate("addHost") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Add")
                }
            }
            items(hostList) { host ->
                HostItem(host = host, onClick = {
                    viewModel.insertHostConfig(HostConfigEntity(name = host.name, host = host.host, port = host.port))
                    Log.i("Host selected:", host.name)
                }, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HostItem(host: HostConfigEntity, onClick: () -> Unit, viewModel: ServiceViewModel) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            if(host.enable){
                Text(text = "Enable", color = Color.Green)
            } else {
                Text(text = "Disable", color = Color.Red)
            }

            Text(text = "Name: ${host.name}")
            Text(text = "Host: ${host.host}")
            Text(text = "Port: ${host.port}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                viewModel.updateHostConfig(host)
                viewModel.viewModelScope.launch {
                    viewModel.updateUrl()
                    viewModel.updateViewHosts(hostId = host.id)
                }
            }) {
                Text(text = "Select Host")
            }

            Button(
                onClick = {
                    viewModel.deleteHostConfig(host.id)
                    viewModel.emitDeleteHostEvent(host.id)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
            ) {
                Text(text = "Delete")
            }
        }
    }
}

@Composable
fun AddHostScreen(navController: NavController, viewModel: ServiceViewModel) {
    var name by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = host,
            onValueChange = { host = it },
            label = { Text("Host") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = port,
            onValueChange = { port = it },
            label = { Text("Port") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val newHost = HostConfigEntity(name = name, host = host, port = port)
                viewModel.insertHostConfig(newHost)

                navController.popBackStack()
                Log.i("Host added:", name)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White)
        ) {
            Text("Add Host")
        }
    }
}
