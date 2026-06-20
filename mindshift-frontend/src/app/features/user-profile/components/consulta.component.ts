import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-consulta-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './consulta.component.html',
  styles: [] 
})
export class ConsultaComponent {
  // Recibe la reserva completa desde el padre
  @Input() citaData: any; 
  // Emite un evento para cerrar el modal
  @Output() cerrar = new EventEmitter<void>();

  cerrarModal() {
    this.cerrar.emit();
  }
}