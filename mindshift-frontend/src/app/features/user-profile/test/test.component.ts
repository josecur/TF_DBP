import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-test-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './test.component.html'
})
export class TestComponent {
  // 🎯 Recibe dinámicamente el intento seleccionado de la tabla del perfil
  @Input() testData: any = null; 
  @Output() cerrar = new EventEmitter<void>();

  cerrarModal() {
    this.cerrar.emit();
  }
}