package com.mindshift.controller

import com.mindshift.dto.LoginRequest
import com.mindshift.dto.ProfesionalRequest
import com.mindshift.service.ProfesionalService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/profesionales")
class ProfesionalController(private val service: ProfesionalService) {

    @GetMapping
    fun listar() = service.listar()

    // Literal /buscar debe declararse; Spring lo prioriza sobre /{id}
    @GetMapping("/buscar")
    fun buscar(@RequestParam(required = false) especialidad: String?) = service.buscar(especialidad)

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@RequestBody req: ProfesionalRequest) = service.crear(req)

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT, RequestMethod.PATCH])
    fun actualizar(@PathVariable id: Long, @RequestBody req: ProfesionalRequest) =
        service.actualizar(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest) = service.login(req)
}
