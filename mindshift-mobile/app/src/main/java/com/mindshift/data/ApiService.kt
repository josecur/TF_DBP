package com.mindshift.data

import com.mindshift.data.model.Escenario
import retrofit2.http.GET

/**
 * Definición de los endpoints del backend (equivale a los servicios HTTP de Angular).
 * Por ahora solo el cuestionario; iremos agregando login, reservas, etc.
 */
interface ApiService {

    @GET("api/escenarios/")
    suspend fun getEscenarios(): List<Escenario>
}
