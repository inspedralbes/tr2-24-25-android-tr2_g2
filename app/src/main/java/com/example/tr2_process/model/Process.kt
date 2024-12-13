package com.example.tr2_process.model

data class LogEntry(
    val message: String,
    val timestamp: String
)

data class Process(
    val id: String,
    val name: String,
    val status: String,
    val enabled: String,
    val message: List<LogEntry>,
    val log: List<LogEntry>,
    val logError: List<LogEntry>
)