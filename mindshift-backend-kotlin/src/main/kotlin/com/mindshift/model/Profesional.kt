package com.mindshift.model

import jakarta.persistence.*

/**
 * Equivalente a `Profesional` del modelo Django (el especialista).
 */
@Entity
@Table(name = "profesional")
class Profesional(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 255)
    var nombreProfesional: String = "",

    @Column(length = 255)
    var apellidoProfesional: String = "",

    @Column(length = 255)
    var telefonoProfesional: String = "",

    @Column(length = 255)
    var correoProfesional: String = "",

    @Column(length = 255)
    var clave: String = "",

    @Column(length = 255)
    var especialidad: String = "",

    var validacion: Int = 0,

    @Column(length = 255)
    var universidad: String = "",

    // Foto en Base64: puede ser muy grande -> CLOB
    @Lob
    @Column(name = "avatar_icono")
    var avatarIcono: String? = null,

    @Column(name = "descripcion_trayectoria", length = 4000)
    var descripcionTrayectoria: String? = null,

    @Column(name = "enlace_agenda", length = 1000)
    var enlaceAgenda: String? = null,

    @Column(length = 255)
    var generoProfesional: String? = null
)
