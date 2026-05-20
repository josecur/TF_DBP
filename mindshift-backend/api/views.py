from rest_framework import viewsets
from .models import Especialista, HistorialTest
from .serializers import EspecialistaSerializer, HistorialTestSerializer

class EspecialistaViewSet(viewsets.ModelViewSet):
    """
    Endpoints automáticos:
    - GET /api/especialistas/  -> Devuelve todos los médicos para el carrusel
    - POST /api/especialistas/ -> Registra el formulario de 3 pasos de Angular
    """
    queryset = Especialista.objects.all()
    serializer_class = EspecialistaSerializer

class HistorialTestViewSet(viewsets.ModelViewSet):
    queryset = HistorialTest.objects.all()
    serializer_class = HistorialTestSerializer