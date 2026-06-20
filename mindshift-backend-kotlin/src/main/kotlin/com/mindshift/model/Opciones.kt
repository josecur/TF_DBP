package com.mindshift.model

import jakarta.persistence.*

/**
 * Equivalente a `Opciones` del modelo Django.
 * Cada opción pertenece a un Escenario (FK `idEscenario` en el JSON) y aporta puntos.
 */
@Entity
@Table(name = "opciones")
class Opciones(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_escenario")
    var escenario: Escenario? = null,

    @Column(length = 2000)
    var contenido: String = "",

    @Column(name = "valor_puntos")
    var valorPuntos: Int = 0
)
