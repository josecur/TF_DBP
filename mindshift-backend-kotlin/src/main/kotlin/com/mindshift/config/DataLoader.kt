package com.mindshift.config

import com.mindshift.model.Escenario
import com.mindshift.model.Opciones
import com.mindshift.model.Profesional
import com.mindshift.repository.EscenarioRepository
import com.mindshift.repository.OpcionesRepository
import com.mindshift.repository.ProfesionalRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Carga inicial de datos (equivale a los scripts importar_*.py de Django).
 * Cada bloque tiene su propio guardia (count == 0) para no duplicar en cada arranque.
 */
@Component
class DataLoader(
    private val escenarioRepo: EscenarioRepository,
    private val opcionesRepo: OpcionesRepository,
    private val profesionalRepo: ProfesionalRepository,
    private val encoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataLoader::class.java)

    @Transactional
    override fun run(vararg args: String) {
        cargarCuestionario()
        cargarProfesionalesDemo()
    }

    /** Escenarios + opciones desde los CSV de resources/data. */
    private fun cargarCuestionario() {
        if (escenarioRepo.count() > 0L) {
            log.info("La BD ya tiene {} escenarios; se omite la carga del cuestionario.", escenarioRepo.count())
            return
        }

        val mapaPorCsvId = HashMap<String, Escenario>()
        leerCsv("data/escenarios.csv").forEach { fila ->
            val csvId = fila["id"]?.trim().orEmpty()
            if (csvId.isEmpty()) return@forEach
            val guardado = escenarioRepo.save(
                Escenario(
                    categoria = fila["rama"]?.trim().orEmpty(),
                    enunciado = fila["pregunta"]?.trim().orEmpty(),
                    orden = csvId.toIntOrNull() ?: 0
                )
            )
            mapaPorCsvId[csvId] = guardado
        }
        log.info("Cargados {} escenarios.", mapaPorCsvId.size)

        var creadas = 0
        leerCsv("data/opciones.csv").forEach { fila ->
            val escenario = mapaPorCsvId[fila["escenario_id"]?.trim()] ?: return@forEach
            val peso = fila["peso"]?.trim()?.toIntOrNull() ?: return@forEach
            opcionesRepo.save(
                Opciones(
                    escenario = escenario,
                    contenido = fila["texto"]?.trim().orEmpty(),
                    valorPuntos = peso
                )
            )
            creadas++
        }
        log.info("Cargadas {} opciones.", creadas)
    }

    /** Especialistas de demostración (la BD original no traía ninguno sembrado). */
    private fun cargarProfesionalesDemo() {
        if (profesionalRepo.count() > 0L) {
            log.info("La BD ya tiene {} profesionales; se omite la carga demo.", profesionalRepo.count())
            return
        }

        val claveDemo = encoder.encode("especialista123")!!
        val demo = listOf(
            Profesional(
                nombreProfesional = "Lucía",
                apellidoProfesional = "Fernández",
                telefonoProfesional = "987654321",
                correoProfesional = "lucia.fernandez@mindshift.pe",
                clave = claveDemo,
                especialidad = "Ansiedad y estrés",
                validacion = 1,
                universidad = "USIL",
                descripcionTrayectoria = "Psicóloga clínica con 8 años de experiencia en terapia cognitivo-conductual para ansiedad social y estrés académico.",
                enlaceAgenda = "https://wa.me/51987654321",
                generoProfesional = "Femenino"
            ),
            Profesional(
                nombreProfesional = "Carlos",
                apellidoProfesional = "Mendoza",
                telefonoProfesional = "987111222",
                correoProfesional = "carlos.mendoza@mindshift.pe",
                clave = claveDemo,
                especialidad = "TDAH y burnout",
                validacion = 1,
                universidad = "UPC",
                descripcionTrayectoria = "Especialista en manejo de la atención, la procrastinación y el agotamiento en estudiantes universitarios.",
                enlaceAgenda = "https://wa.me/51987111222",
                generoProfesional = "Masculino"
            ),
            Profesional(
                nombreProfesional = "Andrea",
                apellidoProfesional = "Ríos",
                telefonoProfesional = "987333444",
                correoProfesional = "andrea.rios@mindshift.pe",
                clave = claveDemo,
                especialidad = "Depresión y apatía",
                validacion = 1,
                universidad = "PUCP",
                descripcionTrayectoria = "Acompañamiento terapéutico en cuadros depresivos, motivación y regulación emocional.",
                enlaceAgenda = "https://wa.me/51987333444",
                generoProfesional = "Femenino"
            )
        )
        profesionalRepo.saveAll(demo)
        log.info("Cargados {} profesionales demo.", demo.size)
    }

    /** Lee un CSV del classpath (UTF-8, posible BOM) y devuelve filas como mapas cabecera->valor. */
    private fun leerCsv(ruta: String): List<Map<String, String>> {
        val lineas = ClassPathResource(ruta).inputStream
            .bufferedReader(Charsets.UTF_8)
            .readLines()
        if (lineas.isEmpty()) return emptyList()

        val cabecera = parsearLinea(lineas.first().removePrefix("﻿"))
        return lineas.drop(1)
            .filter { it.isNotBlank() }
            .map { linea ->
                val valores = parsearLinea(linea)
                cabecera.mapIndexed { i, col -> col to (valores.getOrNull(i) ?: "") }.toMap()
            }
    }

    /** Parser CSV mínimo: respeta campos entre comillas con comas internas y comillas escapadas (""). */
    private fun parsearLinea(linea: String): List<String> {
        val resultado = mutableListOf<String>()
        val sb = StringBuilder()
        var enComillas = false
        var i = 0
        while (i < linea.length) {
            val c = linea[i]
            if (enComillas) {
                if (c == '"') {
                    if (i + 1 < linea.length && linea[i + 1] == '"') {
                        sb.append('"'); i++
                    } else {
                        enComillas = false
                    }
                } else {
                    sb.append(c)
                }
            } else {
                when (c) {
                    '"' -> enComillas = true
                    ',' -> { resultado.add(sb.toString()); sb.setLength(0) }
                    else -> sb.append(c)
                }
            }
            i++
        }
        resultado.add(sb.toString())
        return resultado
    }
}
