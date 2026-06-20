package com.mindshift.service

import com.mindshift.dto.OpcionDto
import com.mindshift.dto.OpcionRequest
import com.mindshift.dto.toDto
import com.mindshift.model.Opciones
import com.mindshift.repository.EscenarioRepository
import com.mindshift.repository.OpcionesRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class OpcionesService(
    private val repo: OpcionesRepository,
    private val escenarioRepo: EscenarioRepository
) {

    @Transactional(readOnly = true)
    fun listar(): List<OpcionDto> = repo.findAll().map { it.toDto() }

    @Transactional(readOnly = true)
    fun obtener(id: Long): OpcionDto = buscar(id).toDto()

    @Transactional
    fun crear(req: OpcionRequest): OpcionDto {
        val o = Opciones(
            contenido = req.contenido ?: "",
            valorPuntos = req.valor_puntos ?: 0
        )
        req.idEscenario?.let { o.escenario = escenarioRepo.findById(it).orElse(null) }
        return repo.save(o).toDto()
    }

    @Transactional
    fun actualizar(id: Long, req: OpcionRequest): OpcionDto {
        val o = buscar(id)
        req.contenido?.let { o.contenido = it }
        req.valor_puntos?.let { o.valorPuntos = it }
        req.idEscenario?.let { o.escenario = escenarioRepo.findById(it).orElse(null) }
        return repo.save(o).toDto()
    }

    @Transactional
    fun eliminar(id: Long) {
        if (!repo.existsById(id)) throw notFound()
        repo.deleteById(id)
    }

    private fun buscar(id: Long): Opciones =
        repo.findById(id).orElseThrow { notFound() }

    private fun notFound() = ResponseStatusException(HttpStatus.NOT_FOUND, "Opción no encontrada")
}
