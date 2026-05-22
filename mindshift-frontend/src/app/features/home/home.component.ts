import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; 
import { HttpClient } from '@angular/common/http'; // 🎯 INYECTADO PARA PERSISTENCIA

import { CuestionarioComponent, PerfilUsuario } from '../cuestionario/cuestionario.component';
import { AppSugerenciaComponent } from './components/sugerencia/sugerencia.component';
import { AppPanelComponent } from './components/panel/panel.component';
import { DiagnosticoModalComponent } from './components/diganostico-modal.component/diagnostico-modal.component';
import { EspecialistaService } from '../especialista/registro-especialista/especialista.service'; 
import { LoginComponent } from '../login/login.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    CuestionarioComponent, 
    AppSugerenciaComponent, 
    AppPanelComponent,
    DiagnosticoModalComponent,
    LoginComponent
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  private especialistaService = inject(EspecialistaService); 
  private router = inject(Router); 
  private http = inject(HttpClient); // 🎯 INYECTADO PARA TRANSACCIONES ATÓMICAS

  estadoFlujo = signal<'QUIZ' | 'AUTH' | 'RESULTADOS'>('QUIZ');
  modoReevaluacion = signal<boolean>(false);
  mostrarModal = signal<boolean>(false);
  mostrarModalLogin = signal<boolean>(false);
  perfilReciente = signal<PerfilUsuario | null>(null);
  usuarioLogueado = signal<any | null>(null);
  listaEspecialistas = signal<any[]>([]);

  ngOnInit() {
    this.verificarSesion();
    this.cargarStaffMedico(); 
  }

  cargarStaffMedico() {
    this.especialistaService.obtenerEspecialistas().subscribe({
      next: (data) => this.listaEspecialistas.set(data),
      error: (err) => console.error('Error al traer médicos de SQLite:', err)
    });
  }

  verificarSesion() {
    const active = localStorage.getItem('session_active');
    const datosTest = localStorage.getItem('usuario_mindstep');
    
    if (active) this.usuarioLogueado.set(JSON.parse(active));
    if (datosTest) this.perfilReciente.set(JSON.parse(datosTest));
  }

  // 🎯 CAPTURA EN VIVO: Sincroniza el nivel calculado directamente en SQLite antes de abrir el modal
  onUsuarioActualizado(perfil: PerfilUsuario) {
    localStorage.setItem('usuario_mindstep', JSON.stringify(perfil));
    const sesion = { rol: 'usuario', id: perfil.id, nombres: perfil.nombres, correo: perfil.correo };
    localStorage.setItem('session_active', JSON.stringify(sesion));
    
    this.usuarioLogueado.set(sesion);
    this.perfilReciente.set(perfil);      
    this.modoReevaluacion.set(false);      
    
    // 🚀 CONEXIÓN RELACIONAL: Registra el nivel de carga mental calculado en Django
    const urlPatch = `http://localhost:8000/api/alumnos/${perfil.id}/`;
    const payloadPatch = {
      nombres: perfil.nombres,
      apellidos: perfil.apellidos,
      ultimo_nivel_carga: perfil.nivelCargaMental
    };

    this.http.patch<any>(urlPatch, payloadPatch).subscribe({
      next: (res) => {
        console.log('¡Columna ultimo_nivel_carga guardada con éxito en SQLite!', res);
        // Desplegamos el popup dinámico una vez asegurada la persistencia
        this.mostrarModal.set(true);           
      },
      error: (err) => {
        console.error('Error de sincronización HTTP con Django. Usando respaldo local.', err);
        this.mostrarModal.set(true);           
      }
    });
  }

  abrirLogin() { this.mostrarModalLogin.set(true); }
  cerrarLogin() { this.mostrarModalLogin.set(false); }
  activarReevaluacion() { this.modoReevaluacion.set(true); }
  cancelarReevaluacion() { this.modoReevaluacion.set(false); }
  cerrarPopupDiagnostico() { this.mostrarModal.set(false); }

  cerrarSesionDesdeNavbar() {
    this.usuarioLogueado.set(null);
    localStorage.clear();
    this.router.navigate(['/']).then(() => window.location.reload());
  }
}