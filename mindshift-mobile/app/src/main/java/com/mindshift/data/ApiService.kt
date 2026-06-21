package com.mindshift.data

import com.mindshift.data.model.Escenario
import com.mindshift.data.model.Profesional
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Definición de los endpoints del backend (equivale a los servicios HTTP de Angular).
 * Iremos agregando login, reservas, etc.
 */
interface ApiService {

    @GET("api/escenarios/")
    suspend fun getEscenarios(): List<Escenario>

    @GET("api/profesionales/")
    suspend fun getProfesionales(): List<Profesional>

    @GET("api/profesionales/{id}/")
    suspend fun getProfesional(@Path("id") id: Long): Profesional
}
