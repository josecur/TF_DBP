from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.decorators import action
from .models import Usuario, Escenario, Opciones, Profesional, Reserva
from .serializers import (
    UsuarioSerializer, EscenarioSerializer, OpcionesSerializer,
    ProfesionalSerializer, ReservaSerializer
)


class UsuarioViewSet(viewsets.ModelViewSet):
    queryset         = Usuario.objects.all()
    serializer_class = UsuarioSerializer

    @action(detail=False, methods=['post'], url_path='registro')
    def registro(self, request):
        puntaje = request.data.get('puntaje', 0)
        try:
            puntaje = int(puntaje)
        except (ValueError, TypeError):
            puntaje = 0

        if puntaje >= 18:
            nivel = "CRITICO"
        elif puntaje >= 10:
            nivel = "MODERADO"
        else:
            nivel = "BAJO"

        data_mapeada = {
            'nombreUsuario':   request.data.get('nombre'),
            'apellidoUsuario': request.data.get('apellido'),
            'correoUsuario':   request.data.get('email'),
            'telefonoUsuario': request.data.get('telefono'),
            'clave':           request.data.get('password'),
            'nivel_riesgo':    nivel,
            'generoUsuario':   request.data.get('genero', '')  # ✅ opcional
        }

        serializer = UsuarioSerializer(data=data_mapeada)
        if serializer.is_valid():
            usuario = serializer.save()
            return Response({
                "status":       "Éxito",
                "id":           usuario.id,
                "nombres":      usuario.nombreUsuario,
                "apellido":     usuario.apellidoUsuario,
                "correo":       usuario.correoUsuario,
                "nivel_riesgo": usuario.nivel_riesgo,
                "puntaje":      puntaje
            }, status=status.HTTP_201_CREATED)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    @action(detail=False, methods=['post'], url_path='login')
    def login(self, request):
        correo   = request.data.get('username')
        password = request.data.get('password')
        try:
            usuario = Usuario.objects.get(correoUsuario=correo, clave=password)
            return Response({
                "status":       "Login exitoso",
                "id":           usuario.id,
                "nombres":      usuario.nombreUsuario,
                "apellido":     usuario.apellidoUsuario,
                "correo":       usuario.correoUsuario,
                "nivel_riesgo": usuario.nivel_riesgo
            }, status=status.HTTP_200_OK)
        except Usuario.DoesNotExist:
            return Response(
                {"error": "Credenciales inválidas"},
                status=status.HTTP_401_UNAUTHORIZED
            )


class EscenarioViewSet(viewsets.ModelViewSet):
    queryset         = Escenario.objects.all().order_by('orden')
    serializer_class = EscenarioSerializer


class OpcionesViewSet(viewsets.ModelViewSet):
    queryset         = Opciones.objects.all()
    serializer_class = OpcionesSerializer


class ProfesionalViewSet(viewsets.ModelViewSet):
    queryset         = Profesional.objects.all()
    serializer_class = ProfesionalSerializer

    @action(detail=False, methods=['get'], url_path='buscar')
    def buscar(self, request):
        especialidad = request.query_params.get('especialidad')
        if especialidad:
            profesionales = Profesional.objects.filter(
                especialidad__icontains=especialidad
            )
        else:
            profesionales = Profesional.objects.all()
        serializer = ProfesionalSerializer(profesionales, many=True)
        return Response(serializer.data)

    @action(detail=False, methods=['post'], url_path='login')
    def login(self, request):
        correo   = request.data.get('username')
        password = request.data.get('password')
        try:
            p = Profesional.objects.get(correoProfesional=correo, clave=password)
            return Response({
                "status":                 "Login exitoso",
                "id":                     p.id,
                "nombreProfesional":      p.nombreProfesional,
                "apellidoProfesional":    p.apellidoProfesional,
                "especialidad":           p.especialidad,
                "validacion":             p.validacion,
                "avatar_icono":           p.avatar_icono            or '',
                "descripcion_trayectoria": p.descripcion_trayectoria or '',
                "enlace_agenda":          p.enlace_agenda            or '',
                "generoProfesional":      p.generoProfesional        or ''
            }, status=status.HTTP_200_OK)
        except Profesional.DoesNotExist:
            return Response(
                {"error": "Credenciales inválidas"},
                status=status.HTTP_401_UNAUTHORIZED
            )
# En views.py
class ReservaViewSet(viewsets.ModelViewSet):
    serializer_class = ReservaSerializer
    
    def get_queryset(self):
        # Usamos distinct() para asegurar que no haya filas duplicadas
        queryset = Reserva.objects.all().order_by('-fecha').distinct()
        
        user_id = self.request.query_params.get('idUsuario')
        if user_id:
            queryset = queryset.filter(idUsuario=user_id)
            
        return queryset

    def perform_create(self, serializer):
        # El serializer ya debería capturar el 'motivo' enviado desde el JSON del frontend
        # Si quisieras hacer validaciones extra antes de guardar, las harías aquí:
        serializer.save()

    @action(detail=False, methods=['get'], url_path='por-usuario')
    def por_usuario(self, request):
        """Opcional: Filtrar reservas por ID de usuario"""
        user_id = request.query_params.get('idUsuario')
        reservas = Reserva.objects.filter(idUsuario=user_id).order_by('-fecha')
        serializer = self.get_serializer(reservas, many=True)
        return Response(serializer.data)

    @action(detail=False, methods=['get'], url_path='por-profesional')
    def por_profesional(self, request):
        """Opcional: Filtrar reservas por ID de profesional"""
        prof_id = request.query_params.get('idProfesional')
        reservas = Reserva.objects.filter(idProfesional=prof_id).order_by('-fecha')
        serializer = self.get_serializer(reservas, many=True)
        return Response(serializer.data)