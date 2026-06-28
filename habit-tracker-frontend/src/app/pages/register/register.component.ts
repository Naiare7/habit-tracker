import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';

/**
 * Pantalla de registro de nuevo usuario.
 * Muestra un formulario con nombre, email y contrasena,
 * valida los campos y envia la peticion al backend.
 */
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private authService = inject(AuthService);

  nombre: string = '';
  email: string = '';
  contrasena: string = '';
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';

  /**
   * Valida que el email tenga un formato correcto.
   */
  private emailValido(email: string): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  }

  /**
   * Envia el formulario de registro al backend.
   * Valida los campos antes de enviar y muestra mensajes de exito o error.
   */
  registrar(): void {
    // Limpiar mensajes anteriores
    this.mensajeExito = '';
    this.mensajeError = '';

    // Validar campos obligatorios
    if (!this.nombre.trim() || !this.email.trim() || !this.contrasena.trim()) {
      this.mensajeError = 'Todos los campos son obligatorios 🌸';
      return;
    }

    // Validar formato de email
    if (!this.emailValido(this.email)) {
      this.mensajeError = 'El formato del email no es valido 📧';
      return;
    }

    // Validar longitud de contrasena
    if (this.contrasena.length < 6) {
      this.mensajeError = 'La contrasena debe tener al menos 6 caracteres 🔐';
      return;
    }

    this.cargando = true;

    this.authService.register(this.nombre, this.email, this.contrasena).subscribe({
      next: () => {
        this.cargando = false;
        this.mensajeExito = 'Cuenta creada con exito 🌱 Bienvenida!';
        this.nombre = '';
        this.email = '';
        this.contrasena = '';
      },
      error: (error) => {
        this.cargando = false;
        if (error.error && error.error.error) {
          this.mensajeError = error.error.error;
        } else {
          this.mensajeError = 'Error al crear la cuenta. Intentalo de nuevo.';
        }
      }
    });
  }
}
