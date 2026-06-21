package com.mindshift.data

import com.mindshift.data.model.Escenario
import com.mindshift.data.model.LoginRequest
import com.mindshift.data.model.Profesional
import com.mindshift.data.model.ProfesionalLoginResponse
import com.mindshift.data.model.RegistroRequest
import com.mindshift.data.model.RegistroResponse
import com.mindshift.data.model.ReservaDto
import com.mindshift.data.model.ReservaRequest
import com.mindshift.data.model.ReservaResponse
import com.mindshift.data.model.UsuarioLoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Definición de los endpoints del backend (equivale a los servicios HTTP de Angular).
 */
interface ApiService {

    @GET("api/escenarios/")
    suspend fun getEscenarios(): List<Escenario>

    @GET("api/profesionales/")
    suspend fun getProfesionales(): List<Profesional>

    @GET("api/profesionales/{id}/")
    suspend fun getProfesional(@Path("id") id: Long): Profesional

    @POST("api/usuarios/login/")
    suspend fun loginUsuario(@Body req: LoginRequest): UsuarioLoginResponse

    @POST("api/usuarios/registro/")
    suspend fun registroUsuario(@Body req: RegistroRequest): RegistroResponse

    @POST("api/profesionales/login/")
    suspend fun loginProfesional(@Body req: LoginRequest): ProfesionalLoginResponse

    @POST("api/reservas/")
    suspend fun crearReserva(@Body req: ReservaRequest): ReservaResponse

    @GET("api/reservas/")
    suspend fun getReservasUsuario(@Query("idUsuario") idUsuario: Long): List<ReservaDto>
}
