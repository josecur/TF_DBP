import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; 
import { CuestionarioComponent, PerfilUsuario } from '../cuestionario/cuestionario.component';
import { AppSugerenciaComponent } from './components/sugerencia/sugerencia.component';
import { AppPanelComponent } from './components/panel/panel.component';
import { DiagnosticoModalComponent } from './components/diganostico-modal.component/diagnostico-modal.component';
import { LoginComponent } from '../login/login.component';
import { EspecialistaService } from '../especialista/registro-especialista/especialista.service'; // 🚀 Ajusta la ruta a tu servicio

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
  private especialistaService = inject(EspecialistaService); // 👈 Inyectamos el servicio de la BD

  estadoFlujo = signal<'QUIZ' | 'AUTH' | 'RESULTADOS'>('QUIZ');
  modoReevaluacion = signal<boolean>(false);
  mostrarModal = signal<boolean>(false);
  mostrarModalLogin = signal<boolean>(false);
  perfilReciente = signal<PerfilUsuario | null>(null);

  // 🩺 SEÑAL DE ESPECIALISTAS VIVOS DESDE LA BD
  listaEspecialistas = signal<any[]>([]);

  ngOnInit() {
    this.verificarSesion();
    this.cargarStaffMedico(); // 🚀 Jalamos los psicólogos de SQLite al arrancar
  }

  // Carga los datos desde Django REST Framework
  cargarStaffMedico() {
    this.especialistaService.obtenerEspecialistas().subscribe({
      next: (data) => {
        this.listaEspecialistas.set(data); // Guardamos los médicos reales en la señal
      },
      error: (err) => console.error('Error al traer médicos de SQLite:', err)
    });
  }

  usuarioLogueado = computed(() => {
    const active = localStorage.getItem('session_active');
    return active ? JSON.parse(active) : null;
  });

  verificarSesion() {
    const active = localStorage.getItem('session_active');
    const datosTest = localStorage.getItem('usuario_mindstep');
    if (datosTest && !active) {
      const u = JSON.parse(datosTest);
      localStorage.setItem('session_active', JSON.stringify({ rol: 'usuario', nombres: u.nombres }));
    }
  }

  onUsuarioActualizado(perfil: PerfilUsuario) {
    localStorage.setItem('usuario_mindstep', JSON.stringify(perfil));
    localStorage.setItem('session_active', JSON.stringify({ rol: 'usuario', nombres: perfil.nombres }));
    this.perfilReciente.set(perfil);       
    this.modoReevaluacion.set(false);      
    this.mostrarModal.set(true);           
  }

  abrirLogin() { this.mostrarModalLogin.set(true); }
  cerrarLogin() { this.mostrarModalLogin.set(false); }
  activarReevaluacion() { this.modoReevaluacion.set(true); }
  cancelarReevaluacion() { this.modoReevaluacion.set(false); }
  cerrarPopupDiagnostico() { this.mostrarModal.set(false); }
}