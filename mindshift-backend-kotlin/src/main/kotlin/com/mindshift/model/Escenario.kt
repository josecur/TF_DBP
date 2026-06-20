package com.mindshift.model

import jakarta.persistence.*

/**
 * Equivalente a `Escenario` del modelo Django.
 * Representa una pregunta/situación del cuestionario, agrupada por categoría.
 */
@Entity
@Table(name = "escenario")
class Escenario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 500)
    var categoria: String = "",

    @Column(length = 4000)
    var enunciado: String = "",

    var orden: Int = 1,

    @OneToMany(
        mappedBy = "escenario",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var opciones: MutableList<Opciones> = mutableListOf()
)
