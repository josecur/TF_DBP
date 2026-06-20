package com.mindshift.controller

import com.mindshift.dto.OpcionRequest
import com.mindshift.service.OpcionesService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/opciones")
class OpcionesController(private val service: OpcionesService) {

    @GetMapping
    fun listar() = service.listar()

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@RequestBody req: OpcionRequest) = service.crear(req)

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT, RequestMethod.PATCH])
    fun actualizar(@PathVariable id: Long, @RequestBody req: OpcionRequest) =
        service.actualizar(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}
