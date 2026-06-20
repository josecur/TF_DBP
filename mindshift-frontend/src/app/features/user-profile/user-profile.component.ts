import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { PerfilUsuario } from '../../core/models/perfil-usuario.models';
import { TestComponent } from './test/test.component';
import { ConsultaComponent } from './components/consulta.component';
import { ReservaService } from '../../core/services/reserva.service'; 

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TestComponent, ConsultaComponent],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  private router = inject(Router);
  private http   = inject(HttpClient);
  private reservaService = inject(ReservaService);

  // Señales únicas
  misReservas = signal<any[]>([]);
  usuarioLogueado  = signal<PerfilUsuario | null>(null);
  mostrarTestPopup = signal<boolean>(false);
  mostrarCitaPopup = signal<boolean>(false);
  
  // Usaremos 'citaParaRevisar' para el modal de contacto y detalles
  citaParaRevisar = signal<any | null>(null);
  testSeleccionado = signal<any | null>(null);

  formNombre   = '';
  formApellido = ''; 
  formTelefono = '';

ngOnInit() {
  this.cargarDatosUsuario();
  this.cargarMisReservas(); // <-- Si esto se llama en otro sitio (como un constructor o un setter), se duplica
}

  cargarMisReservas() {
    const sesionStr = localStorage.getItem('session_active');
    if (sesionStr) {
      const sesion = JSON.parse(sesionStr);
      if (sesion?.id) {
        this.reservaService.obtenerReservasPorUsuario(sesion.id).subscribe({
          next: (data: any[]) => this.misReservas.set(data),
          error: (err: any) => console.error("Error cargando reservas:", err)
        });
      }
    }
  }

  cargarDatosUsuario() {
    const activeSession = localStorage.getItem('session_active');
    const uData = localStorage.getItem('usuario_mindstep');

    if (activeSession && uData) {
      const datosCompletos = JSON.parse(uData);
      this.usuarioLogueado.set(datosCompletos);
      this.formNombre = datosCompletos.nombres || datosCompletos.nombreUsuario || '';
      this.formApellido = datosCompletos.apellido || datosCompletos.apellidoUsuario || '';
      this.formTelefono = datosCompletos.telefono || datosCompletos.telefonoUsuario || '';
    } else {
      this.router.navigate(['/']);
    }
  }

  // Lógica para Tests
  abrirTest(intento: any) {
    if (intento) {
      this.testSeleccionado.set(intento);
    } else {
      this.testSeleccionado.set({ id: 1, puntuacion_total: 0, nivel_carga_calculado: 'SIN DATOS', fecha_evaluacion: 'N/A' });
    }
    this.mostrarTestPopup.set(true);
  }

  cerrarTest() {
    this.mostrarTestPopup.set(false);
    this.testSeleccionado.set(null);
  }

  // Lógica para Contacto y Detalles (Modal Unificado)
  abrirModalContacto(reserva: any) {
    this.citaParaRevisar.set(reserva);
  }

cerrarCita() {
  this.citaParaRevisar.set(null); // Limpiamos la señal unificada
}

  // Resto de métodos
  eliminarIntentoTest(idTest: number | string, indice: number) {
    if (!idTest || !confirm('¿Eliminar este diagnóstico?')) return;
    this.http.delete(`http://localhost:8000/api/historial-tests/${idTest}/`).subscribe({
      next: () => {
        const usuarioActual = this.usuarioLogueado();
        if (usuarioActual?.historial) {
          usuarioActual.historial.splice(indice, 1);
          localStorage.setItem('usuario_mindstep', JSON.stringify(usuarioActual));
          this.usuarioLogueado.set({ ...usuarioActual });
        }
      }
    });
  }

  actualizarInformacionPerfil() {
    const usuario = this.usuarioLogueado();
    if (!usuario) return;
    const actualizado = { ...usuario, nombres: this.formNombre, apellido: this.formApellido, telefono: this.formTelefono };
    localStorage.setItem('usuario_mindstep', JSON.stringify(actualizado));
    this.usuarioLogueado.set(actualizado);
    alert('Perfil actualizado correctamente.');
  }

  
}