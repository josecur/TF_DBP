import { Component, Input, Output, EventEmitter, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaService } from '../../core/services/reserva.service';

@Component({
  selector: 'app-reserva-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reserva-modal.component.html',
  styleUrls: ['./reserva-modal.component.css']
})
export class ReservaModalComponent {
  private reservaService = inject(ReservaService);

  @Input() medico: any; 
  @Output() cerrar = new EventEmitter<void>();

  mensajeEstudiante = signal<string>('');
  cargando = signal<boolean>(false);
  error = signal<string | null>(null);

  confirmarReserva() {
    this.error.set(null);
    const sesion = localStorage.getItem('session_active');
    
    if (!sesion) {
      this.error.set('Debes iniciar sesión para enviar una solicitud.');
      return;
    }

    const usuario = JSON.parse(sesion);

    if (usuario.rol !== 'usuario') {
      this.error.set('Acción denegada: Solo los usuarios pueden solicitar consultas.');
      return;
    }

    this.cargando.set(true);

    // Creamos el objeto de solicitud incluyendo el mensaje
    const reservaData = {
      idUsuario: usuario.id,
      idProfesional: this.medico.id,
      motivo: this.mensajeEstudiante(), // Aquí pasamos el mensaje del estudiante
      fecha: new Date().toISOString(),
      estado: 'Pendiente' // Estado inicial para que el especialista lo vea
    };

    this.reservaService.crearReserva(reservaData).subscribe({
      next: () => {
        this.cargando.set(false);
        alert('¡Solicitud enviada con éxito! El especialista revisará tu perfil.');
        this.cerrar.emit();
      },
      error: () => {
        this.cargando.set(false);
        this.error.set('Error al enviar la solicitud al servidor.');
      }
    });
  }
}