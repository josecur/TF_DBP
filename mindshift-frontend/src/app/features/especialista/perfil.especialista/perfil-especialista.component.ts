import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { EspecialistaService } from '../registro-especialista/especialista.service'; 
import { ReservaModalComponent } from '../../reserva/reserva-modal.component';

@Component({
  selector: 'app-perfil-especialista',
  standalone: true,
  imports: [CommonModule, RouterModule, ReservaModalComponent],
  templateUrl: './perfil-especialista.component.html',
  styleUrls: ['./perfil-especialista.component.css']
})
export class PerfilEspecialistaComponent implements OnInit {
  private route = inject(ActivatedRoute); 
  private especialistaService = inject(EspecialistaService); 

  especialista = signal<any | null>(null);
  mostrarModalCita = signal<boolean>(false);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.cargarDetalleEspecialista(Number(idParam));
      }
    });
  }

  cargarDetalleEspecialista(id: number) {
    this.especialistaService.obtenerEspecialistas().subscribe({
      next: (lista: any[]) => {
        const encontrado = lista.find(medico => medico.id == id);
        if (encontrado) {
          if (!encontrado.publicaciones) encontrado.publicaciones = [];
          this.especialista.set(encontrado);
        }
      },
      error: (err) => console.error('Error al cargar especialista:', err)
    });
  }

  // ✅ ESTA ES LA FUNCIÓN QUE TE FALTABA
  esCliente(): boolean {
    const sesion = localStorage.getItem('session_active');
    if (!sesion) return false;
    try {
      const usuario = JSON.parse(sesion);
      return usuario.rol === 'usuario';
    } catch (e) {
      return false;
    }
  }

  solicitarConsulta() {
    // Re-validamos por seguridad antes de abrir
    if (!this.esCliente()) {
      alert('Acción denegada: Solo los estudiantes pueden solicitar consultas.');
      return;
    }
    this.mostrarModalCita.set(true);
  }
}