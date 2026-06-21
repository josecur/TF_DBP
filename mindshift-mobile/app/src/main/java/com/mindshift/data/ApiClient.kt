package com.mindshift.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente HTTP único (Retrofit) hacia el backend Spring Boot.
 *
 * BASE_URL = localhost porque usamos `adb reverse tcp:8000 tcp:8000`, que tunela el
 * localhost:8000 del celular hacia el PC por el cable USB (sin depender del WiFi ni del
 * firewall de Windows).
 *
 * Si algún día desarrollas por WiFi en vez de USB, cambia BASE_URL por la IP del PC,
 * por ejemplo: "http://192.168.0.104:8000/"
 */
object ApiClient {

    private const val BASE_URL = "http://localhost:8000/"

    val service: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
