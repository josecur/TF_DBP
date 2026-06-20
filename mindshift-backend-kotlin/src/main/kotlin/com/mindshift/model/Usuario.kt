package com.mindshift.model

import jakarta.persistence.*

/**
 * Equivalente a `Usuario` del modelo Django (el paciente/alumno).
 * La `clave` se almacena hasheada con BCrypt (mejora frente al texto plano original).
 */
@Entity
@Table(name = "usuario")
class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 255)
    var nombreUsuario: String = "",

    @Column(length = 255)
    var apellidoUsuario: String = "",

    @Column(length = 255)
    var telefonoUsuario: String = "",

    @Column(length = 255)
    var correoUsuario: String = "",

    @Column(length = 255)
    var clave: String = "",

    @Column(name = "nivel_riesgo", length = 255)
    var nivelRiesgo: String? = null,

    @Column(length = 255)
    var generoUsuario: String? = null
)
