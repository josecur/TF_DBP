package com.mindshift.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindshift.data.Session
import com.mindshift.data.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Observa la sesión activa y permite cerrarla. */
class PerfilViewModel(app: Application) : AndroidViewModel(app) {

    private val sm = SessionManager(app)

    val sesion: StateFlow<Session?> = sm.sesion.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun cerrarSesion() {
        viewModelScope.launch { sm.cerrar() }
    }
}
