from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    # 🚀 Asegúrate de que esta línea exista y esté bien escrita:
    path('api/', include('api.urls')),
]