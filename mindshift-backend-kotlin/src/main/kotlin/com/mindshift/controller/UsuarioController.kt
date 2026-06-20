package com.mindshift.controller

import com.mindshift.dto.LoginRequest
import com.mindshift.dto.RegistroRequest
import com.mindshift.dto.UsuarioRequest
import com.mindshift.service.UsuarioService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/usuarios")
class UsuarioController(private val service: UsuarioService) {

    @GetMapping
    fun listar() = service.listar()

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@RequestBody req: UsuarioRequest) = service.crear(req)

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT, RequestMethod.PATCH])
    fun actualizar(@PathVariable id: Long, @RequestBody req: UsuarioRequest) =
        service.actualizar(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)

    // --- Acciones personalizadas (equivalen a @action en DRF) ---

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    fun registro(@RequestBody req: RegistroRequest) = service.registro(req)

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest) = service.login(req)
}
