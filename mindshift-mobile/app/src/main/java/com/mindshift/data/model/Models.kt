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

data class Profesional(
    val id: Long,
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

// --- Autenticación (mismos cuerpos/JSON que el backend) ---

/** POST /login/ usa `username` (el correo) + `password`. */
data class LoginRequest(
    val username: String,
    val password: String
)

data class UsuarioLoginResponse(
    val status: String,
    val id: Long,
    val nombres: String,
    val apellido: String,
    val correo: String,
    val nivel_riesgo: String?
)

/** POST /api/usuarios/registro/ — mismo shape que el formulario Angular. */
data class RegistroRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val password: String,
    val puntaje: Int
)

data class RegistroResponse(
    val status: String,
    val id: Long,
    val nombres: String,
    val apellido: String,
    val correo: String,
    val nivel_riesgo: String?,
    val puntaje: Int
)

data class ProfesionalLoginResponse(
    val status: String,
    val id: Long,
    val nombreProfesional: String,
    val apellidoProfesional: String,
    val especialidad: String,
    val validacion: Int,
    val avatar_icono: String?,
    val descripcion_trayectoria: String?,
    val enlace_agenda: String?,
    val generoProfesional: String?
)

// --- Reservas ---

/** POST /api/reservas/ — solicitar una cita. `fecha` en ISO-8601 (ej. Instant.now().toString()). */
data class ReservaRequest(
    val idUsuario: Long,
    val idProfesional: Long,
    val motivo: String,
    val fecha: String,
    val estado: String
)

/** Respuesta de la reserva creada (solo se usan id y estado; Gson ignora el resto). */
data class ReservaResponse(
    val id: Long,
    val estado: String
)
