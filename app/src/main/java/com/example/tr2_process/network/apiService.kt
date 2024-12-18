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
import com.example.tr2_process.data.HostConfigDao

private var DEV_URL = "http://10.0.2.2:3001/"

private var retrofit = createRetrofitInstance(DEV_URL)



suspend fun updateUrlHost(hostConfigDao: HostConfigDao) {
    try {
        val enabledHostConfig = hostConfigDao.getEnabled()

        if (enabledHostConfig != null) {
            DEV_URL = "${enabledHostConfig.host}:${enabledHostConfig.port}/"
        } else {
            DEV_URL = "http://10.0.2.2:3000/"
        }

        // Recreate the Retrofit instance with the updated URL
        retrofit = createRetrofitInstance(DEV_URL)
        ApiService.retrofitService = retrofit.create(ServerApiService::class.java)
    } catch (e: Exception) {
        Log.e("updateUrlHost", "Error updating URL host: ${e.message}")
    }
}

private fun createRetrofitInstance(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
}
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
    lateinit var retrofitService: ServerApiService
}