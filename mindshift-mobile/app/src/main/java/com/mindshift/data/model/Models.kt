package com.mindshift.data.model

/**
 * Modelos que reflejan el JSON del backend Spring Boot.
 * Los nombres de los campos coinciden con las claves JSON (Gson los mapea por nombre),
 * por eso se respeta `valor_puntos` e `idEscenario` tal cual los devuelve la API.
 */

data class Opcion(
    val id: Long,
    val idEscenario: Long?,
    val contenido: String,
    val valor_puntos: Int
)

data class Escenario(
    val id: Long,
    val categoria: String,
    val enunciado: String,
    val orden: Int,
    val opciones: List<Opcion>
)
