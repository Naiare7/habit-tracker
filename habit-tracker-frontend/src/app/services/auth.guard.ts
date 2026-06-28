import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Guard de ruta que protege las paginas que requieren autenticacion.
 * Si el usuario no esta logueado, redirige a la pantalla de login.
 */
export const authGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estaLogueado()) {
    return true;
  }

  return router.parseUrl('/login');
};
