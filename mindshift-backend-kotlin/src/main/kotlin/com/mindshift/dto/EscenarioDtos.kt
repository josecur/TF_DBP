package com.mindshift.dto

/**
 * DTOs del cuestionario. Los nombres de los campos replican EXACTAMENTE el JSON
 * que produce/consume el frontend Angular (algunos camelCase, otros snake_case),
 * por eso se respeta `valor_puntos` e `idEscenario` tal cual.
 */

data class OpcionDto(
    val id: Long?,
    val idEscenario: Long?,
    val contenido: String,
    val valor_puntos: Int
)

data class EscenarioDto(
    val id: Long?,
    val categoria: String,
    val enunciado: String,
    val orden: Int,
    val opciones: List<OpcionDto>
)

// --- Requests (creación / actualización) ---

data class EscenarioRequest(
    val categoria: String? = null,
    val enunciado: String? = null,
    val orden: Int? = null
)

data class OpcionRequest(
    val idEscenario: Long? = null,
    val contenido: String? = null,
    val valor_puntos: Int? = null
)
