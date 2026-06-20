import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({ 
  selector: 'app-diagnostico-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './diagnostico-modal.component.html',
  styleUrls: ['./diagnostico-modal.component.css']
})
export class DiagnosticoModalComponent {
  // Input con tipado flexible para evitar errores de referencia
  @Input() usuario: any = null; 
  @Output() cerrar = new EventEmitter<void>();

  /**
   * Getter para extraer la distorsión del historial.
   * Utiliza encadenamiento opcional para prevenir errores si usuario o historial es nulo.
   */
  get distorsionDetectada(): string {
    if (!this.usuario?.historial || this.usuario.historial.length === 0) {
      return 'neutro';
    }
    
    // Obtenemos el último registro del historial
    const ultimoTest = this.usuario.historial[this.usuario.historial.length - 1];
    
    // Priorizamos distorsión dominante, luego predominante, fallback a neutro
    const rawValue = ultimoTest?.distorsion_dominante || 
                     ultimoTest?.distorsion_predominante || 
                     'neutro';

    // Limpiamos el valor: minúsculas y sin espacios
    return rawValue.toString().toLowerCase().trim();
  }

  /**
   * Cierra el modal emitiendo el evento al padre
   */
  cerrarModal(): void {
    this.cerrar.emit();
  }

  /**
   * Acción de agendamiento
   */
  agendarConsulta(): void {
    // Aquí puedes integrar la lógica de navegación o llamado a servicio
    alert('Redireccionando al canal de atención prioritaria de MindStep...');
    this.cerrar.emit();
  }
}