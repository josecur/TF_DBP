from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.decorators import action
from .models import Especialista, Publicacion, ConsultaEspecialista, HistorialTest, AlumnoUsuario
from .serializers import (
    EspecialistaSerializer, PublicacionSerializer, 
    ConsultaEspecialistaSerializer, HistorialTestSerializer, 
    AlumnoUsuarioSerializer
)

class EspecialistaViewSet(viewsets.ModelViewSet):
    queryset = Especialista.objects.all().prefetch_related('publicaciones', 'consultas')
    serializer_class = EspecialistaSerializer

    @action(detail=True, methods=['post'], url_path='cambiar-contra')
    def cambiar_contra(self, request, pk=None):
        especialista = self.get_object()
        nueva_contra = request.data.get('nueva_contrasena')
        
        if not nueva_contra:
            return Response({"error": "Debe proporcionar la nueva contraseña."}, status=status.HTTP_400_BAD_REQUEST)
        
        especialista.password_hash = nueva_contra
        especialista.save()
        return Response({"status": "Contraseña cambiada exitosamente en SQLite."}, status=status.HTTP_200_OK)


class PublicacionViewSet(viewsets.ModelViewSet):
    queryset = Publicacion.objects.all().order_by('-fecha_publicacion')
    serializer_class = PublicacionSerializer

    def create(self, request, *args, **kwargs):
        especialista_id = request.data.get('especialista_id')
        try:
            especialista = Especialista.objects.get(id=especialista_id)
        except Especialista.DoesNotExist:
            return Response({"error": "Especialista no encontrado."}, status=status.HTTP_404_NOT_FOUND)

        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save(especialista=especialista)
        return Response(serializer.data, status=status.HTTP_201_CREATED)


class ConsultaEspecialistaViewSet(viewsets.ModelViewSet):
    queryset = ConsultaEspecialista.objects.all().order_by('-fecha_solicitud')
    serializer_class = ConsultaEspecialistaSerializer


# 📥 HISTORIAL: Ordenado cronológicamente para heredar el último test arriba
class HistorialTestViewSet(viewsets.ModelViewSet):
    queryset = HistorialTest.objects.all().order_by('-id')
    serializer_class = HistorialTestSerializer


class AlumnoUsuarioViewSet(viewsets.ModelViewSet):
    queryset = AlumnoUsuario.objects.all().prefetch_related('consultas_solicitadas', 'historial_tests')
    serializer_class = AlumnoUsuarioSerializer

    def create(self, request, *args, **kwargs):
        data = request.data.copy()
        
        if 'correo' in data and not data.get('username'):
            data['username'] = data['correo'].split('@')[0]

        serializer = self.get_serializer(data=data)
        if not serializer.is_valid():
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)

    @action(detail=False, methods=['post'], url_path='login')
    def login_alumno(self, request):
        correo = request.data.get('correo')
        password = request.data.get('password')

        if not correo or not password:
            return Response(
                {"error": "Falta proporcionar correo y contraseña."}, 
                status=status.HTTP_400_BAD_REQUEST
            )

        try:
            alumno = AlumnoUsuario.objects.get(correo=correo, password_hash=password)
            serializer = self.get_serializer(alumno)
            return Response(serializer.data, status=status.HTTP_200_OK)
            
        except AlumnoUsuario.DoesNotExist:
            return Response(
                {"error": "Credenciales inválidas. Verifica tu correo o contraseña corporativa."}, 
                status=status.HTTP_401_UNAUTHORIZED
            )