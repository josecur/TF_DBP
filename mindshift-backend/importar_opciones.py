import os
import django
import csv

# 1. Configurar el entorno
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings')
django.setup()

from api.models import Escenario, Opciones

# 2. Ruta del archivo de opciones
csv_file_path = 'Hoja 2 - Tabla Opcion - Hoja 2 - Tabla Opcion.csv'

with open(csv_file_path, newline='', encoding='utf-8-sig') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        try:
            # Buscamos el escenario al que pertenece esta opción
            escenario = Escenario.objects.get(id=row['escenario_id'])
            
            # Creamos la opción vinculada
            opcion, creado = Opciones.objects.get_or_create(
                idEscenario=escenario,
                contenido=row['texto'].strip(),
                valor_puntos=int(row['peso'])
            )
            
            if creado:
                print(f"✅ Opción cargada para escenario {row['escenario_id']}: {row['texto'][:30]}...")
            else:
                print(f"⚠️ Ya existe la opción: {row['texto'][:30]}...")
                
        except Escenario.DoesNotExist:
            print(f"❌ Error: No se encontró escenario con ID {row['escenario_id']}")
        except ValueError:
            print(f"❌ Error: El valor de puntos '{row['peso']}' no es un número válido.")