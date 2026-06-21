package com.mindshift.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindshift.data.ApiClient
import com.mindshift.data.SessionManager
import com.mindshift.data.model.ReservaDto
import com.mindshift.data.model.ReservaUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Panel del especialista: lista sus solicitudes (GET /reservas/por-profesional) y permite
 * aceptarlas (PATCH estado=Aceptado). Al aceptar, el backend revela el contacto al usuario.
 */
class PanelEspecialistaViewModel(app: Application) : AndroidViewModel(app) {

    private val sm = SessionManager(app)
    private var profId: Long = 0

    private val _estado = MutableStateFlow<Resource<List<ReservaDto>>>(Resource.Loading)
    val estado: StateFlow<Resource<List<ReservaDto>>> = _estado.asStateFlow()

    /** Id de la reserva que se está aceptando (para deshabilitar su botón). */
    private val _aceptando = MutableStateFlow<Long?>(null)
    val aceptando: StateFlow<Long?> = _aceptando.asStateFlow()

    init { cargar() }

    fun cargar() {
        _estado.value = Resource.Loading
        viewModelScope.launch {
            try {
                val s = sm.sesion.first()
                if (s == null || s.rol != "especialista") {
                    _estado.value = Resource.Error("Inicia sesión como especialista para ver tus solicitudes.")
                    return@launch
                }
                profId = s.id
                _estado.value = Resource.Success(ApiClient.service.getReservasProfesional(profId))
            } catch (e: Exception) {
                _estado.value = Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun aceptar(reservaId: Long) {
        _aceptando.value = reservaId
        viewModelScope.launch {
            try {
                ApiClient.service.aceptarReserva(reservaId, ReservaUpdateRequest("Aceptado"))
                _estado.value = Resource.Success(ApiClient.service.getReservasProfesional(profId))
            } catch (e: Exception) {
                // Si falla, dejamos la lista como está; el usuario puede reintentar.
            } finally {
                _aceptando.value = null
            }
        }
    }
}
