from django.db import models

class Especialista(models.Model):
    ENFOQUES = [
        ('TCC', 'Terapia Cognitivo-Conductual'),
        ('Ansiedad', 'Manejo de Ansiedad y Depresión'),
        ('Neuropsicología', 'Neuropsicología Aplicada'),
        ('Manejo de Estrés', 'Manejo de Estrés Clínico'),
    ]

    nombres = models.CharField(max_length=100)
    apellidos = models.CharField(max_length=100)
    numero_colegiatura = models.CharField(max_length=50, unique=True)
    especialidad = models.CharField(max_length=50, choices=ENFOQUES, default='TCC')
    distorsiones_tratadas = models.JSONField(default=list)  # Ej: ["CATASTROPHIZING", "MIND_READING"]
    telefono = models.CharField(max_length=20)
    correo = models.EmailField(unique=True)
    enlace_agenda = models.URLField(max_length=500)
    foto_url = models.URLField(max_length=500, blank=True, default="https://images.unsplash.com/photo-1622253692010-333f2da6031d?q=80&w=200")

    def __str__(self):
        return f"Dr. {self.nombres} {self.apellidos} - {self.numero_colegiatura}"

class HistorialTest(models.Model):
    # En un sistema real usarías el modelo User de Django, aquí lo simplificamos con el nombre del alumno
    usuario_nombre = models.CharField(max_length=150, default="Giancarlo Huisa")
    puntuacion_total = models.IntegerField()
    distorsion_predominante = models.CharField(max_length=100)
    fecha_evaluacion = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Test {self.distorsion_predominante} - {self.fecha_evaluacion.strftime('%Y-%m-%d')}"