package com.mindshift.dto

import java.time.Instant

data class EvaluacionDto(
    val id: Long?,
    val idUsuario: Long?,
    val puntaje: Int,
    val nivel: String,
    val categoria: String?,
    val fecha: Instant?
)

/** Cuerpo para registrar una evaluación. `fecha` es opcional (el backend usa la actual). */
data class EvaluacionRequest(
    val idUsuario: Long? = null,
    val puntaje: Int? = null,
    val nivel: String? = null,
    val categoria: String? = null,
    val fecha: Instant? = null
)
