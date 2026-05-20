import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  
  // Verificamos si existe la sesión del usuario en el disco local
  const tieneSesion = localStorage.getItem('usuario_mindstep');

  if (tieneSesion) {
    return true; // 🔓 Autorizado: Permite el acceso a la ruta /user-profile
  }

  // 🔒 Denegado: Si no se ha registrado, lo redirige a la Home
  router.navigate(['/']);
  return false;
};