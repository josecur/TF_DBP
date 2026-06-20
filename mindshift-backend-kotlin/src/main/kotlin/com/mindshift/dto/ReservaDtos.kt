package com.mindshift.dto

import java.time.Instant

/**
 * Respuesta de Reserva. Incluye los campos calculados (SerializerMethodField en DRF):
 * alumno_nombre, alumno_correo, nivel_riesgo, medico_nombre, medico_contacto.
 * Los campos de contacto son write-only (solo entran, no salen) igual que en Django.
 */
data class ReservaDto(
    val id: Long?,
    val idUsuario: Long?,
    val idProfesional: Long?,
    val estado: String,
    val fecha: Instant?,
    val motivo: String,
    val alumno_nombre: String,
    val alumno_correo: String,
    val nivel_riesgo: String?,
    val medico_nombre: String,
    val medico_contacto: Map<String, Any?>
)

/** Cuerpo para crear (POST) o actualizar parcialmente (PATCH/PUT) una reserva. */
data class ReservaRequest(
    val idUsuario: Long? = null,
    val idProfesional: Long? = null,
    val estado: String? = null,
    val fecha: Instant? = null,
    val motivo: String? = null,
    val contacto_correo: String? = null,
    val contacto_whatsapp: String? = null
)
