import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pregunta-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pregunta-item.component.html',
  styleUrls: ['./pregunta-item.component.css']
})
export class PreguntaItemComponent {
  @Input() pregunta: any;
  @Output() respuestaSeleccionada = new EventEmitter<number>();

  opcionSeleccionada: number | null = null; // ✅ feedback visual al seleccionar

  enviarRespuesta(valor: number) {
    this.opcionSeleccionada = valor;
    // pequeño delay para que el usuario vea la selección antes de avanzar
    setTimeout(() => {
      this.opcionSeleccionada = null;
      this.respuestaSeleccionada.emit(valor);
    }, 200);
  }
}