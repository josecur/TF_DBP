package com.mindshift.repository

import com.mindshift.model.Escenario
import com.mindshift.model.Evaluacion
import com.mindshift.model.Opciones
import com.mindshift.model.Profesional
import com.mindshift.model.Reserva
import com.mindshift.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repositorios Spring Data JPA. Cada uno reemplaza al `Model.objects` de Django.
 * Los métodos derivados ("query methods") generan el SQL automáticamente por su nombre.
 */

interface EscenarioRepository : JpaRepository<Escenario, Long> {
    // Equivale a: Escenario.objects.all().order_by('orden')
    fun findAllByOrderByOrdenAsc(): List<Escenario>
}

interface OpcionesRepository : JpaRepository<Opciones, Long> {
    fun findByEscenarioId(escenarioId: Long): List<Opciones>
}

interface UsuarioRepository : JpaRepository<Usuario, Long> {
    // Para el login: Usuario.objects.get(correoUsuario=...)
    fun findByCorreoUsuario(correoUsuario: String): Usuario?
}

interface ProfesionalRepository : JpaRepository<Profesional, Long> {
    fun findByCorreoProfesional(correoProfesional: String): Profesional?

    // Equivale a: Profesional.objects.filter(especialidad__icontains=...)
    fun findByEspecialidadContainingIgnoreCase(especialidad: String): List<Profesional>
}

interface ReservaRepository : JpaRepository<Reserva, Long> {
    // Equivale a: Reserva.objects.all().order_by('-fecha')
    fun findAllByOrderByFechaDesc(): List<Reserva>

    fun findByUsuarioIdOrderByFechaDesc(usuarioId: Long): List<Reserva>

    fun findByProfesionalIdOrderByFechaDesc(profesionalId: Long): List<Reserva>
}

interface EvaluacionRepository : JpaRepository<Evaluacion, Long> {
    fun findByUsuarioIdOrderByFechaDesc(usuarioId: Long): List<Evaluacion>
}
