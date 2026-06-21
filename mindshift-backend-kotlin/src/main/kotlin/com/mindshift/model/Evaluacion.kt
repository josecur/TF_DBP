package com.mindshift.model

import jakarta.persistence.*
import java.time.Instant

/**
 * Una evaluación (intento del cuestionario) de un usuario, con su puntaje y nivel.
 * Es nuevo respecto al Django original (que solo guardaba un `nivel_riesgo` por usuario):
 * permite tener historial por fecha.
 */
@Entity
@Table(name = "evaluacion")
class Evaluacion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    var usuario: Usuario? = null,

    var puntaje: Int = 0,

    @Column(length = 50)
    var nivel: String = "",

    @Column(length = 100)
    var categoria: String? = null,

    var fecha: Instant = Instant.now()
)
