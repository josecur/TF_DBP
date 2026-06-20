package com.mindshift.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configuración general de la aplicación.
 * - CORS: permite que el frontend Angular (localhost:4200) consuma la API.
 *   Equivale a CORS_ALLOWED_ORIGINS de django-cors-headers.
 * - PasswordEncoder: BCrypt para hashear contraseñas (reemplaza el texto plano de Django).
 */
@Configuration
class AppConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
