from rest_framework import serializers
from .models import Especialista, Publicacion, ConsultaEspecialista, HistorialTest, AlumnoUsuario

# 📰 Serializer para las publicaciones (Se mantiene igual)
class PublicacionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Publicacion
        fields = ['id', 'categoria', 'titulo', 'contenido', 'fecha_publicacion']


# 📥 Serializer para las consultas de los alumnos (ACTUALIZADO)
class ConsultaEspecialistaSerializer(serializers.ModelSerializer):
    class Meta:
        model = ConsultaEspecialista
        fields = [
            'id', 'especialista', 'alumno_usuario', 'alumno_nombre', 
            'alumno_correo', 'motivo', 'estado', 'fecha_solicitud'
        ]


# 🩺 Serializer principal del especialista (Se mantiene igual)
class EspecialistaSerializer(serializers.ModelSerializer):
    publicaciones = PublicacionSerializer(many=True, read_only=True)
    consultas = ConsultaEspecialistaSerializer(many=True, read_only=True)

    class Meta:
        model = Especialista
        fields = [
            'id', 'username', 'password_hash', 'correo', 'nombres', 'apellidos', 
            'numero_colegiatura', 'universidad', 'especialidad', 'pais', 'idiomas', 
            'publico_objetivo', 'descripcion_trayectoria', 'fecha_registro', 
            'distorsiones_tratadas', 'telefono', 'enlace_agenda', 'avatar_icono',
            'publicaciones', 'consultas'
        ]
        extra_kwargs = {'password_hash': {'write_only': True}}


# 📊 Serializer para el historial de tests cognitivos (Mapea explícitamente todo)
class HistorialTestSerializer(serializers.ModelSerializer):
    class Meta:
        model = HistorialTest
        fields = [
            'id', 'alumno_usuario', 'usuario_nombre', 'puntuacion_total', 
            'distorsion_predominante', 'nivel_carga_calculado', 'fecha_evaluacion'
        ]


# 📥 NUEVO SERIALIZER: AlumnoUsuario (Para registrar, loguear y gestionar la data del alumno)
# 📥 SERIALIZER: AlumnoUsuario (CORREGIDO)
class AlumnoUsuarioSerializer(serializers.ModelSerializer):
    consultas_solicitadas = ConsultaEspecialistaSerializer(many=True, read_only=True)
    historial_tests = HistorialTestSerializer(many=True, read_only=True)

    class Meta:
        model = AlumnoUsuario
        fields = [
            'id', 'username', 'password_hash', 'correo', 'nombres', 'apellidos', 
            'codigo_identificacion',  # 🎯 ¡FIJADO AQUÍ! Cambiado de 'identification' a 'identificacion'
            'carrera', 'telefono', 'fecha_registro', 
            'ultimo_nivel_carga', 'consultas_solicitadas', 'historial_tests'
        ]
        extra_kwargs = {'password_hash': {'write_only': True}}