package com.mindshift.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindshift.data.ApiClient
import com.mindshift.data.SessionManager
import com.mindshift.data.model.EvaluacionDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Historial de evaluaciones del usuario logueado (GET /api/evaluaciones/?idUsuario=). */
class HistorialViewModel(app: Application) : AndroidViewModel(app) {

    private val sm = SessionManager(app)

    private val _estado = MutableStateFlow<Resource<List<EvaluacionDto>>>(Resource.Loading)
    val estado: StateFlow<Resource<List<EvaluacionDto>>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        _estado.value = Resource.Loading
        viewModelScope.launch {
            try {
                val s = sm.sesion.first()
                if (s == null || s.rol != "usuario") {
                    _estado.value = Resource.Error("Inicia sesión como usuario para ver tu historial.")
                    return@launch
                }
                _estado.value = Resource.Success(ApiClient.service.getEvaluaciones(s.id))
            } catch (e: Exception) {
                _estado.value = Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}
