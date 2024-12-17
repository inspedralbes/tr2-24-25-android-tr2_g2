package com.example.tr2_process.network

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.example.tr2_process.data.AppDatabase
import com.example.tr2_process.model.Process
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import androidx.lifecycle.viewModelScope
import com.example.tr2_process.data.HostConfigDao


private var DEV_URL = "http://10.0.2.2:3000/"



suspend fun updateUrlHost(hostConfigDao: HostConfigDao) {

    val enabledHostConfig = hostConfigDao.getEnabled()

    if (enabledHostConfig != null) {
        DEV_URL = enabledHostConfig.host + ":" + enabledHostConfig.port + "/"
        retrofit = createRetrofitInstance(DEV_URL)
        Log.i("URL", DEV_URL)
    } else {
        DEV_URL = "http://10.0.2.2:3000/"
    }
}

private fun createRetrofitInstance(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
}

private var retrofit = createRetrofitInstance(DEV_URL)

interface ServerApiService {
    @GET("getProcess")
    suspend fun getProcess(): MutableList<Process>

    @GET("startService/{id}")
    suspend fun startService(@Path("id") id: String): Process

//    @PUT("changeEnabledProcess/{id}")
//    suspend fun changeEnabledProcess(@Path("id") id: String): Process

    @GET("stopService/{id}")
    suspend fun stopService(@Path("id") id: String): Process
//
//    @GET("getStatusService")
//    fun getStatusService(): Call<ProcessResponse>

}

object ApiService {
    val retrofitService: ServerApiService by lazy {
        retrofit.create(ServerApiService::class.java)
    }
}