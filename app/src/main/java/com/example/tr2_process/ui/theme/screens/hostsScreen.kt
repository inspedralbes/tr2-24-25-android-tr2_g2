package com.example.tr2_process.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tr2_process.ui.theme.ServiceViewModel
import com.example.tr2_process.data.HostConfigEntity

@Composable
fun HostScreen(navController: NavController, viewModel: ServiceViewModel) {
    // Obtener la lista de hosts del ViewModel
    val hostList = viewModel.hostState.collectAsState().value.hostConfigList

    // Si la lista de hosts está vacía, mostrar un mensaje
    if (hostList.isEmpty()) {
        Text("No hosts available")
    } else {
        // Mostrar una lista de hosts guardados
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(hostList) { host ->
                HostItem(host = host, onClick = {
                    // Cuando un host es seleccionado, guardamos el host en la configuración actual
                    viewModel.insertHostConfig(HostConfigEntity(name = host.name, host = host.host, port = host.port))
                    Toast.makeText(navController.context, "Host selected: ${host.name}", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}

@Composable
fun HostItem(host: HostConfigEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "Name: ${host.name}")
            Text(text = "Host: ${host.host}")
            Text(text = "Port: ${host.port}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onClick) {
                Text(text = "Select Host")
            }
        }
    }
}
