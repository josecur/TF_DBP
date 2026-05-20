import { Routes } from '@angular/router';

export const routes: Routes = [
  // 1. La raíz '' carga directamente la HOME con todo su diseño modular
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  // 2. Acceso al cuestionario independiente
  {
    path: 'cuestionario',
    loadComponent: () => import('./features/cuestionario/cuestionario.component').then(m => m.CuestionarioComponent)
  },
  // 3. Acceso al panel privado del usuario logueado
  {
    path: 'user-profile',
    loadComponent: () => import('./features/user-profile/user-profile.component').then(m => m.UserProfileComponent)
  },
  // 4. Buscador global de profesionales
  {
    path: 'buscar-especialista',
    loadComponent: () => import('./features/buscador-expertos/buscador-expertos.component').then(m => m.BuscadorExpertosComponent)
  },
  // 5. Formulario dinámico de reclutamiento (Django)
  {
    path: 'registro-especialista',
    loadComponent: () => import('./features/especialista/registro-especialista/registro-especialista.component').then(m => m.RegistroEspecialistaComponent)
  },
  // 🎯 6. NUEVA RUTA DINÁMICA: Carga el perfil extendido (Corregido con su coma al final)
  {
    path: 'perfil-especialista/:id',
    loadComponent: () => import('./features/especialista/perfil.especialista/perfil-especialista.component').then(m => m.PerfilEspecialistaComponent)
  }, // 👈 ¡ESTA COMA ERA VITAL!
  // 7. Comodín de seguridad
  {
    path: '**',
    redirectTo: ''
  }
];