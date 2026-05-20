import os
from pathlib import Path

# 1. RUTAS BASE DEL PROYECTO
BASE_DIR = Path(__file__).resolve().parent.parent

# 2. CONFIGURACIÓN DE SEGURIDAD (DESARROLLO)
SECRET_KEY = 'django-insecure-mindshift-key-generica-para-desarrollo'
DEBUG = True
ALLOWED_HOSTS = ['localhost', '127.0.0.1']

# 3. APLICACIONES DEL SISTEMA
INSTALLED_APPS = [
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    
    # Módulos para la API y CORS
    'rest_framework',
    'corsheaders',
    'api',
]

# 4. MIDDLEWARES (PROCESADORES DE PETICIONES)
MIDDLEWARE = [
    'corsheaders.middleware.CorsMiddleware',  # Crucial al inicio para interceptar CORS
    'django.middleware.common.CommonMiddleware',
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]

# 5. CONFIGURACIÓN DE ENRUTAMIENTO GENERAL
ROOT_URLCONF = 'config.urls'

# 6. MOTOR DE PLANTILLAS (RESTAURADO)
TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]

WSGI_APPLICATION = 'config.wsgi.application'

# 7. BASE DE DATOS LOCAL (SQLITE3)
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': BASE_DIR / 'db.sqlite3',
    }
}

# 8. VALIDACIÓN DE CONTRASEÑAS
AUTH_PASSWORD_VALIDATORS = [
    {'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator'},
    {'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator'},
    {'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator'},
    {'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator'},
]

# 9. INTERNACIONALIZACIÓN (Configurado para Perú / Español)
LANGUAGE_CODE = 'es-pe'
TIME_ZONE = 'America/Lima'
USE_I18N = True
USE_TZ = True

# 10. ARCHIVOS ESTÁTICOS
STATIC_URL = 'static/'
DEFAULT_AUTO_FIELD = 'django.db.models.BigAutoField'

# 11. ORÍGENES PERMITIDOS PARA ANGULAR
CORS_ALLOWED_ORIGINS = [
    "http://localhost:4200",
]