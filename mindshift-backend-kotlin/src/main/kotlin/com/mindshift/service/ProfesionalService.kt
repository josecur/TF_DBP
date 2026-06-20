package com.mindshift.service

import com.mindshift.dto.LoginRequest
import com.mindshift.dto.ProfesionalDto
import com.mindshift.dto.ProfesionalLoginResponse
import com.mindshift.dto.ProfesionalRequest
import com.mindshift.dto.toDto
import com.mindshift.model.Profesional
import com.mindshift.repository.ProfesionalRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ProfesionalService(
    private val repo: ProfesionalRepository,
    private val encoder: PasswordEncoder
) {

    @Transactional(readOnly = true)
    fun listar(): List<ProfesionalDto> = repo.findAll().map { it.toDto() }

    @Transactional(readOnly = true)
    fun obtener(id: Long): ProfesionalDto = buscarEntidad(id).toDto()

    @Transactional
    fun crear(req: ProfesionalRequest): ProfesionalDto {
        val p = Profesional(
            nombreProfesional = req.nombreProfesional ?: "",
            apellidoProfesional = req.apellidoProfesional ?: "",
            telefonoProfesional = req.telefonoProfesional ?: "",
            correoProfesional = req.correoProfesional ?: "",
            clave = encoder.encode(req.clave ?: "")!!,
            especialidad = req.especialidad ?: "",
            validacion = req.validacion ?: 0,
            universidad = req.universidad ?: "",
            avatarIcono = req.avatar_icono,
            descripcionTrayectoria = req.descripcion_trayectoria,
            enlaceAgenda = req.enlace_agenda,
            generoProfesional = req.generoProfesional
        )
        return repo.save(p).toDto()
    }

    @Transactional
    fun actualizar(id: Long, req: ProfesionalRequest): ProfesionalDto {
        val p = buscarEntidad(id)
        req.nombreProfesional?.let { p.nombreProfesional = it }
        req.apellidoProfesional?.let { p.apellidoProfesional = it }
        req.telefonoProfesional?.let { p.telefonoProfesional = it }
        req.correoProfesional?.let { p.correoProfesional = it }
        req.clave?.let { p.clave = encoder.encode(it)!! }
        req.especialidad?.let { p.especialidad = it }
        req.validacion?.let { p.validacion = it }
        req.universidad?.let { p.universidad = it }
        req.avatar_icono?.let { p.avatarIcono = it }
        req.descripcion_trayectoria?.let { p.descripcionTrayectoria = it }
        req.enlace_agenda?.let { p.enlaceAgenda = it }
        req.generoProfesional?.let { p.generoProfesional = it }
        return repo.save(p).toDto()
    }

    @Transactional
    fun eliminar(id: Long) {
        if (!repo.existsById(id)) throw notFound()
        repo.deleteById(id)
    }

    /** GET /api/profesionales/buscar?especialidad=  (filtro icontains de Django). */
    @Transactional(readOnly = true)
    fun buscar(especialidad: String?): List<ProfesionalDto> {
        val lista = if (!especialidad.isNullOrBlank()) {
            repo.findByEspecialidadContainingIgnoreCase(especialidad)
        } else {
            repo.findAll()
        }
        return lista.map { it.toDto() }
    }

    /** POST /api/profesionales/login/ */
    @Transactional(readOnly = true)
    fun login(req: LoginRequest): ProfesionalLoginResponse {
        val p = repo.findByCorreoProfesional(req.username ?: "") ?: throw credencialesInvalidas()
        if (!encoder.matches(req.password ?: "", p.clave)) throw credencialesInvalidas()
        return ProfesionalLoginResponse(
            status = "Login exitoso",
            id = p.id,
            nombreProfesional = p.nombreProfesional,
            apellidoProfesional = p.apellidoProfesional,
            especialidad = p.especialidad,
            validacion = p.validacion,
            avatar_icono = p.avatarIcono ?: "",
            descripcion_trayectoria = p.descripcionTrayectoria ?: "",
            enlace_agenda = p.enlaceAgenda ?: "",
            generoProfesional = p.generoProfesional ?: ""
        )
    }

    private fun buscarEntidad(id: Long): Profesional =
        repo.findById(id).orElseThrow { notFound() }

    private fun notFound() = ResponseStatusException(HttpStatus.NOT_FOUND, "Profesional no encontrado")
    private fun credencialesInvalidas() =
        ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")
}
