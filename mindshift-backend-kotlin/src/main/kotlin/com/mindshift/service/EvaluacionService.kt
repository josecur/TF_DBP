package com.mindshift.service

import com.mindshift.dto.EvaluacionDto
import com.mindshift.dto.EvaluacionRequest
import com.mindshift.dto.toDto
import com.mindshift.model.Evaluacion
import com.mindshift.repository.EvaluacionRepository
import com.mindshift.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class EvaluacionService(
    private val repo: EvaluacionRepository,
    private val usuarioRepo: UsuarioRepository
) {

    @Transactional(readOnly = true)
    fun porUsuario(idUsuario: Long): List<EvaluacionDto> =
        repo.findByUsuarioIdOrderByFechaDesc(idUsuario).map { it.toDto() }

    @Transactional
    fun crear(req: EvaluacionRequest): EvaluacionDto {
        val e = Evaluacion(
            puntaje = req.puntaje ?: 0,
            nivel = req.nivel ?: "",
            categoria = req.categoria,
            fecha = req.fecha ?: Instant.now()
        )
        req.idUsuario?.let { e.usuario = usuarioRepo.findById(it).orElse(null) }
        return repo.save(e).toDto()
    }
}
