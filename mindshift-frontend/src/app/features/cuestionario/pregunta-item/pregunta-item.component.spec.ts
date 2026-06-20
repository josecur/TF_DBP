import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pregunta-item',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="pregunta-box" *ngIf="datos">
      <h3>{{ datos.enunciado || 'Cargando enunciado...' }}</h3>
      
      <div class="opciones-container">
        <button *ngFor="let op of datos.opciones" 
                (click)="seleccionar(op.valor_puntos)"
                class="opcion-btn">
          {{ op.contenido }}
        </button>
      </div>
    </div>
  `,
  styles: [`
    .pregunta-box { padding: 20px; }
    .opcion-btn { display: block; width: 100%; margin: 10px 0; padding: 15px; cursor: pointer; }
  `]
})
export class PreguntaItemComponent {
  @Input() datos: any;
  @Output() respuestaSeleccionada = new EventEmitter<number>();

  seleccionar(valor: number) {
    this.respuestaSeleccionada.emit(valor);
  }
}