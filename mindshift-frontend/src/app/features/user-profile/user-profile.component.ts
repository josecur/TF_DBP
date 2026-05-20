import { Component, OnInit, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms'; // 🚀 Importado para que funcionen los inputs de edición
import { PerfilUsuario } from '../cuestionario/cuestionario.component';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule], // 👈 Agregado FormsModule aquí
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  
  usuarioLogueado = signal<PerfilUsuario | null>(null);
  editando = signal<boolean>(false);

  // Variables auxiliares amarradas al formulario de edición
  formNombres = '';
  formApellidos = '';
  formTelefono = '';
  formSector = '';
  formModalidad = '';

  // Variables operativas de micro-hábitos
  pausasCompletadas = signal<number>(0);
  pausasObjetivo = signal<number>(4);
  minutosEnfoque = signal<number>(0);

  porcentajeProgreso = computed(() => {
    if (this.pausasObjetivo() === 0) return 0;
    const calculo = Math.round((this.pausasCompletadas() / this.pausasObjetivo()) * 100);
    return calculo > 100 ? 100 : calculo;
  });

  constructor(private router: Router) {
    effect(() => {
      localStorage.setItem('mindstep_pausas', this.pausasCompletadas().toString());
    });
    effect(() => {
      localStorage.setItem('mindstep_enfoque', this.minutosEnfoque().toString());
    });
  }

  ngOnInit() {
    const datosGuardados = localStorage.getItem('usuario_mindstep');
    if (datosGuardados) {
      const usuario = JSON.parse(datosGuardados);
      this.usuarioLogueado.set(usuario);
      
      // Inicializamos los inputs del formulario con los valores reales guardados
      this.formNombres = usuario.nombres;
      this.formApellidos = usuario.apellidos;
      this.formTelefono = usuario.telefono;
      this.formSector = usuario.sector || 'TECNOLOGÍA Y SOFTWARE';
      this.formModalidad = usuario.modalidad || 'HÍBRIDO';

      const pausasPrevias = localStorage.getItem('mindstep_pausas');
      const enfoquePrevio = localStorage.getItem('mindstep_enfoque');
      if (pausasPrevias) this.pausasCompletadas.set(parseInt(pausasPrevias, 10));
      if (enfoquePrevio) this.minutosEnfoque.set(parseInt(enfoquePrevio, 10));
    }
  }

  // 🚀 FUNCIÓN NUEVA: Guarda los datos editados por el usuario manteniendo su historial intacto
  guardarCambiosPerfil() {
    const usuario = this.usuarioLogueado();
    if (usuario) {
      usuario.nombres = this.formNombres;
      usuario.apellidos = this.formApellidos;
      usuario.telefono = this.formTelefono;
      usuario.sector = this.formSector;
      usuario.modalidad = this.formModalidad;

      // Guardamos la estructura en la máquina
      localStorage.setItem('usuario_mindstep', JSON.stringify(usuario));
      this.usuarioLogueado.set({ ...usuario }); // Forzamos actualización reactiva
      this.editando.set(false);
      alert('¡Perfil corporativo actualizado con éxito!');
    }
  }

  registrarPausaCompletada() {
    if (this.pausasCompletadas() < this.pausasObjetivo()) {
      this.pausasCompletadas.update(actual => actual + 1);
    }
  }

  agregarMinutosEnfoque(minutos: number) {
    this.minutosEnfoque.update(actual => actual + minutos);
  }

  ejecutarCerrarSesion() {
    localStorage.removeItem('usuario_mindstep');
    localStorage.removeItem('mindstep_pausas');
    localStorage.removeItem('mindstep_enfoque');
    this.router.navigate(['/']);
  }
}