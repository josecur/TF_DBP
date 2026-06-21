package com.mindshift.dto

import com.mindshift.model.Escenario
import com.mindshift.model.Evaluacion
import com.mindshift.model.Opciones
import com.mindshift.model.Profesional
import com.mindshift.model.Reserva
import com.mindshift.model.Usuario

/** Conversión de entidades JPA a DTOs de respuesta (equivale a `serializer.data` en DRF). */

fun Opciones.toDto() = OpcionDto(
    id = id,
    idEscenario = escenario?.id,
    contenido = contenido,
    valor_puntos = valorPuntos
)

fun Escenario.toDto() = EscenarioDto(
    id = id,
    categoria = categoria,
    enunciado = enunciado,
    orden = orden,
    opciones = opciones.map { it.toDto() }
)

fun Usuario.toDto() = UsuarioDto(
    id = id,
    nombreUsuario = nombreUsuario,
    apellidoUsuario = apellidoUsuario,
    telefonoUsuario = telefonoUsuario,
    correoUsuario = correoUsuario,
    nivel_riesgo = nivelRiesgo,
    generoUsuario = generoUsuario
)

fun Profesional.toDto() = ProfesionalDto(
    id = id,
    nombreProfesional = nombreProfesional,
    apellidoProfesional = apellidoProfesional,
    telefonoProfesional = telefonoProfesional,
    correoProfesional = correoProfesional,
    especialidad = especialidad,
    validacion = validacion,
    universidad = universidad,
    avatar_icono = avatarIcono,
    descripcion_trayectoria = descripcionTrayectoria,
    enlace_agenda = enlaceAgenda,
    generoProfesional = generoProfesional
)

fun Evaluacion.toDto() = EvaluacionDto(
    id = id,
    idUsuario = usuario?.id,
    puntaje = puntaje,
    nivel = nivel,
    categoria = categoria,
    fecha = fecha
)

fun Reserva.toDto(): ReservaDto {
    val aceptado = estado == "Aceptado"
    val u = usuario
    val p = profesional

    val alumnoNombre =
        if (u != null) "${u.nombreUsuario} ${u.apellidoUsuario}" else "Usuario no encontrado"
    val medicoNombre =
        if (p != null) "Dr(a). ${p.nombreProfesional} ${p.apellidoProfesional}" else "Profesional no asignado"
    val alumnoCorreo = if (aceptado && u != null) u.correoUsuario else "Oculto"
    val nivel = if (aceptado && u != null) u.nivelRiesgo else "Bloqueado"

    val medicoContacto: Map<String, Any?> = if (aceptado && p != null) {
        mapOf(
            "telefono" to p.telefonoProfesional,
            "correo" to (contactoCorreo ?: p.correoProfesional),
            "enlace" to (contactoWhatsapp ?: p.enlaceAgenda)
        )
    } else {
        mapOf("mensaje" to "Privado")
    }

    return ReservaDto(
        id = id,
        idUsuario = u?.id,
        idProfesional = p?.id,
        estado = estado,
        fecha = fecha,
        motivo = motivo,
        alumno_nombre = alumnoNombre,
        alumno_correo = alumnoCorreo,
        nivel_riesgo = nivel,
        medico_nombre = medicoNombre,
        medico_contacto = medicoContacto
    )
}
