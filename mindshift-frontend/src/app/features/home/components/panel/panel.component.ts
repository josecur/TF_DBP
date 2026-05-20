import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // Importa el módulo de enrutamiento para usar routerLink

interface Especialista {
  nombre: string;
  especialidad: string;
  descripcion: string;
  avatar: string;
}

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './panel.component.html',
  styleUrls: ['./panel.component.css']
})
export class AppPanelComponent {
  
  // 🚀 LISTA DINÁMICA: Centralizamos los datos para limpiar el HTML
  especialistas = signal<Especialista[]>([
    { nombre: 'Dr. Carlos Mendoza', especialidad: 'Psicología Clínica', descripcion: 'Orientación en gestión de ansiedad y control del estrés académico o profesional.', avatar: '👨‍⚕️' },
    { nombre: 'Dra. Elena Valdivia', especialidad: 'Salud Preventiva', descripcion: 'Diagnóstico de fatiga cognitiva y optimización avanzada del descanso mental.', avatar: '👩‍⚕️' },
    { nombre: 'Dr. Marcus Arana', especialidad: 'Neuropsicología', descripcion: 'Evaluación de patrones atencionales y optimización del foco en alta exigencia.', avatar: '👨‍⚕️' },
    { nombre: 'Dra. Vanessa Rojas', especialidad: 'Terapia Conductual', descripcion: 'Estrategias operativas para romper ciclos de procrastinación sistemática.', avatar: '👩‍⚕️' },
    { nombre: 'Dr. Ricardo Tello', especialidad: 'Gestión Emocional', descripcion: 'Soporte preventivo ante el síndrome de desgaste ocupacional (Burnout).', avatar: '👨‍⚕️' }
  ]);

  // Señal para controlar la posición actual del carrusel
  indiceActual = signal<number>(0);

  estiloDesplazamiento = computed(() => {
    const pixeles = this.indiceActual() * -314;
    return `translateX(${pixeles}px)`;
  });

  moverIzquierda() {
    if (this.indiceActual() > 0) {
      this.indiceActual.update(idx => idx - 1);
    } else {
      // Efecto bucle: Si está al inicio y da izquierda, va al final
      this.indiceActual.set(this.especialistas().length - 1);
    }
  }

  moverDerecha() {
    if (this.indiceActual() < this.especialistas().length - 1) {
      this.indiceActual.update(idx => idx + 1);
    } else {
      // Efecto bucle: Si llegó al final y da derecha, regresa al inicio
      this.indiceActual.set(0);
    }
  }

  abrirConvocatoria() {
    alert('Accediendo al portal de postulación para especialistas de MindStep...');
  }
  // Agrega esto dentro de tu clase AppPanelComponent en panel.component.ts:
ngOnInit() {
  const guardados = localStorage.getItem('staff_especialistas');
  if (guardados) {
    const medicosNuevos = JSON.parse(guardados);
    // Fusionamos de forma reactiva la lista por defecto con los creados en el formulario
    this.especialistas.update(actuales => [...actuales, ...medicosNuevos]);
  }
}
}