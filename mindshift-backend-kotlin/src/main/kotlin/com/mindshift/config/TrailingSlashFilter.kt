package com.mindshift.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Django REST agrega/acepta la barra final ("/api/usuarios/") automáticamente.
 * Spring MVC, en cambio, no la tolera por defecto y devolvería 404.
 *
 * Este filtro reenvía internamente cualquier petición que termine en "/" hacia la
 * misma ruta sin la barra, para que el frontend Angular funcione sin cambios.
 * Se excluye la consola H2, que sí necesita sus rutas con barra.
 */
@Component
@Order(value = -100)
class TrailingSlashFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI
        if (uri.length > 1 && uri.endsWith("/") && !uri.startsWith("/h2-console")) {
            val nuevaUri = uri.trimEnd('/')
            request.getRequestDispatcher(nuevaUri).forward(request, response)
        } else {
            filterChain.doFilter(request, response)
        }
    }
}
