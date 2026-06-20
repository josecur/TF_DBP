package com.mindshift.service

import com.mindshift.dto.ReservaDto
import com.mindshift.dto.ReservaRequest
import com.mindshift.dto.toDto
import com.mindshift.model.Reserva
import com.mindshift.repository.ProfesionalRepository
import com.mindshift.repository.ReservaRepository
import com.mindshift.repository.UsuarioRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class ReservaService(
    private val repo: ReservaRepository,
    private val usuarioRepo: UsuarioRepository,
    private val profesionalRepo: ProfesionalRepository
) {

    /** GET /api/reservas?idUsuario= — lista todo (orden -fecha) o filtra por usuario. */
    @Transactional(readOnly = true)
    fun listar(idUsuario: Long?): List<ReservaDto> {
        val base = if (idUsuario != null) {
            repo.findByUsuarioIdOrderByFechaDesc(idUsuario)
        } else {
            repo.findAllByOrderByFechaDesc()
        }
        return base.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun obtener(id: Long): ReservaDto = buscar(id).toDto()

    @Transactional
    fun crear(req: ReservaRequest): ReservaDto {
        val r = Reserva(
            estado = req.estado ?: "Pendiente",
            fecha = req.fecha ?: Instant.now(),
            motivo = req.motivo ?: "Sin motivo especificado",
            contactoCorreo = req.contacto_correo,
            contactoWhatsapp = req.contacto_whatsapp
        )
        req.idUsuario?.let { r.usuario = usuarioRepo.findById(it).orElse(null) }
        req.idProfesional?.let { r.profesional = profesionalRepo.findById(it).orElse(null) }
        return repo.save(r).toDto()
    }

    /** PUT/PATCH /api/reservas/{id} — actualización parcial (el panel envía solo estado + contacto). */
    @Transactional
    fun actualizar(id: Long, req: ReservaRequest): ReservaDto {
        val r = buscar(id)
        req.estado?.let { r.estado = it }
        req.motivo?.let { r.motivo = it }
        req.fecha?.let { r.fecha = it }
        req.contacto_correo?.let { r.contactoCorreo = it }
        req.contacto_whatsapp?.let { r.contactoWhatsapp = it }
        req.idUsuario?.let { r.usuario = usuarioRepo.findById(it).orElse(null) }
        req.idProfesional?.let { r.profesional = profesionalRepo.findById(it).orElse(null) }
        return repo.save(r).toDto()
    }

    @Transactional
    fun eliminar(id: Long) {
        if (!repo.existsById(id)) throw notFound()
        repo.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun porUsuario(idUsuario: Long): List<ReservaDto> =
        repo.findByUsuarioIdOrderByFechaDesc(idUsuario).map { it.toDto() }

    @Transactional(readOnly = true)
    fun porProfesional(idProfesional: Long): List<ReservaDto> =
        repo.findByProfesionalIdOrderByFechaDesc(idProfesional).map { it.toDto() }

    private fun buscar(id: Long): Reserva =
        repo.findById(id).orElseThrow { notFound() }

    private fun notFound() = ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada")
}
