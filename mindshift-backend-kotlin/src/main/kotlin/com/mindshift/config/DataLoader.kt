package com.mindshift.config

import com.mindshift.model.Escenario
import com.mindshift.model.Opciones
import com.mindshift.repository.EscenarioRepository
import com.mindshift.repository.OpcionesRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Carga inicial de datos del cuestionario (equivale a los scripts importar_datos.py /
 * importar_opciones.py de Django). Lee los CSV de src/main/resources/data y solo se
 * ejecuta si la base de datos está vacía, para no duplicar en cada arranque.
 *
 * La relación opción -> escenario se reconstruye mapeando por el `id` del CSV, sin
 * depender de que las claves primarias de H2 coincidan con las de Django.
 */
@Component
class DataLoader(
    private val escenarioRepo: EscenarioRepository,
    private val opcionesRepo: OpcionesRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataLoader::class.java)

    @Transactional
    override fun run(vararg args: String) {
        if (escenarioRepo.count() > 0L) {
            log.info("La BD ya tiene {} escenarios; se omite la carga inicial.", escenarioRepo.count())
            return
        }

        // 1) Escenarios: categoria=rama, enunciado=pregunta, orden=id (la columna 'ambito' se ignora)
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

        // 2) Opciones: contenido=texto, valor_puntos=peso (la columna 'distorsion' se ignora)
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

    /**
     * Parser CSV mínimo (RFC 4180): respeta campos entre comillas con comas internas
     * y comillas escapadas ("").
     */
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
