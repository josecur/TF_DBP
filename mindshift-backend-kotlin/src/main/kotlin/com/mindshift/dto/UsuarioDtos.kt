package com.mindshift.dto

/** Respuesta pública de Usuario (sin `clave`, igual que el serializer con write_only). */
data class UsuarioDto(
    val id: Long?,
    val nombreUsuario: String,
    val apellidoUsuario: String,
    val telefonoUsuario: String,
    val correoUsuario: String,
    val nivel_riesgo: String?,
    val generoUsuario: String?
)

/** Cuerpo genérico para crear/actualizar Usuario (campos nullable = update parcial). */
data class UsuarioRequest(
    val nombreUsuario: String? = null,
    val apellidoUsuario: String? = null,
    val telefonoUsuario: String? = null,
    val correoUsuario: String? = null,
    val clave: String? = null,
    val nivel_riesgo: String? = null,
    val generoUsuario: String? = null
)

/** POST /api/usuarios/registro/ — mismo shape que envía el formulario Angular. */
data class RegistroRequest(
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val password: String? = null,
    val genero: String? = null,
    val puntaje: Int? = null
)

data class RegistroResponse(
    val status: String,
    val id: Long?,
    val nombres: String,
    val apellido: String,
    val correo: String,
    val nivel_riesgo: String?,
    val puntaje: Int
)

/** POST /login/ usa `username` (el correo) + `password`. */
data class LoginRequest(
    val username: String? = null,
    val password: String? = null
)

data class UsuarioLoginResponse(
    val status: String,
    val id: Long?,
    val nombres: String,
    val apellido: String,
    val correo: String,
    val nivel_riesgo: String?
)
