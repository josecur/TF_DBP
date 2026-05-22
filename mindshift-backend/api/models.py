from django.db import models

class Especialista(models.Model):
    ENFOQUES = [
        ('Psicología Clínica', 'Psicología Clínica'),
        ('Neuropsicología', 'Neuropsicología Aplicada'),
        ('Terapia Conductual', 'Terapia Cognitivo-Conductual'),
        ('Salud Preventiva Ocupacional', 'Salud Preventiva Ocupacional'),
    ]
    
    NIVELES_OBJETIVO = [
        ('BAJO', 'Carga Mental Baja'),
        ('MODERADO', 'Carga Mental Moderada'),
        ('CRITICO', 'Carga Mental Crítica'),
    ]

    username = models.CharField(max_length=150, unique=True, default="medico.demo")
    password_hash = models.CharField(max_length=255, default="password_seguro")
    correo = models.EmailField(unique=True)
    nombres = models.CharField(max_length=100)
    apellidos = models.CharField(max_length=100)
    numero_colegiatura = models.CharField(max_length=50, unique=True)
    universidad = models.CharField(max_length=255, default="USIL")
    especialidad = models.CharField(max_length=100, choices=ENFOQUES, default='Psicología Clínica')
    pais = models.CharField(max_length=100, default="Perú")
    idiomas = models.CharField(max_length=100, default="Español / Inglés Avanzado")
    publico_objetivo = models.CharField(max_length=20, choices=NIVELES_OBJETIVO, default='MODERADO')
    descripcion_trayectoria = models.TextField(blank=True, null=True)
    fecha_registro = models.DateField(auto_now_add=True)
    distorsiones_tratadas = models.JSONField(default=list)
    telefono = models.CharField(max_length=20)
    enlace_agenda = models.URLField(max_length=500, blank=True, default="https://wa.me/51986575756")
    
    # 🎯 CORREGIDO: Cambiado de CharField a TextField para almacenar strings Base64 completos de fotos
    avatar_icono = models.TextField(blank=True, null=True, default="https://cdn-icons-png.flaticon.com/512/3135/3135715.png")

    def __str__(self):
        return f"Dr. {self.nombres} {self.apellidos}"


class Publicacion(models.Model):
    especialista = models.ForeignKey(Especialista, on_delete=models.CASCADE, related_name='publicaciones')
    categoria = models.CharField(max_length=100, default="Gestión Emocional")
    titulo = models.CharField(max_length=200)
    contenido = models.TextField()
    fecha_publicacion = models.DateTimeField(auto_now_add=True)


# 📥 NUEVA TABLA: Usuarios/Alumnos que consumen la plataforma, rinden tests y agendan citas
class AlumnoUsuario(models.Model):
    CARRERAS = [
        ('Escolar', 'Educación Secundaria / Escolar'),
        ('Ingeniería de Sistemas', 'Ingeniería de Sistemas e Informática'),
        ('Psicología', 'Psicología'),
        ('Administración', 'Administración de Empresas'),
        ('Derecho', 'Derecho'),
    ]

    username = models.CharField(max_length=150, unique=True)
    password_hash = models.CharField(max_length=255)  # Clave encriptada
    correo = models.EmailField(unique=True)
    nombres = models.CharField(max_length=100)
    apellidos = models.CharField(max_length=100)
    codigo_identificacion = models.CharField(max_length=50, unique=True)  # Código USIL, DNI o Escolar
    carrera = models.CharField(max_length=100, choices=CARRERAS, default='Escolar')
    telefono = models.CharField(max_length=20, blank=True, null=True)
    fecha_registro = models.DateTimeField(auto_now_add=True)
    
    # Récord del último screening del usuario
    ultimo_nivel_carga = models.CharField(max_length=20, default="SIN_TEST")  # SIN_TEST, BAJO, MODERADO, CRITICO

    def __str__(self):
        return f"Usuario: {self.nombres} {self.apellidos} ({self.codigo_identificacion})"


# 📥 REGISTRO DE CONSULTAS: Alumnos vinculados al médico (ACTUALIZADO CON RELACIÓN)
class ConsultaEspecialista(models.Model):
    ESTADOS = [
        ('PENDIENTE', 'Pendiente'),
        ('ATENDIDO', 'Atendido'),
    ]
    especialista = models.ForeignKey(Especialista, on_delete=models.CASCADE, related_name='consultas')
    
    # 🎯 NUEVA RELACIÓN: Enlazado formalmente al usuario de la plataforma
    alumno_usuario = models.ForeignKey(AlumnoUsuario, on_delete=models.CASCADE, related_name='consultas_solicitadas', null=True, blank=True)
    
    alumno_nombre = models.CharField(max_length=150)  # Respaldo por si se ingresa texto plano
    alumno_correo = models.EmailField()
    motivo = models.TextField(default="Derivación por estrés académico.")
    estado = models.CharField(max_length=20, choices=ESTADOS, default='PENDIENTE')
    fecha_solicitud = models.DateTimeField(auto_now_add=True)


# 📥 HISTORIAL DE EVALUACIONES: Vinculado formalmente al Alumno
class HistorialTest(models.Model):
    # 🎯 NUEVA RELACIÓN: Sabremos exactamente qué usuario rindió qué test
    alumno_usuario = models.ForeignKey(AlumnoUsuario, on_delete=models.CASCADE, related_name='historial_tests', null=True, blank=True)
    
    usuario_nombre = models.CharField(max_length=150, default="Giancarlo Huisa")
    puntuacion_total = models.IntegerField()
    distorsion_predominante = models.CharField(max_length=100)
    nivel_carga_calculado = models.CharField(max_length=20, default="MODERADO") 
    fecha_evaluacion = models.DateTimeField(auto_now_add=True)