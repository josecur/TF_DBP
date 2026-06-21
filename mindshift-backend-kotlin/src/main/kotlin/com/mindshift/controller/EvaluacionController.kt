package com.mindshift.controller

import com.mindshift.dto.EvaluacionRequest
import com.mindshift.service.EvaluacionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/evaluaciones")
class EvaluacionController(private val service: EvaluacionService) {

    @GetMapping
    fun listar(@RequestParam idUsuario: Long) = service.porUsuario(idUsuario)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@RequestBody req: EvaluacionRequest) = service.crear(req)
}
