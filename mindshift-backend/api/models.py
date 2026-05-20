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

    # 🔒 PASO 1: Credenciales de Acceso Seguras
    username = models.CharField(max_length=150, unique=True, default="medico.demo")
    password_hash = models.CharField(max_length=255, default="password_seguro")
    correo = models.EmailField(unique=True)

    # 🎓 PASO 2: Información Académica y Colegiatura
    nombres = models.CharField(max_length=100)
    apellidos = models.CharField(max_length=100)
    numero_colegiatura = models.CharField(max_length=50, unique=True)
    universidad = models.CharField(max_length=255, default="USIL")
    especialidad = models.CharField(max_length=100, choices=ENFOQUES, default='Psicología Clínica')

    # 🌎 PASO 3: Segmentación Corporativa, Match y Trayectoria
    pais = models.CharField(max_length=100, default="Perú")
    idiomas = models.CharField(max_length=100, default="Español / Inglés Avanzado")
    publico_objetivo = models.CharField(max_length=20, choices=NIVELES_OBJETIVO, default='MODERADO')
    descripcion_trayectoria = models.TextField(blank=True, null=True)
    
    # 📆 NUEVO: Fecha en la que el especialista se unió a MindStep
    fecha_registro = models.DateField(auto_now_add=True)

    # Datos operativos heredados de tu idea base
    distorsiones_tratadas = models.JSONField(default=list)  # Ej: ["CATASTROPHIZING", "MIND_READING"]
    telefono = models.CharField(max_length=20)
    enlace_agenda = models.URLField(max_length=500, blank=True, default="https://wa.me/51986575756")
    avatar_icono = models.CharField(max_length=10, default="👨‍⚕️")

    def __str__(self):
        return f"Dr. {self.nombres} {self.apellidos} - {self.numero_colegiatura} ({self.publico_objetivo})"


# 📰 NUEVO MODELO: Publicaciones generadas por el médico para su perfil
class Publicacion(models.Model):
    # Relación: Si se borra el médico, se borran sus publicaciones (on_delete=models.CASCADE)
    especialista = models.ForeignKey(Especialista, on_delete=models.CASCADE, related_name='publicaciones')
    categoria = models.CharField(max_length=100, default="Gestión Emocional")
    titulo = models.CharField(max_length=200)
    contenido = models.TextField()
    fecha_publicacion = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.titulo} - Por: {self.especialista.nombres} ({self.fecha_publicacion.strftime('%Y-%m-%d')})"


class HistorialTest(models.Model):
    usuario_nombre = models.CharField(max_length=150, default="Giancarlo Huisa")
    puntuacion_total = models.IntegerField()
    distorsion_predominante = models.CharField(max_length=100)
    nivel_carga_calculado = models.CharField(max_length=20, default="MODERADO") 
    fecha_evaluacion = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Test {self.distorsion_predominante} ({self.nivel_carga_calculado}) - {self.fecha_evaluacion.strftime('%Y-%m-%d')}"