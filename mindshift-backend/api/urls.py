from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import EspecialistaViewSet, HistorialTestViewSet

# Configuración del router automático de Django REST Framework
router = DefaultRouter()
router.register(r'especialistas', EspecialistaViewSet)
router.register(r'tests', HistorialTestViewSet)

urlpatterns = [
    path('', include(router.urls)),
]