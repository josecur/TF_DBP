package com.mindshift.model

import jakarta.persistence.*
import java.time.Instant

/**
 * Equivalente a `Reserva` del modelo Django (una cita entre usuario y profesional).
 */
@Entity
@Table(name = "reserva")
class Reserva(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    var usuario: Usuario? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_profesional")
    var profesional: Profesional? = null,

    @Column(length = 255)
    var estado: String = "Pendiente",

    var fecha: Instant = Instant.now(),

    @Column(length = 2000)
    var motivo: String = "Sin motivo especificado",

    @Column(name = "contacto_correo", length = 255)
    var contactoCorreo: String? = null,

    @Column(name = "contacto_whatsapp", length = 1000)
    var contactoWhatsapp: String? = null
)
