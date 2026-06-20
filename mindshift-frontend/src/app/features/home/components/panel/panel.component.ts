import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EspecialistaService } from '../../../especialista/registro-especialista/especialista.service';

// 🎯 Sincronizado con el modelo exacto de Django
export interface Especialista {
  id?: number; 
  nombreProfesional: string;    // 👈 Cambiado para coincidir con el ORM
  apellidoProfesional: string;  // 👈 Cambiado para coincidir con el ORM
  especialidad: string;
  descripcion_trayectoria: string;
  universidad: string;
  validacion: number;
  avatar_icono: string;
  pais?: string;
  idiomas?: string;
  publico_objetivo?: string;
}

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './panel.component.html',
  styleUrls: ['./panel.component.css']
})
export class AppPanelComponent implements OnInit {
  private especialistaService = inject(EspecialistaService);

  especialistas = signal<Especialista[]>([]);
  indiceActual = signal<number>(0);

  ngOnInit() {
    this.especialistaService.obtenerEspecialistas().subscribe({
      next: (data: Especialista[]) => {
        this.especialistas.set(data);
      },
      error: (err) => console.error('Error cargando datos de Django:', err)
    });
  }

  // 📐 ESTILO DE DESPLAZAMIENTO: Ajustado a -314px por tarjeta para que el slider ruede perfecto
  estiloDesplazamiento = computed(() => {
    const pixeles = this.indiceActual() * -314;
    return `translateX(${pixeles}px)`;
  });

  moverIzquierda() {
    if (this.indiceActual() > 0) {
      this.indiceActual.update(idx => idx - 1);
    } else if (this.especialistas().length > 0) {
      this.indiceActual.set(this.especialistas().length - 1);
    }
  }

  moverDerecha() {
    if (this.indiceActual() < this.especialistas().length - 1) {
      this.indiceActual.update(idx => idx + 1);
    } else {
      this.indiceActual.set(0);
    }
  }
}