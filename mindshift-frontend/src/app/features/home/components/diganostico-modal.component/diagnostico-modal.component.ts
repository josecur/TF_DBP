import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PerfilUsuario } from '../../../cuestionario/cuestionario.component';

@Component({
  selector: 'app-diagnostico-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './diagnostico-modal.component.html',
  styleUrls: ['./diagnostico-modal.component.css']
})
export class DiagnosticoModalComponent {
  // Recibe la estructura compleja de datos de forma segura
  @Input() usuario: PerfilUsuario | null = null;
  
  @Output() cerrar = new EventEmitter<void>();

  cerrarModal() {
    this.cerrar.emit();
  }

  agendarConsulta() {
    alert('Redireccionando al canal de atención prioritaria de MindStep...');
    this.cerrar.emit();
  }
}