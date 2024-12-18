package com.example.tr2_process.ui.theme

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.tr2_process.data.AppDatabase
import com.example.tr2_process.data.HostConfigEntity
import com.example.tr2_process.model.Process
import com.example.tr2_process.network.ApiService
import com.example.tr2_process.network.updateUrlHost
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

class ServiceViewModel(application: Application) : AndroidViewModel(application) {

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

    val hostConfigDao = db.hostConfigDao()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }
            getHostConfig()
            getAllHosts()
            updateUrlHost(hostConfigDao)
            getAllProcess()
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
            socket_process.on("updateHost", actualizarHosts)
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

    suspend fun updateUrl(): HostConfigEntity {
        val enabledHostConfig = hostConfigDao.getEnabled()
        return enabledHostConfig
    }

    suspend fun getAllProcess() {
        Log.i("getAllProcess", "Fetching processes")
        try {
            withContext(Dispatchers.IO) {
                updateUrlHost(hostConfigDao)
                val processList = ApiService.retrofitService.getProcess()
                Log.i("lista de los cojones", processList.toString())
                withContext(Dispatchers.Main) {
                    _uiState.value = LlistaProcessViewModel(processList)
                    Log.i("getAllProcess", processList.toString())
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error fetching processes: ${e.message}")
        }
    }

    private suspend fun getAllHosts() {
        val hostConfigList = hostConfigDao.getAll()
        _hostState.value = LlistaHostsViewModel(hostConfigList)
        Log.i("Hosts:", hostConfigList.toString())
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
            getAllHosts()
        }
     }

    fun deleteHostConfig(id: Int) {
        viewModelScope.launch {
            try {
                hostConfigDao.deleteData(id)
                getAllHosts()
                Log.i("Host deleted:", "$id")
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error deleting host config: ${e.message}")
            }
        }
    }

    fun emitDeleteHostEvent(hostId: Int) {
        socket_process.emit("updateHost", hostId)
        Log.i("Host deleted:", "$hostId")
    }

    fun updateViewHosts(hostId: Int) {
        viewModelScope.launch {
            socket_process.emit("updateHost", hostId)
            getAllHosts()
            Log.i("Host selected:", "$hostId")
        }
    }

    fun updateHostConfig(hostConfig: HostConfigEntity) {
        viewModelScope.launch {
            hostConfigDao.disableAll()
            hostConfigDao.enableById(hostConfig.id)
            updateUrlHost(hostConfigDao)
            reconnectSocket()
            getAllHosts() // Asegúrate de llamar a getAllHosts después de actualizar la configuración
            getAllProcess() // Llama a getAllProcess después de actualizar la configuración
            Log.i("List Hosts:", hostConfigDao.getAll().toString())
        }
    }
    private fun reconnectSocket() {
        socket_process.disconnect()
        connectSocket()
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