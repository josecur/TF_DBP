package com.mindshift.service

import com.mindshift.dto.EscenarioDto
import com.mindshift.dto.EscenarioRequest
import com.mindshift.dto.toDto
import com.mindshift.model.Escenario
import com.mindshift.repository.EscenarioRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class EscenarioService(private val repo: EscenarioRepository) {

    @Transactional(readOnly = true)
    fun listar(): List<EscenarioDto> = repo.findAllByOrderByOrdenAsc().map { it.toDto() }

    @Transactional(readOnly = true)
    fun obtener(id: Long): EscenarioDto = buscar(id).toDto()

    @Transactional
    fun crear(req: EscenarioRequest): EscenarioDto {
        val e = Escenario(
            categoria = req.categoria ?: "",
            enunciado = req.enunciado ?: "",
            orden = req.orden ?: 1
        )
        return repo.save(e).toDto()
    }

    @Transactional
    fun actualizar(id: Long, req: EscenarioRequest): EscenarioDto {
        val e = buscar(id)
        req.categoria?.let { e.categoria = it }
        req.enunciado?.let { e.enunciado = it }
        req.orden?.let { e.orden = it }
        return repo.save(e).toDto()
    }

    @Transactional
    fun eliminar(id: Long) {
        if (!repo.existsById(id)) throw notFound()
        repo.deleteById(id)
    }

    private fun buscar(id: Long): Escenario =
        repo.findById(id).orElseThrow { notFound() }

    private fun notFound() = ResponseStatusException(HttpStatus.NOT_FOUND, "Escenario no encontrado")
}
