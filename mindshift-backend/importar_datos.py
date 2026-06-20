import os
import django
import csv

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings')
django.setup()
from api.models import Escenario 

# 1. Listamos los archivos para ver qué ve Python
print("Archivos encontrados en esta carpeta:")
archivos = [f for f in os.listdir('.') if f.endswith('.csv')]
print(archivos)

if archivos:
    # 2. Cargamos el primero que encuentre, sin importar cómo se llame
    nombre_archivo = archivos[0]
    print(f"Cargando automáticamente: {nombre_archivo}")
    
    with open(nombre_archivo, newline='', encoding='utf-8-sig') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            Escenario.objects.get_or_create(
                categoria=row['rama'].strip(),
                enunciado=row['pregunta'].strip(),
                orden=row['id'].strip()
            )
            print(f"✅ Cargado: {row['pregunta']}")
else:
    print("❌ ERROR: No se encontró ningún archivo .csv en esta carpeta.")