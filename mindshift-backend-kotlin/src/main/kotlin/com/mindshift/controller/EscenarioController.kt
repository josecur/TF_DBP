package com.mindshift.controller

import com.mindshift.dto.EscenarioRequest
import com.mindshift.service.EscenarioService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/escenarios")
class EscenarioController(private val service: EscenarioService) {

    @GetMapping
    fun listar() = service.listar()

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@RequestBody req: EscenarioRequest) = service.crear(req)

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT, RequestMethod.PATCH])
    fun actualizar(@PathVariable id: Long, @RequestBody req: EscenarioRequest) =
        service.actualizar(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}
