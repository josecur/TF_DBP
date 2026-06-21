package com.mindshift.ui

/** Estado genérico de una carga remota: cargando / éxito / error. */
sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val mensaje: String) : Resource<Nothing>
}
