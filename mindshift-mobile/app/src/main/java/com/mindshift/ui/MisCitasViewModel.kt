package com.mindshift.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindshift.data.ApiClient
import com.mindshift.data.SessionManager
import com.mindshift.data.model.ReservaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Lista las reservas del usuario logueado (GET /api/reservas/?idUsuario=). */
class MisCitasViewModel(app: Application) : AndroidViewModel(app) {

    private val sm = SessionManager(app)

    private val _estado = MutableStateFlow<Resource<List<ReservaDto>>>(Resource.Loading)
    val estado: StateFlow<Resource<List<ReservaDto>>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        _estado.value = Resource.Loading
        viewModelScope.launch {
            try {
                val s = sm.sesion.first()
                if (s == null || s.rol != "usuario") {
                    _estado.value = Resource.Error("Inicia sesión como usuario para ver tus citas.")
                    return@launch
                }
                _estado.value = Resource.Success(ApiClient.service.getReservasUsuario(s.id))
            } catch (e: Exception) {
                _estado.value = Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}
