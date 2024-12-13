package com.example.tr2_process.network

import com.example.tr2_process.model.Process
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

private const val DEV_URL = "http://10.0.2.2:3000/"
private const val PROD_URL = ""

private val retrofit = Retrofit.Builder()
    .baseUrl(DEV_URL)
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    .build()

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