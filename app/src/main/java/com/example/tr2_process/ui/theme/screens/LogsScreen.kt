package com.example.tr2_process.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr2_process.ui.theme.ServiceViewModel

@Composable
fun LogsScreen(processId: String, viewModel: ServiceViewModel, navController: NavController) {
    val process = viewModel.uiState.collectAsState().value.llistaProcess.find { it.id == processId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(top = 75.dp),
        contentAlignment = Alignment.Center
    ) {
        if (process != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (process.log.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Logs")
                        }
                        items(process.log) { logEntry ->
                            AnimatedLogCard(
                                message = logEntry.message,
                                timestamp = logEntry.timestamp,
                                color = Color(0xFF007BFF), // Azul
                                type = "LOG"
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "No logs available",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    if (process.logError.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Log Errors")
                        }
                        items(process.logError) { logErrorEntry ->
                            AnimatedLogCard(
                                message = logErrorEntry.message,
                                timestamp = logErrorEntry.timestamp,
                                color = Color(0xFFFF4D4D), // Rojo
                                type = "ERROR"
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "No log errors available",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    if (process.message.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Messages")
                        }
                        items(process.message) { message ->
                            AnimatedLogCard(
                                message = message.message,
                                timestamp = message.timestamp,
                                color = Color(0xFF212121), // Negro
                                type = "MESSAGE"
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "No messages available",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            // Mensaje si el proceso no se encuentra
            Text(
                text = "Process not found",
                color = MaterialTheme.colorScheme.error,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Back")
        }
    }
}


@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .background(Color(0xFFEEEEEE))
            .padding(8.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Composable
fun AnimatedLogCard(message: String, timestamp: String = "N/A", color: Color, type: String) {
    androidx.compose.animation.AnimatedVisibility(
        visible = true,
        enter = androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Tipo de log en la parte superior
                Text(
                    text = type,
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Mensaje
                Text(
                    text = message,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Timestamp en la parte inferior derecha
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = timestamp,
                        color = Color.DarkGray,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}
