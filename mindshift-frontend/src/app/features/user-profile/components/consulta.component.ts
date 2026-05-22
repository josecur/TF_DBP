import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-consulta-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './consulta.component.html'
})
export class ConsultaComponent {
  @Input() citaData: any = null;
  @Output() cerrar = new EventEmitter<void>();

  cerrarModal() {
    this.cerrar.emit();
  }
}