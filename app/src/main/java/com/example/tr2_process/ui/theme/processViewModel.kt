package com.example.tr2_process.ui.theme

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.tr2_process.data.AppDatabase
import com.example.tr2_process.data.HostConfigDao
import com.example.tr2_process.data.HostConfigEntity
import com.example.tr2_process.model.Process
import com.example.tr2_process.network.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.socket.client.Socket
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LlistaProcessViewModel(var llistaProcess: List<Process> = emptyList())
data class LlistaHostsViewModel(var hostConfigList: List<HostConfigEntity> = emptyList())

class ServiceViewModel(application: Application, viewModel: ViewModel) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LlistaProcessViewModel())
    val uiState: StateFlow<LlistaProcessViewModel> = _uiState.asStateFlow()

    private val _hostState = MutableStateFlow(LlistaHostsViewModel())
    val hostState: StateFlow<LlistaHostsViewModel> = _hostState.asStateFlow()

    private val URL_SOCKET_DEV = "http://10.0.2.2:3000/"
    private val gson = Gson()
    private lateinit var socket_process: Socket

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "process_config"
    ).build()

    private val hostConfigDao = db.hostConfigDao()

//    init {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                db.clearAllTables()
//            }
//            getAllProcess()
//            getHostConfig()
//            connectSocket()
//        }
//    }

    init {
        viewModelScope.launch {
            getAllProcess()
            getHostConfig()
            getAllHosts()
        }
       connectSocket()
    }


    private fun connectSocket() {
        try{
            socket_process = IO.socket(URL_SOCKET_DEV)
        }catch (e: Exception){
            println("Error conectando socket: ${e.message}")
        }
        socket_process.connect()
        socket_process.on(Socket.EVENT_CONNECT){
            println("Socket conectado")
            socket_process.on("wsdata", actualizarServicios)
            socket_process.on("deleteHost", actualizarHosts)
        }
        socket_process.on(Socket.EVENT_DISCONNECT){
            println("Socket desconectado")
        }
    }

    private val actualizarServicios = Emitter.Listener { args ->
        val dataJson = args[0] as String
        println("data Json: $dataJson")

        val data = gson.fromJson(dataJson, Array<Process>::class.java).toList()
        println("Datos parseados: $data")
        updateProcessList(data)
    }

    private val actualizarHosts = Emitter.Listener { args ->
        val dataJson = args[0] as String
        println("data Json: $dataJson")

        val data = gson.fromJson(dataJson, Array<HostConfigEntity>::class.java).toList()
        updateHostList(data)
    }

    private fun updateHostList(newData: List<HostConfigEntity>) {
        _hostState.value = LlistaHostsViewModel(newData)
    }

    private fun updateProcessList(newData: List<Process>) {
        _uiState.value = LlistaProcessViewModel(newData)
    }

    private suspend fun getProcessFromApi(): List<Process> {
        return ApiService.retrofitService.getProcess()
    }

    private suspend fun getAllProcess() {
        val processList = getProcessFromApi()
        _uiState.value = LlistaProcessViewModel(processList)

    }

    private suspend fun getAllHosts(){
        val hostConfigList = hostConfigDao.getAll()
        _hostState.value = LlistaHostsViewModel(hostConfigList)
    }

    private suspend fun getHostConfig(){
        try{
            val hostConfigList = hostConfigDao.getAll()
            _hostState.value = LlistaHostsViewModel(hostConfigList)
        }catch(e: Exception){
            println("Error obteniendo configuración de host: ${e.message}")
        }
    }

    fun insertHostConfig(hostConfig: HostConfigEntity){
        viewModelScope.launch {
            hostConfigDao.insertData(hostConfig)
        }
     }

    fun deleteHostConfig(id: Int) {
        viewModelScope.launch {
            hostConfigDao.deleteData(id)
        }
    }

    fun emitDeleteHostEvent(hostId: Int) {
        socket_process.emit("deleteHost", hostId)
        Log.i("Host deleted:", "$hostId")
    }


    fun startService(id: String, onResult: (Process?) -> Unit) {
        viewModelScope.launch {
            try {
                val updatedProcess = ApiService.retrofitService.startService(id)
                onResult(updatedProcess)
                getAllProcess()
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun stopService(id: String, onResult: (Process?) -> Unit) {
        viewModelScope.launch {
            try {
                val updatedProcess = ApiService.retrofitService.stopService(id)
                onResult(updatedProcess)
                getAllProcess()
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

//    fun changeEnabledProcess(id: String, onResult: (Process?) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val updatedProcess = ApiService.retrofitService.changeEnabledProcess(id)
//                onResult(updatedProcess)
//                getAllProcess()
//            } catch (e: Exception) {
//                onResult(null)
//            }
//        }
//
//    }

}