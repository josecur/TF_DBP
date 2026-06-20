package com.mindshift.dto

/** Respuesta pública de Profesional (sin `clave`). */
data class ProfesionalDto(
    val id: Long?,
    val nombreProfesional: String,
    val apellidoProfesional: String,
    val telefonoProfesional: String,
    val correoProfesional: String,
    val especialidad: String,
    val validacion: Int,
    val universidad: String,
    val avatar_icono: String?,
    val descripcion_trayectoria: String?,
    val enlace_agenda: String?,
    val generoProfesional: String?
)

/** Cuerpo para crear/actualizar Profesional (nullable = update parcial, como el PUT del panel). */
data class ProfesionalRequest(
    val nombreProfesional: String? = null,
    val apellidoProfesional: String? = null,
    val telefonoProfesional: String? = null,
    val correoProfesional: String? = null,
    val clave: String? = null,
    val especialidad: String? = null,
    val validacion: Int? = null,
    val universidad: String? = null,
    val avatar_icono: String? = null,
    val descripcion_trayectoria: String? = null,
    val enlace_agenda: String? = null,
    val generoProfesional: String? = null
)

data class ProfesionalLoginResponse(
    val status: String,
    val id: Long?,
    val nombreProfesional: String,
    val apellidoProfesional: String,
    val especialidad: String,
    val validacion: Int,
    val avatar_icono: String,
    val descripcion_trayectoria: String,
    val enlace_agenda: String,
    val generoProfesional: String
)
