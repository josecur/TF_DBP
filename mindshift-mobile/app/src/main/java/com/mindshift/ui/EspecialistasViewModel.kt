package com.mindshift.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindshift.data.ApiClient
import com.mindshift.data.model.Profesional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Lista de especialistas (GET /api/profesionales/). */
class EspecialistasViewModel : ViewModel() {

    private val _estado = MutableStateFlow<Resource<List<Profesional>>>(Resource.Loading)
    val estado: StateFlow<Resource<List<Profesional>>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        _estado.value = Resource.Loading
        viewModelScope.launch {
            _estado.value = try {
                Resource.Success(ApiClient.service.getProfesionales())
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}

/**
 * Detalle de un especialista (GET /api/profesionales/{id}/).
 * Lee el id del argumento de navegación a través del SavedStateHandle.
 */
class EspecialistaDetalleViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val id: Long = savedStateHandle.get<String>("id")?.toLongOrNull() ?: 0L

    private val _estado = MutableStateFlow<Resource<Profesional>>(Resource.Loading)
    val estado: StateFlow<Resource<Profesional>> = _estado.asStateFlow()

    init { cargar() }

    fun cargar() {
        _estado.value = Resource.Loading
        viewModelScope.launch {
            _estado.value = try {
                Resource.Success(ApiClient.service.getProfesional(id))
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}
