package com.example.tr2_process.ui.theme.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

        Column {
            // Botón de "Back" para regresar a la pantalla anterior
            Button(
                onClick = {
                    navController.popBackStack()  // Regresar a la pantalla anterior
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White),
                modifier = Modifier.padding(bottom = 16.dp) // Espaciado debajo del botón
            ) {
                Text("Back")
            }
            // Botón de "Add" para añadir un nuevo host
            Button(
                onClick = {
                    navController.navigate("addHost")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White),
                modifier = Modifier.padding(bottom = 16.dp) // Espaciado debajo del botón
            ) {
                Text("Add")
            }
            // Mostrar una lista de hosts guardados
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(hostList) { host ->
                    HostItem(host = host, onClick = {
                        // Cuando un host es seleccionado, guardamos el host en la configuración actual
                        viewModel.insertHostConfig(HostConfigEntity(name = host.name, host = host.host, port = host.port))
                        Toast.makeText(navController.context, "Host selected: ${host.name}", Toast.LENGTH_SHORT).show()
                    }, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HostItem(host: HostConfigEntity, onClick: () -> Unit, viewModel: ServiceViewModel) {

    val processList by viewModel.uiState.collectAsState()

    LazyColumn {
        items(processList.llistaProcess) { process ->
            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "Name: ${host.name}")
                    Text(text = "Host: ${host.host}")
                    Text(text = "Port: ${host.port}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = onClick) {
                        Text(text = "Select Host")
                    }

                    Button(
                        onClick = {
                            viewModel.deleteHostConfig(host.id)
                            viewModel.emitDeleteHostEvent(host.id)

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Delete")
                    }
                }
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
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White)
        ) {
            Text("Add Host")
        }
    }
}
