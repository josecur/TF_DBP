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
  genero = signal<string>('Femenino'); // 🧬 Nueva señal para el género

  // 📸 Señales para procesar la subida del archivo físico
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

guardarEspecialista() {
  const partesNombre = this.nombreCompleto().trim().split(' ');
  const primerNombre     = partesNombre[0] || 'Especialista';
  const apellidosRestantes = partesNombre.slice(1).join(' ') || 'Profesional';

  const nuevoMedico = {
    nombreProfesional:       primerNombre,
    apellidoProfesional:     apellidosRestantes,
    telefonoProfesional:     '986575756',
    correoProfesional:       this.correo(),
    clave:                   this.password(),
    especialidad:            this.especialidad(),
    universidad:             this.universidad(),
    avatar_icono:            this.imagenBase64() || '',
    descripcion_trayectoria: this.descripcion().trim(),
    enlace_agenda:           'https://wa.me/51986575756',
    generoProfesional:       this.genero(),
    validacion:              1
  };

  this.especialistaService.registrarEspecialista(nuevoMedico).subscribe({
    next: (respuesta: any) => {
      // ✅ session_active con TODOS los campos que necesita el panel
      const sesionCompleta = {
        rol:                     'especialista',
        id:                      respuesta.id,
        nombres:                 respuesta.nombreProfesional,
        nombreProfesional:       respuesta.nombreProfesional,
        apellidoProfesional:     respuesta.apellidoProfesional,
        especialidad:            respuesta.especialidad,
        avatar_icono:            respuesta.avatar_icono            || '',
        descripcion_trayectoria: respuesta.descripcion_trayectoria || '',
        enlace_agenda:           respuesta.enlace_agenda            || '',
        generoProfesional:       respuesta.generoProfesional        || 'Masculino',
        validacion:              respuesta.validacion
      };
      const sesionParaStorage = { ...sesionCompleta };
      delete sesionParaStorage.avatar_icono;

      localStorage.setItem('session_active',      JSON.stringify(sesionCompleta));
      localStorage.setItem('usuario_especialista', JSON.stringify(sesionCompleta));
      localStorage.setItem('session_active', JSON.stringify(sesionParaStorage));

      alert('¡Registro corporativo enviado con éxito!');
      this.router.navigate(['/dashboard-especialista'])
          .then(() => window.location.reload());
          
    alert('¡Registro corporativo enviado con éxito!');
      this.router.navigate(['/dashboard-especialista']);
    },
    error: (err: any) => {
      console.error('Error:', err);
      alert('Error al registrar. Verifica la conexión con el backend.');
    }
  });
}
}