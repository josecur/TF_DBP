package com.mindshift.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindshift.data.ApiClient
import com.mindshift.data.Session
import com.mindshift.data.SessionManager
import com.mindshift.data.model.ReservaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class ReservaUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false
)

/**
 * Maneja la solicitud de cita desde el detalle del especialista.
 * Conoce la sesión activa (para tomar el id del usuario) igual que reserva-modal del web.
 */
class ReservaViewModel(app: Application) : AndroidViewModel(app) {

    private val sm = SessionManager(app)

    val sesion: StateFlow<Session?> = sm.sesion.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _estado = MutableStateFlow(ReservaUiState())
    val estado: StateFlow<ReservaUiState> = _estado.asStateFlow()

    fun solicitarCita(idProfesional: Long, motivo: String) {
        val s = sesion.value
        if (s == null || s.rol != "usuario") {
            _estado.value = ReservaUiState(error = "Inicia sesión como usuario para solicitar una cita.")
            return
        }
        _estado.value = ReservaUiState(cargando = true)
        viewModelScope.launch {
            try {
                ApiClient.service.crearReserva(
                    ReservaRequest(
                        idUsuario = s.id,
                        idProfesional = idProfesional,
                        motivo = motivo.ifBlank { "Sin motivo especificado" },
                        fecha = ahoraIso(),
                        estado = "Pendiente"
                    )
                )
                _estado.value = ReservaUiState(exito = true)
            } catch (e: Exception) {
                _estado.value = ReservaUiState(error = e.message ?: "No se pudo enviar la solicitud.")
            }
        }
    }

    /** Fecha-hora actual en ISO-8601 UTC, sin usar java.time (compatible con minSdk 24). */
    private fun ahoraIso(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        fmt.timeZone = TimeZone.getTimeZone("UTC")
        return fmt.format(Date())
    }
}
