package com.mindshift.controller

import com.mindshift.dto.ReservaRequest
import com.mindshift.service.ReservaService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reservas")
class ReservaController(private val service: ReservaService) {

    @GetMapping
    fun listar(@RequestParam(required = false) idUsuario: Long?) = service.listar(idUsuario)

    @GetMapping("/por-usuario")
    fun porUsuario(@RequestParam idUsuario: Long) = service.porUsuario(idUsuario)

    @GetMapping("/por-profesional")
    fun porProfesional(@RequestParam idProfesional: Long) = service.porProfesional(idProfesional)

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@RequestBody req: ReservaRequest) = service.crear(req)

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT, RequestMethod.PATCH])
    fun actualizar(@PathVariable id: Long, @RequestBody req: ReservaRequest) =
        service.actualizar(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}
