import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';

/**
 * Pantalla de inicio de sesion.
 * Valida las credenciales y redirige al dashboard si son correctas.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  email: string = '';
  contrasena: string = '';
  cargando: boolean = false;
  mensajeError: string = '';

  /**
   * Valida que el email tenga un formato correcto.
   */
  private emailValido(email: string): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  }

  /**
   * Envia las credenciales al backend para iniciar sesion.
   * Si es exitoso, guarda el token y redirige al dashboard.
   */
  iniciarSesion(): void {
    this.mensajeError = '';

    if (!this.email.trim() || !this.contrasena.trim()) {
      this.mensajeError = 'Todos los campos son obligatorios 🌸';
      return;
    }

    if (!this.emailValido(this.email)) {
      this.mensajeError = 'El formato del email no es valido 📧';
      return;
    }

    this.cargando = true;

    this.authService.login(this.email, this.contrasena).subscribe({
      next: (respuesta) => {
        this.authService.guardarToken(respuesta.token!);
        this.authService.guardarUserId(respuesta.id);
        this.authService.guardarNombre(respuesta.name);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.cargando = false;
        if (error.error && error.error.error) {
          this.mensajeError = error.error.error;
        } else {
          this.mensajeError = 'Error al iniciar sesion. Intentalo de nuevo.';
        }
      }
    });
  }
}
