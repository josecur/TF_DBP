package com.mindshift.service

import com.mindshift.dto.LoginRequest
import com.mindshift.dto.RegistroRequest
import com.mindshift.dto.RegistroResponse
import com.mindshift.dto.UsuarioDto
import com.mindshift.dto.UsuarioLoginResponse
import com.mindshift.dto.UsuarioRequest
import com.mindshift.dto.toDto
import com.mindshift.model.Usuario
import com.mindshift.repository.UsuarioRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UsuarioService(
    private val repo: UsuarioRepository,
    private val encoder: PasswordEncoder
) {

    @Transactional(readOnly = true)
    fun listar(): List<UsuarioDto> = repo.findAll().map { it.toDto() }

    @Transactional(readOnly = true)
    fun obtener(id: Long): UsuarioDto = buscar(id).toDto()

    @Transactional
    fun crear(req: UsuarioRequest): UsuarioDto {
        val u = Usuario(
            nombreUsuario = req.nombreUsuario ?: "",
            apellidoUsuario = req.apellidoUsuario ?: "",
            telefonoUsuario = req.telefonoUsuario ?: "",
            correoUsuario = req.correoUsuario ?: "",
            clave = encoder.encode(req.clave ?: "")!!,
            nivelRiesgo = req.nivel_riesgo,
            generoUsuario = req.generoUsuario
        )
        return repo.save(u).toDto()
    }

    @Transactional
    fun actualizar(id: Long, req: UsuarioRequest): UsuarioDto {
        val u = buscar(id)
        req.nombreUsuario?.let { u.nombreUsuario = it }
        req.apellidoUsuario?.let { u.apellidoUsuario = it }
        req.telefonoUsuario?.let { u.telefonoUsuario = it }
        req.correoUsuario?.let { u.correoUsuario = it }
        req.clave?.let { u.clave = encoder.encode(it)!! }
        req.nivel_riesgo?.let { u.nivelRiesgo = it }
        req.generoUsuario?.let { u.generoUsuario = it }
        return repo.save(u).toDto()
    }

    @Transactional
    fun eliminar(id: Long) {
        if (!repo.existsById(id)) throw notFound()
        repo.deleteById(id)
    }

    /**
     * POST /api/usuarios/registro/
     * Calcula el nivel de riesgo a partir del puntaje (mismo umbral que el Django original).
     */
    @Transactional
    fun registro(req: RegistroRequest): RegistroResponse {
        val puntaje = req.puntaje ?: 0
        val nivel = when {
            puntaje >= 18 -> "CRITICO"
            puntaje >= 10 -> "MODERADO"
            else -> "BAJO"
        }
        val u = Usuario(
            nombreUsuario = req.nombre ?: "",
            apellidoUsuario = req.apellido ?: "",
            correoUsuario = req.email ?: "",
            telefonoUsuario = req.telefono ?: "",
            clave = encoder.encode(req.password ?: "")!!,
            nivelRiesgo = nivel,
            generoUsuario = req.genero ?: ""
        )
        val saved = repo.save(u)
        return RegistroResponse(
            status = "Éxito",
            id = saved.id,
            nombres = saved.nombreUsuario,
            apellido = saved.apellidoUsuario,
            correo = saved.correoUsuario,
            nivel_riesgo = saved.nivelRiesgo,
            puntaje = puntaje
        )
    }

    /** POST /api/usuarios/login/ — verifica correo + contraseña (BCrypt). */
    @Transactional(readOnly = true)
    fun login(req: LoginRequest): UsuarioLoginResponse {
        val u = repo.findByCorreoUsuario(req.username ?: "") ?: throw credencialesInvalidas()
        if (!encoder.matches(req.password ?: "", u.clave)) throw credencialesInvalidas()
        return UsuarioLoginResponse(
            status = "Login exitoso",
            id = u.id,
            nombres = u.nombreUsuario,
            apellido = u.apellidoUsuario,
            correo = u.correoUsuario,
            nivel_riesgo = u.nivelRiesgo
        )
    }

    private fun buscar(id: Long): Usuario =
        repo.findById(id).orElseThrow { notFound() }

    private fun notFound() = ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
    private fun credencialesInvalidas() =
        ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")
}
