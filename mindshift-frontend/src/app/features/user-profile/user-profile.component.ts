import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http'; // 🎯 INYECTADO PARA EL BORRADO EN DJANGO
import { PerfilUsuario } from '../cuestionario/cuestionario.component';

import { TestComponent } from './test/test.component';
import { ConsultaComponent } from './components/consulta.component';
 
@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TestComponent, ConsultaComponent],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  private router = inject(Router);
  private http = inject(HttpClient); // 🎯 INYECTADO PARA EL BORRADO FÍSICO

  usuarioLogueado = signal<PerfilUsuario | null>(null);

  // SIGNALS DE APERTURA Y CONTROL PARA LOS MODALES DE "VER MÁS"
  mostrarTestPopup = signal<boolean>(false);
  mostrarCitaPopup = signal<boolean>(false);
  testSeleccionado = signal<any | null>(null);
  citaSeleccionada = signal<any | null>(null);

  // Campos vinculados a la directiva de doble vía [(ngModel)]
  formNombre = '';
  formApellido = '';
  formTelefono = '';
  formCarrera = 'Escolar'; 

  ngOnInit() {
    this.cargarDatosUsuario();
  }

  cargarDatosUsuario() {
    const activeSession = localStorage.getItem('session_active');
    const uData = localStorage.getItem('usuario_mindstep');

    if (activeSession && uData) {
      const datosCompletos = JSON.parse(uData);
      this.usuarioLogueado.set(datosCompletos);

      // Limpieza de data sucia: Aísla el nombre por si se concatenaron apellidos por error
      const nombreLimpio = datosCompletos.nombres ? datosCompletos.nombres.split(' ')[0] : '';
      const apellidoLimpio = datosCompletos.apellidos || (datosCompletos.nombres ? datosCompletos.nombres.split(' ').slice(1).join(' ') : '');

      this.formNombre = nombreLimpio;
      this.formApellido = apellidoLimpio;
      this.formTelefono = datosCompletos.telefono || '';
      this.formCarrera = datosCompletos.carrera || 'Escolar';
    } else {
      this.router.navigate(['/']);
    }
  }

  // 🎯 DISPARADORES REACTIVOS: Pasan el objeto real seleccionado al popup hijo
  abrirTest(intento: any) {
    if (intento) {
      this.testSeleccionado.set(intento);
    } else {
      const usuario = this.usuarioLogueado();
      if (usuario && usuario.historial && usuario.historial.length > 0) {
        this.testSeleccionado.set(usuario.historial[usuario.historial.length - 1]);
      } else {
        // Objeto de respaldo con el tipado estricto si es la primera cuenta registrada
        this.testSeleccionado.set({ 
          id: 1, 
          puntuacion_total: usuario?.historial?.[0]?.puntuacion_total || 5, 
          nivel_carga_calculado: usuario?.nivelCargaMental || 'MODERADO', 
          distorsion_predominante: 'Pensamiento Autocrítico',
          fecha_evaluacion: 'Reciente' 
        });
      }
    }
    this.mostrarTestPopup.set(true);
  }

  cerrarTest() {
    this.mostrarTestPopup.set(false);
    this.testSeleccionado.set(null);
  }

  abrirCita() {
    this.citaSeleccionada.set({
      motivo: 'Carga mental elevada por entregas continuas y bloqueos en el desarrollo de arquitectura.',
      especialista: 'Mesa de Guardia General',
      canal: 'Teleconsulta WhatsApp',
      prioridad: 'Media Obligatoria'
    });
    this.mostrarCitaPopup.set(true);
  }

  cerrarCita() {
    this.mostrarCitaPopup.set(false);
    this.citaSeleccionada.set(null);
  }

  // 🎯 NUEVO MÉTODO: Se conecta con el DELETE de Django para purgar la fila en SQLite y el LocalStorage
  eliminarIntentoTest(idTest: number | string, indice: number) {
    if (!idTest) {
      alert('No se puede eliminar un registro mock provisional.');
      return;
    }

    const confirmar = confirm('¿Estás seguro de que deseas eliminar permanentemente este diagnóstico de tu historial?');
    if (!confirmar) return;

    const urlDelete = `http://localhost:8000/api/historial-tests/${idTest}/`;

    this.http.delete(urlDelete).subscribe({
      next: () => {
        const usuarioActual = this.usuarioLogueado();
        if (usuarioActual && usuarioActual.historial) {
          // Removemos la fila borrada del arreglo reactivo en caliente
          usuarioActual.historial.splice(indice, 1);
          
          // Sincronizamos la persistencia en caché local
          localStorage.setItem('usuario_mindstep', JSON.stringify(usuarioActual));
          this.usuarioLogueado.set({ ...usuarioActual });
        }
        alert('Diagnóstico clínico removido con éxito del historial.');
      },
      error: (err) => {
        console.error('Error al conectar con SQLite para borrar:', err);
        alert('Error de red al intentar eliminar el registro del servidor de Django.');
      }
    });
  }

  actualizarInformationPerfil() {
    // Mantener compatibilidad si se llama con este nombre desde el template anterior
    this.actualizarInformacionPerfil();
  }

  actualizarInformacionPerfil() {
    const usuario = this.usuarioLogueado();
    if (!usuario) return;

    usuario.nombres = this.formNombre;
    usuario.apellidos = this.formApellido;
    usuario.telefono = this.formTelefono;
    (usuario as any).carrera = this.formCarrera;

    localStorage.setItem('usuario_mindstep', JSON.stringify(usuario));
    localStorage.setItem('session_active', JSON.stringify({
      rol: 'usuario',
      id: usuario.id,
      nombres: usuario.nombres,
      apellidos: usuario.apellidos,
      correo: usuario.correo
    }));

    this.usuarioLogueado.set({ ...usuario });
    alert('¡Perfil actualizado con éxito en tu sesión de red!');
  }
}