package com.mindshift.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindshift.data.ApiClient
import com.mindshift.data.Session
import com.mindshift.data.SessionManager
import com.mindshift.data.model.LoginRequest
import com.mindshift.data.model.RegistroRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class AuthUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false
)

/**
 * Lógica de login (usuario y especialista) y registro. Al autenticar, guarda la sesión
 * con SessionManager (DataStore). Reemplaza login.component.ts + formulario-registro.component.ts.
 */
class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val sm = SessionManager(app)

    private val _estado = MutableStateFlow(AuthUiState())
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    fun login(esEspecialista: Boolean, usuario: String, clave: String) {
        if (usuario.isBlank() || clave.isBlank()) {
            _estado.value = AuthUiState(error = "Completa correo y contraseña.")
            return
        }
        _estado.value = AuthUiState(cargando = true)
        viewModelScope.launch {
            try {
                val sesion = if (esEspecialista) {
                    val r = ApiClient.service.loginProfesional(LoginRequest(usuario.trim(), clave))
                    Session(
                        rol = "especialista",
                        id = r.id,
                        nombre = "${r.nombreProfesional} ${r.apellidoProfesional}",
                        correo = usuario.trim(),
                        especialidad = r.especialidad
                    )
                } else {
                    val r = ApiClient.service.loginUsuario(LoginRequest(usuario.trim(), clave))
                    Session(
                        rol = "usuario",
                        id = r.id,
                        nombre = "${r.nombres} ${r.apellido}",
                        correo = r.correo,
                        nivelRiesgo = r.nivel_riesgo
                    )
                }
                sm.guardar(sesion)
                _estado.value = AuthUiState(exito = true)
            } catch (e: Exception) {
                _estado.value = AuthUiState(error = mensajeError(e))
            }
        }
    }

    fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        password: String,
        confirmar: String
    ) {
        when {
            nombre.isBlank() || apellido.isBlank() || email.isBlank() || telefono.isBlank() || password.isBlank() ->
                { _estado.value = AuthUiState(error = "Completa todos los campos."); return }
            password != confirmar ->
                { _estado.value = AuthUiState(error = "Las contraseñas no coinciden."); return }
        }
        _estado.value = AuthUiState(cargando = true)
        viewModelScope.launch {
            try {
                val r = ApiClient.service.registroUsuario(
                    RegistroRequest(nombre.trim(), apellido.trim(), email.trim(), telefono.trim(), password, 0)
                )
                sm.guardar(
                    Session(
                        rol = "usuario",
                        id = r.id,
                        nombre = "${r.nombres} ${r.apellido}",
                        correo = r.correo,
                        nivelRiesgo = r.nivel_riesgo
                    )
                )
                _estado.value = AuthUiState(exito = true)
            } catch (e: Exception) {
                _estado.value = AuthUiState(error = mensajeError(e))
            }
        }
    }

    private fun mensajeError(e: Exception): String = when {
        e is HttpException && e.code() == 401 -> "Correo o contraseña incorrectos."
        e is HttpException && e.code() == 400 -> "Revisa los datos (¿el correo ya está registrado?)."
        else -> e.message ?: "Error de conexión."
    }
}
