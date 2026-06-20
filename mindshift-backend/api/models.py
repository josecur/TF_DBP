from django.db import models

class Escenario(models.Model):
    categoria = models.TextField()
    enunciado = models.TextField()
    orden = models.IntegerField(default=1) 

    def __str__(self):
        return f"{self.orden}. {self.categoria}"


class Opciones(models.Model):
    # Representa las posibles respuestas
    idEscenario = models.ForeignKey(Escenario, on_delete=models.CASCADE, related_name='opciones')
    contenido = models.TextField()
    valor_puntos = models.IntegerField(default=0)  

    def __str__(self):
        return f"{self.contenido} ({self.valor_puntos} pts)"


class Usuario(models.Model):
    # Representa al paciente/alumno
    nombreUsuario = models.TextField()
    apellidoUsuario = models.TextField()
    telefonoUsuario = models.TextField()
    correoUsuario = models.TextField()
    clave = models.TextField()
    nivel_riesgo = models.TextField(null=True, blank=True)  # Almacena el resultado final
    
    # 🧬 ADICIÓN: Género del estudiante (Ej: Masculino, Femenino, Otro)
    generoUsuario = models.TextField(null=True, blank=True)

    def __str__(self):
        return f"{self.nombreUsuario} {self.apellidoUsuario}"


class Profesional(models.Model):
    # Representa al especialista
    nombreProfesional = models.TextField()
    apellidoProfesional = models.TextField()
    telefonoProfesional = models.TextField()
    correoProfesional = models.TextField()
    clave = models.TextField()
    especialidad = models.TextField()
    validacion = models.IntegerField(default=0)  # 0: Pendiente, 1: Validado
    universidad = models.TextField()
    
    # Extensiones para el panel de Angular
    avatar_icono = models.TextField(null=True, blank=True)            # Foto en Base64
    descripcion_trayectoria = models.TextField(null=True, blank=True) # Enfoque terapéutico
    enlace_agenda = models.TextField(null=True, blank=True)           # Link de WhatsApp o agenda virtual
    
    # 🧬 ADICIÓN: Género del profesional (Útil para renderizar Dr. o Dra. dinámicamente)
    generoProfesional = models.TextField(null=True, blank=True) 

    def __str__(self):
        return f"Dr(a). {self.nombreProfesional} {self.apellidoProfesional}"


class Reserva(models.Model):
    # ... tus campos actuales ...
    idUsuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    idProfesional = models.ForeignKey(Profesional, on_delete=models.CASCADE)
    estado = models.TextField(default='Pendiente')
    fecha = models.DateTimeField()
    motivo = models.TextField(default='Sin motivo especificado')

    # AGREGA ESTOS DOS:
    contacto_correo = models.TextField(null=True, blank=True)
    contacto_whatsapp = models.TextField(null=True, blank=True)

    def __str__(self):
        return f"Cita {self.idUsuario} con {self.idProfesional}"