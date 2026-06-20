import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { PerfilUsuario } from '../../core/models/perfil-usuario.models';
import { EspecialistaService } from '../especialista/registro-especialista/especialista.service';

import { CuestionarioComponent } from '../cuestionario/cuestionario.component';
import { AppSugerenciaComponent } from './components/sugerencia/sugerencia.component';
import { AppPanelComponent } from './components/panel/panel.component';
import { DiagnosticoModalComponent } from './components/diganostico-modal.component/diagnostico-modal.component';
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
  private router               = inject(Router);
  private http                 = inject(HttpClient);

  estadoFlujo        = signal<'QUIZ' | 'AUTH' | 'RESULTADOS'>('QUIZ');
  modoReevaluacion   = signal<boolean>(false);
  mostrarModal       = signal<boolean>(false);
  mostrarModalLogin  = signal<boolean>(false);
  perfilReciente     = signal<PerfilUsuario | null>(null);
  usuarioLogueado    = signal<any | null>(null);
  listaEspecialistas = signal<any[]>([]);

  ngOnInit() {
    this.verificarSesion();
    this.cargarStaffMedico();
  }

  cargarStaffMedico() {
    this.especialistaService.obtenerEspecialistas().subscribe({
      next:  (data) => this.listaEspecialistas.set(data),
      error: (err)  => console.error('Error al traer médicos:', err)
    });
  }

  verificarSesion() {
    const active    = localStorage.getItem('session_active');
    const datosTest = localStorage.getItem('usuario_mindstep');
    
    if (active) {
      this.usuarioLogueado.set(JSON.parse(active));
    }
    
    // Solo cargamos el perfil extendido si el rol activo es de un alumno normal
    if (datosTest && this.usuarioLogueado()?.rol === 'usuario') {
      this.perfilReciente.set(JSON.parse(datosTest));
    }
  }

  onUsuarioActualizado(perfil: any) {
    const perfilNormalizado: PerfilUsuario = {
      id:               perfil.id,
      nombres:          perfil.nombres,
      apellido:         perfil.apellido        ?? '',
      correo:           perfil.correo          ?? '',
      telefono:         perfil.telefono        ?? '',
      nivelCargaMental: perfil.nivelCargaMental ?? perfil.nivel_riesgo ?? '',
      puntaje:          perfil.puntaje         ?? 0,
      historial:        perfil.historial       ?? []
    };

    localStorage.setItem('usuario_mindstep', JSON.stringify(perfilNormalizado));

    const sesion = {
      rol:     'usuario',
      id:      perfilNormalizado.id,
      nombres: perfilNormalizado.nombres
    };
    localStorage.setItem('session_active', JSON.stringify(sesion));

    this.usuarioLogueado.set(sesion);
    this.perfilReciente.set(perfilNormalizado);
    this.modoReevaluacion.set(false);

    const urlPatch     = `http://localhost:8000/api/usuarios/${perfilNormalizado.id}/`;
    const payloadPatch = { nivel_riesgo: perfilNormalizado.nivelCargaMental };

    this.http.patch<any>(urlPatch, payloadPatch).subscribe({
      next:  ()    => this.mostrarModal.set(true),
      error: (err) => {
        console.error('Error sync Django:', err);
        this.mostrarModal.set(true);
      }
    });
  }

  abrirLogin()             { this.mostrarModalLogin.set(true);  }
  cerrarLogin()            { this.mostrarModalLogin.set(false); }
  activarReevaluacion()    { this.modoReevaluacion.set(true);   }
  cancelarReevaluacion()   { this.modoReevaluacion.set(false);  }
  cerrPopupDiagnostico() { this.mostrarModal.set(false);      }

  cerrarSesionDesdeNavbar() {
    this.usuarioLogueado.set(null);
    this.perfilReciente.set(null);
    localStorage.clear();
    this.router.navigate(['/']).then(() => window.location.reload());
  }
}