import { Component, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

interface CampoFormulario {
  id: string;
  label: string;
  type: 'text' | 'password' | 'select' | 'textarea';
  placeholder?: string;
  options?: string[];
  fullWidth?: boolean; // 📐 Para controlar si toma 1 o 2 columnas en el Grid
}

@Component({
  selector: 'app-registro-especialista',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './registro-especialista.component.html',
  styleUrls: ['./registro-especialista.component.css']
})
export class RegistroEspecialistaComponent {
  private router = inject(Router);

  // 🔑 PASO 1: Cuenta (Vertical / Limpio)
  bloqueCredenciales: CampoFormulario[] = [
    { id: 'usuario', label: 'Nombre de Usuario *', type: 'text', placeholder: 'g.huisa.med' },
    { id: 'correo', label: 'Correo Institucional *', type: 'text', placeholder: 'usuario@mindstep.med' },
    { id: 'password', label: 'Contraseña de Acceso *', type: 'password', placeholder: '••••••••' }
  ];

  // 🎓 PASO 2: Información Profesional (Rejilla de 2 columnas)
  bloqueProfesional: CampoFormulario[] = [
    { id: 'nombre', label: 'Nombres y Apellidos *', type: 'text', placeholder: 'Dr. Giancarlo Huisa', fullWidth: true },
    { id: 'colegiatura', label: 'Colegiatura (C.Ps.P / CMP) *', type: 'text', placeholder: 'C.Ps.P 45871' },
    { id: 'universidad', label: 'Universidad de Egreso *', type: 'text', placeholder: 'USIL' },
    { id: 'especialidad', label: 'Especialización *', type: 'select', options: ['Psicología Clínica', 'Neuropsicología', 'Terapia Conductual', 'Salud Preventiva Ocupacional'], fullWidth: true }
  ];

  // 🌎 PASO 3: Segmentación y Destino (Moderno + Textarea amplio)
  bloqueDestino: CampoFormulario[] = [
    { id: 'pais', label: 'País *', type: 'select', options: ['Perú', 'Colombia', 'México', 'Chile', 'Argentina'] },
    { id: 'idiomas', label: 'Idiomas *', type: 'select', options: ['Español Nativo', 'Español / Inglés Avanzado', 'Español / Portugués'] },
    { id: 'publicoObjetivo', label: 'Público Objetivo (Según Carga Mental) *', type: 'select', options: ['Carga Mental Baja (Prevención)', 'Carga Mental Moderada (Intervención Corta)', 'Carga Mental Crítica (Soporte Urgente)'], fullWidth: true },
    { id: 'descripcion', label: 'Trayectoria Profesional *', type: 'textarea', placeholder: 'Resume tus certificaciones, años de experiencia o enfoque terapéutico...', fullWidth: true }
  ];

  pasoActual = signal<number>(1);
  triggerValidacion = signal<number>(0);
  
  // 🔄 Variable exclusiva para el Checkbox de TyC
  aceptaTerminos = false;

  datos: Record<string, string> = {
    usuario: '', correo: '', password: '',
    nombre: '', colegiatura: '', universidad: '',
    pais: 'Perú', idiomas: 'Español / Inglés Avanzado', especialidad: 'Psicología Clínica',
    publicoObjetivo: 'Carga Mental Moderada (Intervención Corta)', descripcion: ''
  };

  // Validaciones de los 3 pasos
  paso1Valido = computed(() => {
    this.triggerValidacion();
    return !!this.datos['usuario']?.trim() && this.datos['correo']?.includes('@') && this.datos['password']?.trim().length >= 6;
  });

  paso2Valido = computed(() => {
    this.triggerValidacion();
    return !!this.datos['nombre']?.trim() && !!this.datos['colegiatura']?.trim() && !!this.datos['universidad']?.trim();
  });

  paso3Valido = computed(() => {
    this.triggerValidacion();
    return !!this.datos['descripcion']?.trim() && this.datos['descripcion'].length > 10 && this.aceptaTerminos; // 🔐 Exige el Checkbox
  });

  onKeyup() {
    this.triggerValidacion.update(v => v + 1);
  }

  avanzarPaso() {
    if (this.pasoActual() === 1 && this.paso1Valido()) this.pasoActual.set(2);
    else if (this.pasoActual() === 2 && this.paso2Valido()) this.pasoActual.set(3);
  }

  retrocederPaso() {
    if (this.pasoActual() > 1) this.pasoActual.update(p => p - 1);
  }

  guardarEspecialista() {
    if (!this.paso3Valido()) return;

    const medicosPrevios = localStorage.getItem('staff_especialistas');
    let listaMedicos = medicosPrevios ? JSON.parse(medicosPrevios) : [];

    let tagObjetivo = 'MODERADO';
    if (this.datos['publicoObjetivo'].includes('Baja')) tagObjetivo = 'BAJO';
    if (this.datos['publicoObjetivo'].includes('Crítica')) tagObjetivo = 'CRITICO';

    const nuevoMedico = {
      nombre: this.datos['nombre'],
      especialidad: this.datos['especialidad'],
      descripcion: this.datos['descripcion'],
      universidad: this.datos['universidad'],
      pais: this.datos['pais'],
      idiomas: this.datos['idiomas'],
      publicoObjetivo: tagObjetivo,
      avatar: this.datos['nombre'].toLowerCase().includes('dra') || this.datos['nombre'].toLowerCase().includes('elena') ? '👩‍⚕️' : '👨‍⚕️',
      whatsapp: '986575756'
    };

    listaMedicos.push(nuevoMedico);
    localStorage.setItem('staff_especialistas', JSON.stringify(listaMedicos));

    alert('¡Cuenta creada y perfil profesional incorporado con éxito!');
    this.router.navigate(['/']);
  }
}