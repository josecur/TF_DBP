from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    EspecialistaViewSet, PublicacionViewSet, 
    ConsultaEspecialistaViewSet, HistorialTestViewSet,
    AlumnoUsuarioViewSet  # 🎯 Importamos el nuevo ViewSet de usuarios
)

# 1. Creamos el enrutador local de la API
router = DefaultRouter()
router.register(r'especialistas', EspecialistaViewSet, basename='especialista')
router.register(r'publicaciones', PublicacionViewSet, basename='publicacion')
router.register(r'consultas', ConsultaEspecialistaViewSet, basename='consulta')
router.register(r'historial-tests', HistorialTestViewSet, basename='historial-test')

# 🎯 REGISTRAMOS LA NUEVA TABLA: Genera las rutas automáticas para los Alumnos / Usuarios
router.register(r'alumnos', AlumnoUsuarioViewSet, basename='alumno')

# 2. Las URLs exponen directamente lo que maneja el router
urlpatterns = [
    path('', include(router.urls)),
]