package com.example.tr2_process.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr2_process.model.Process
import com.example.tr2_process.ui.theme.ServiceViewModel

@Composable
fun ListProcess(navController: NavController, viewModel: ServiceViewModel) {

    LaunchedEffect(Unit) {
        viewModel.getAllProcess()
    }

    val processList by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        navController.navigate("hosts")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0), contentColor = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Hosts")
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(processList.llistaProcess) { process ->
                    ProcessRow(process, onStart = { id ->
                        viewModel.startService(id) { updatedProcess ->
                            if (updatedProcess != null) {
                                println("Service started successfully: ${updatedProcess.name}")
                            } else {
                                println("Error starting service")
                            }
                        }
                    }, onStop = { id ->
                        viewModel.stopService(id) { updatedProcess ->
                            if (updatedProcess != null) {
                                println("Service stopped successfully: ${updatedProcess.name}")
                            } else {
                                println("Error stopping service")
                            }
                        }
                    }, navController = navController)
                }
            }
        }
    }
}


@Composable
fun ProcessRow(process: Process, onStart: (String) -> Unit, onStop: (String) -> Unit, navController: NavController) {
    val processName = process.name ?: "Unknown Process"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                when (process.status) {
                                    "running" -> Color(0xFF4CAF50)
                                    "stopped" -> Color(0xFFF44336)
                                    else -> Color.Gray
                                }
                            )
                            .offset(x = (-12).dp, y = (-12).dp) // Adjust these values as needed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$processName (${process.status})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = process.enabled,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (process.enabled) {
                                "enabled" -> Color(0xFF4CAF50)
                                else -> Color(0xFFF44336)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (process.enabled != "enabled") {
                    Button(
                        onClick = {
                            println("Starting service for process ID: ${process.id}")
                            onStart(process.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)
                    ) {
                        Text("Start")
                    }
                } else {
                    Button(
                        onClick = {
                            println("Stopping service for process ID: ${process.id}")
                            onStop(process.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336), contentColor = Color.White)
                    ) {
                        Text("Stop")
                    }
                }

                Button(
                    onClick = {
                        navController.navigate("logs/${process.id}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White)
                ) {
                    Text("Logs")
                }

            }
        }
    }
}
