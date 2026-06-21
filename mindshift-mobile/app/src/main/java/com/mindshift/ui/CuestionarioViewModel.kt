package com.mindshift.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindshift.data.ApiClient
import com.mindshift.data.model.Escenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Pasos del flujo del cuestionario (igual que el paso 'seleccion'/'cuestionario'/'resultados' del web). */
enum class Paso { SELECCION, CUESTIONARIO, RESULTADO }

data class CuestionarioState(
    val cargando: Boolean = true,
    val error: String? = null,
    val categorias: List<String> = emptyList(),
    val paso: Paso = Paso.SELECCION,
    val categoria: String? = null,
    val preguntas: List<Escenario> = emptyList(),
    val indice: Int = 0,
    val puntaje: Int = 0
) {
    val preguntaActual: Escenario? get() = preguntas.getOrNull(indice)
    val totalPreguntas: Int get() = preguntas.size
    val progreso: Float get() = if (preguntas.isEmpty()) 0f else indice.toFloat() / preguntas.size

    /** Mapeo de puntaje a nivel de riesgo (mismos umbrales que el flujo por categoría del web: 31 / 16). */
    val nivelRiesgo: String
        get() = when {
            puntaje >= 31 -> "CRÍTICO"
            puntaje >= 16 -> "MODERADO"
            else -> "BAJO"
        }
}

/**
 * Lógica del cuestionario. Reemplaza a CuestionarioComponent + TestStateService del frontend Angular:
 * carga los escenarios, filtra por categoría, acumula el puntaje y calcula el nivel de riesgo.
 */
class CuestionarioViewModel : ViewModel() {

    private val _state = MutableStateFlow(CuestionarioState())
    val state: StateFlow<CuestionarioState> = _state.asStateFlow()

    private var todos: List<Escenario> = emptyList()

    init {
        cargar()
    }

    fun cargar() {
        _state.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            try {
                todos = ApiClient.service.getEscenarios()
                val cats = todos.map { it.categoria }.distinct()
                _state.update {
                    it.copy(cargando = false, error = null, categorias = cats, paso = Paso.SELECCION)
                }
            } catch (e: Exception) {
                _state.update { it.copy(cargando = false, error = e.message ?: "Error de conexión") }
            }
        }
    }

    fun seleccionarCategoria(categoria: String) {
        val filtradas = todos.filter { it.categoria == categoria }.sortedBy { it.orden }
        _state.update {
            it.copy(
                categoria = categoria,
                preguntas = filtradas,
                indice = 0,
                puntaje = 0,
                paso = Paso.CUESTIONARIO
            )
        }
    }

    fun responder(valorPuntos: Int) {
        _state.update { s ->
            val nuevoPuntaje = s.puntaje + valorPuntos
            if (s.indice < s.preguntas.size - 1) {
                s.copy(puntaje = nuevoPuntaje, indice = s.indice + 1)
            } else {
                s.copy(puntaje = nuevoPuntaje, paso = Paso.RESULTADO)
            }
        }
    }

    fun reiniciar() {
        _state.update {
            it.copy(
                paso = Paso.SELECCION,
                categoria = null,
                preguntas = emptyList(),
                indice = 0,
                puntaje = 0
            )
        }
    }
}
