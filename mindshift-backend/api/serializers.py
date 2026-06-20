from rest_framework import serializers
from .models import Escenario, Opciones, Usuario, Profesional, Reserva

class OpcionesSerializer(serializers.ModelSerializer):
    class Meta:
        model = Opciones
        fields = ['id', 'idEscenario', 'contenido', 'valor_puntos']

class EscenarioSerializer(serializers.ModelSerializer):
    opciones = OpcionesSerializer(many=True, read_only=True)
    class Meta:
        model = Escenario
        fields = ['id', 'categoria', 'enunciado', 'orden', 'opciones']

class UsuarioSerializer(serializers.ModelSerializer):
    class Meta:
        model = Usuario
        fields = [
            'id', 'nombreUsuario', 'apellidoUsuario', 'telefonoUsuario', 
            'correoUsuario', 'clave', 'nivel_riesgo', 'generoUsuario'
        ]
        extra_kwargs = {'clave': {'write_only': True}}

class ProfesionalSerializer(serializers.ModelSerializer):
    class Meta:
        model = Profesional
        fields = [
            'id', 'nombreProfesional', 'apellidoProfesional', 'telefonoProfesional', 
            'correoProfesional', 'clave', 'especialidad', 'validacion', 'universidad',
            'avatar_icono', 'descripcion_trayectoria', 'enlace_agenda', 'generoProfesional'
        ]
        extra_kwargs = {
            'clave': {'write_only': True, 'required': False} 
        }

    def update(self, instance, validated_data):
        if 'clave' in validated_data:
            instance.clave = validated_data.pop('clave')
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        instance.save()
        return instance
class ReservaSerializer(serializers.ModelSerializer):
    # ... tus campos (se quedan igual) ...
    alumno_nombre = serializers.SerializerMethodField()
    alumno_correo = serializers.SerializerMethodField()
    nivel_riesgo = serializers.SerializerMethodField()
    medico_nombre = serializers.SerializerMethodField()
    medico_contacto = serializers.SerializerMethodField()
    
    contacto_correo = serializers.EmailField(write_only=True, required=False)
    contacto_whatsapp = serializers.URLField(write_only=True, required=False)

    class Meta:
        model = Reserva
        fields = [
            'id', 'idUsuario', 'idProfesional', 'estado', 'fecha', 'motivo', 
            'contacto_correo', 'contacto_whatsapp', 
            'alumno_nombre', 'alumno_correo', 'nivel_riesgo', 
            'medico_nombre', 'medico_contacto'
        ]
        # AGREGA ESTO PARA HACER LOS CAMPOS OPCIONALES EN ACTUALIZACIONES
        extra_kwargs = {
            'idUsuario': {'required': False},
            'idProfesional': {'required': False},
            'fecha': {'required': False},
            'motivo': {'required': False},
            'estado': {'required': False}
        }

    def get_alumno_nombre(self, obj):
        return f"{obj.idUsuario.nombreUsuario} {obj.idUsuario.apellidoUsuario}" if obj.idUsuario else "Usuario no encontrado"

    def get_medico_nombre(self, obj):
        return f"Dr(a). {obj.idProfesional.nombreProfesional} {obj.idProfesional.apellidoProfesional}" if obj.idProfesional else "Profesional no asignado"

    def get_alumno_correo(self, obj):
        return obj.idUsuario.correoUsuario if obj.estado == 'Aceptado' and obj.idUsuario else "Oculto"

    def get_nivel_riesgo(self, obj):
        return obj.idUsuario.nivel_riesgo if obj.estado == 'Aceptado' and obj.idUsuario else "Bloqueado"

    def get_medico_contacto(self, obj):
        if obj.estado == 'Aceptado' and obj.idProfesional:
            return {
                "telefono": obj.idProfesional.telefonoProfesional,
                "correo": obj.contacto_correo or obj.idProfesional.correoProfesional,
                "enlace": obj.contacto_whatsapp or obj.idProfesional.enlace_agenda
            }
        return {"mensaje": "Privado"}

    def create(self, validated_data):
        # Manejo de creación inicial
        return Reserva.objects.create(**validated_data)

    def update(self, instance, validated_data):
        instance.estado = validated_data.get('estado', instance.estado)
        instance.motivo = validated_data.get('motivo', instance.motivo)
        
        # Guardar los campos de contacto si vienen en el PUT
        if 'contacto_correo' in validated_data:
            instance.contacto_correo = validated_data['contacto_correo']
        if 'contacto_whatsapp' in validated_data:
            instance.contacto_whatsapp = validated_data['contacto_whatsapp']
            
        instance.save()
        return instance