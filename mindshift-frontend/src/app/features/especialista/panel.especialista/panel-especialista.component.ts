import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ReservaService } from '../../../core/services/reserva.service';

@Component({
  selector: 'app-panel-especialista',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './panel-especialista.component.html',
  styleUrls: ['./panel-especialista.component.css']
})
export class PanelEspecialistaComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private reservaService = inject(ReservaService);

  // Estados de modales
  mostrarPopupMedios = signal<boolean>(false);
  mediosContacto = { correo: '', whatsapp: '' };
  idReservaTemporal: number | null = null; // Guardamos el ID aquí para que no se pierda
  
  // Datos
  medico = signal<any | null>(null);
  consultas = signal<any[]>([]);
  citaParaRevisar = signal<any | null>(null);

  // Formularios
  formNombres = '';
  formApellidos = '';
  formAgenda = '';
  formTrayectoria = '';
  formAvatar = '';
  
  nuevoTitulo = '';
  nuevoContenido = '';

  ngOnInit() {
    const sesion = localStorage.getItem('session_active');
    if (!sesion) { this.router.navigate(['/']); return; }

    const datosMedicos = JSON.parse(sesion);
    this.medico.set(datosMedicos);
    
    this.formNombres = datosMedicos.nombreProfesional || '';
    this.formApellidos = datosMedicos.apellidoProfesional || '';
    this.formAgenda = datosMedicos.enlace_agenda || '';
    this.formTrayectoria = datosMedicos.descripcion_trayectoria || '';
    this.formAvatar = datosMedicos.avatar_icono || '';

    this.cargarSolicitudesDeEnlace();
  }

  cargarSolicitudesDeEnlace() {
    this.reservaService.obtenerTodasLasReservas().subscribe({
      next: (lista: any[]) => {
        const idMedico = Number(this.medico()?.id);
        this.consultas.set(lista.filter(c => Number(c.idProfesional) === idMedico));
      }
    });
  }

  abrirModalDiagnostico(cita: any) { 
    this.citaParaRevisar.set(cita); 
  }

  // PASO 1: Iniciar el proceso de vinculación
 atenderConsulta() {
    const cita = this.citaParaRevisar();
    if (!cita) return;
    
    this.idReservaTemporal = cita.id; // Guardamos el ID en la propiedad temporal
    this.citaParaRevisar.set(null);   // Cerramos el primer modal
    this.mostrarPopupMedios.set(true); // Abrimos el segundo
  }

// En panel-especialista.component.ts
finalizarVinculacion() {
  const id = this.idReservaTemporal;
  if (!id) return;

  const payload = {
    estado: 'Aceptado',
    contacto_correo: this.mediosContacto.correo,
    contacto_whatsapp: this.mediosContacto.whatsapp
  };

  // CAMBIA .put POR .patch
  // El PATCH es específicamente para actualizaciones parciales
  this.http.patch(`http://localhost:8000/api/reservas/${id}/`, payload).subscribe({
    next: () => {
      alert('¡Vinculación confirmada!');
      this.mostrarPopupMedios.set(false);
      this.cargarSolicitudesDeEnlace();
    },
    error: (err) => console.log('Error:', err.error)
  });
}

  guardarPerfil() {
    const payload = {
      nombreProfesional: this.formNombres,
      apellidoProfesional: this.formApellidos,
      enlace_agenda: this.formAgenda,
      descripcion_trayectoria: this.formTrayectoria,
      avatar_icono: this.formAvatar
    };
    this.http.put(`http://localhost:8000/api/profesionales/${this.medico().id}/`, payload).subscribe({
      next: (res: any) => {
        const sesionActualizada = { ...this.medico(), ...res };
        localStorage.setItem('session_active', JSON.stringify(sesionActualizada));
        this.medico.set(sesionActualizada);
        alert('Perfil actualizado.');
      }
    });
  }

  publicarArticulo() {
    this.http.post('http://localhost:8000/api/publicaciones/', {
      idProfesional: this.medico().id,
      titulo: this.nuevoTitulo,
      contenido: this.nuevoContenido
    }).subscribe({ next: () => alert('Publicado!') });
  }

  alCambiarFotoPerfil(event: any) {
    const archivo = event.target.files[0];
    if (archivo) {
      const lector = new FileReader();
      lector.onload = () => this.formAvatar = lector.result as string;
      lector.readAsDataURL(archivo);
    }
  }

  cerrarSesion() { localStorage.removeItem('session_active'); this.router.navigate(['/']); }
}