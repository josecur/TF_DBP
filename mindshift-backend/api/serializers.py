from rest_framework import serializers
from .models import Especialista, HistorialTest, Publicacion # 🚀 Añadido Publicacion

# 📰 NUEVO SERIALIZER: Transforma los artículos del médico a JSON
class PublicacionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Publicacion
        fields = ['id', 'categoria', 'titulo', 'contenido', 'fecha_publicacion']


class EspecialistaSerializer(serializers.ModelSerializer):
    # 🔗 Relación anidada: Jala automáticamente las publicaciones ligadas a este especialista
    publicaciones = PublicacionSerializer(many=True, read_only=True)

    class Meta:
        model = Especialista
        fields = [
            'id', 'username', 'password_hash', 'correo', 
            'nombres', 'apellidos', 'numero_colegiatura', 
            'universidad', 'especialidad', 'pais', 'idiomas', 
            'publico_objetivo', 'descripcion_trayectoria', 
            'distorsiones_tratadas', 'telefono', 'enlace_agenda', 'avatar_icono',
            'fecha_registro', 'publicaciones' # 👈 📆 ¡Campos clave para el perfil profesional!
        ]
        extra_kwargs = {'password_hash': {'write_only': True}} # Seguridad: Oculta el hash en los GET


class HistorialTestSerializer(serializers.ModelSerializer):
    class Meta:
        model = HistorialTest
        fields = '__all__'