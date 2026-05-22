import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { EspecialistaService } from './especialista.service';

@Component({
  selector: 'app-registro-especialista',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  providers: [EspecialistaService],
  templateUrl: './registro-especialista.component.html',
  styleUrls: ['./registro-especialista.component.css']
})
export class RegistroEspecialistaComponent {
  private router = inject(Router);
  private especialistaService = inject(EspecialistaService);

  // 🔄 Control del flujo del formulario
  pasoActual = signal<number>(1);
  terminosAceptados = signal<boolean>(false);

  // 📝 Señales Atómicas e Independientes
  username = signal<string>('');
  password = signal<string>('');
  correo = signal<string>('');
  
  nombreCompleto = signal<string>('');
  colegiatura = signal<string>('');
  universidad = signal<string>('');
  especialidad = signal<string>('Psicología Clínica');
  
  pais = signal<string>('Perú');
  idiomas = signal<string>('Español');
  publicoObjetivo = signal<string>('MODERADO');
  descripcion = signal<string>('');

  // 📸 Nuevas señales para procesar la subida del archivo físico
  imagenBase64 = signal<string>(''); 
  imagenPrevia = signal<string>('');

  // 🎛️ Validadores Reactivos Letra por Letra
  paso1Valido = computed(() => {
    return this.username().trim().length >= 4 &&
           this.password().trim().length >= 4 &&
           this.correo().includes('@');
  });

  paso2Valido = computed(() => {
    return this.nombreCompleto().trim().length > 5 &&
           this.colegiatura().trim().length >= 4 &&
           this.universidad().trim().length > 3;
  });

  paso3Valido = computed(() => {
    return this.descripcion().trim().length >= 10 && this.terminosAceptados();
  });

  // 🧭 Métodos de Navegación del Formulario
  siguientePaso() {
    if (this.pasoActual() === 1 && this.paso1Valido()) {
      this.pasoActual.set(2);
    } else if (this.pasoActual() === 2 && this.paso2Valido()) {
      this.pasoActual.set(3);
    }
  }

  anteriorPaso() {
    if (this.pasoActual() > 1) {
      this.pasoActual.set(this.pasoActual() - 1);
    }
  }

  // 📥 MÉTODOS DE CONVERSIÓN: Captura la imagen local del PC y la convierte a String
  alSeleccionarArchivo(event: any) {
    const archivo: File = event.target.files[0];
    
    if (archivo) {
      if (!archivo.type.startsWith('image/')) {
        alert('Por favor, selecciona un archivo de tipo imagen válido.');
        return;
      }

      const lector = new FileReader();
      
      lector.onload = () => {
        const cadenaResultado = lector.result as string;
        this.imagenPrevia.set(cadenaResultado); // Genera la previsualización en el HTML
        this.imagenBase64.set(cadenaResultado); // Genera la cadena Base64 pura
      };

      lector.readAsDataURL(archivo);
    }
  }

  // 🚀 Guardado final en la base de datos SQLite a través de Django REST Framework
  guardarEspecialista() {
    // Romper cadena si faltan nombres o apellidos
    const partesNombre = this.nombreCompleto().trim().split(' ');
    const primerNombre = partesNombre[0] || 'Especialista';
    const apellidosRestantes = partesNombre.slice(1).join(' ') || 'Profesional';

    // Formatear el JSON idéntico a las columnas del modelo de Django
    const nuevoMedico = {
      username: this.username(),
      password_hash: this.password(), 
      correo: this.correo(),
      nombres: primerNombre,
      apellidos: apellidosRestantes,
      numero_colegiatura: this.colegiatura(),
      universidad: this.universidad(),
      especialidad: this.especialidad(),
      pais: this.pais(),
      idiomas: this.idiomas(),
      publico_objetivo: this.publicoObjetivo(),
      descripcion_trayectoria: this.descripcion(),
      distorsiones_tratadas: ["CATASTROPHIZING", "MIND_READING"], 
      telefono: '986575756',
      enlace_agenda: 'https://wa.me/51986575756',
      // 🎯 LA CLAVE: Si subió foto mandamos el Base64, sino se inyecta una silueta neutra por defecto
      avatar_icono: this.imagenBase64() || 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png'
    };

    // Petición HTTP POST real hacia tu backend
    this.especialistaService.registrarEspecialista(nuevoMedico).subscribe({
      next: (respuesta: any) => {
        alert('¡Registro corporativo guardado exitosamente en SQLite!');
        
        // Guardamos en LocalStorage la respuesta que manda Django
        localStorage.setItem('usuario_especialista', JSON.stringify(respuesta));
        
        // Redirección automática inmediata hacia el panel del especialista
        this.router.navigate(['/dashboard-especialista']); 
      },
      error: (err: any) => {
        console.error('Error al conectar con Django:', err);
        alert('Error al registrar. Verifica que el usuario o colegiatura no estén duplicados.');
      }
    });
  }
}