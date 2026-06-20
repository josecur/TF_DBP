from django.urls import path, include
from rest_framework.routers import DefaultRouter
# IMPORTANTE: Estos son los ÚNICOS ViewSets que existen ahora
from .views import (
    EscenarioViewSet, 
    OpcionesViewSet, 
    UsuarioViewSet, 
    ProfesionalViewSet, 
    ReservaViewSet
)

router = DefaultRouter()
router.register(r'escenarios', EscenarioViewSet, basename='escenario')
router.register(r'opciones', OpcionesViewSet, basename='opcion')
router.register(r'usuarios', UsuarioViewSet, basename='usuario')
router.register(r'profesionales', ProfesionalViewSet, basename='profesional')
router.register(r'reservas', ReservaViewSet, basename='reserva')

urlpatterns = [
    path('', include(router.urls)),
]