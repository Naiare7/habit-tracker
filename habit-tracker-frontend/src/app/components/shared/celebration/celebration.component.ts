import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import confetti from 'canvas-confetti';

/**
 * Componente de celebración que muestra un modal animado con confeti
 * cuando se desbloquea un logro o se alcanza un hito importante.
 * Recibe un mensaje y un emoji como parametros y se cierra automaticamente
 * a los 3 segundos o al hacer clic en el boton o en el fondo.
 */
@Component({
  selector: 'app-celebration',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './celebration.component.html',
  styleUrls: ['./celebration.component.scss']
})
export class CelebrationComponent implements OnInit, OnDestroy {

  /** Mensaje motivador a mostrar en la celebracion */
  @Input() mensaje: string = '¡Genial! Un paso más';

  /** Emoji representativo del logro o celebracion */
  @Input() emoji: string = '🎉';

  /** Evento que se emite cuando el modal se cierra */
  @Output() cerrado = new EventEmitter<void>();

  /** Identificador del timeout para cerrar automaticamente */
  private timeoutId: ReturnType<typeof setTimeout> | null = null;

  /**
   * Inicializa el componente lanzando confeti y configurando el cierre automatico.
   */
  ngOnInit(): void {
    this.lanzarConfeti();
    this.configurarCierreAutomatico();
  }

  /**
   * Limpia el timeout al destruir el componente para evitar fugas de memoria.
   */
  ngOnDestroy(): void {
    if (this.timeoutId !== null) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
  }

  /**
   * Lanza confeti con una configuracion suave acorde al estilo cozy de la aplicacion.
   */
  private lanzarConfeti(): void {
    const duracion = 2000;
    const animacionFinal = Date.now() + duracion;

    // Lanza confeti desde el centro con colores de la paleta cozy
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 },
      colors: ['#FFB347', '#A8C5A0', '#C3B1E1', '#F2C4CE', '#FFF3B0']
    });

    // Continua lanzando confeti durante un breve tiempo para mayor efecto
    const intervalo = setInterval(() => {
      const tiempoRestante = animacionFinal - Date.now();

      if (tiempoRestante <= 0) {
        clearInterval(intervalo);
        return;
      }

      confetti({
        particleCount: 50,
        angle: 60,
        spread: 55,
        origin: { x: 0 },
        colors: ['#FFB347', '#A8C5A0', '#C3B1E1']
      });

      confetti({
        particleCount: 50,
        angle: 120,
        spread: 55,
        origin: { x: 1 },
        colors: ['#F2C4CE', '#FFF3B0', '#B8D4E8']
      });
    }, 250);
  }

  /**
   * Configura el cierre automatico del modal tras 3 segundos.
   */
  private configurarCierreAutomatico(): void {
    this.timeoutId = setTimeout(() => {
      this.cerrar();
    }, 3000);
  }

  /**
   * Cierra el modal, limpia el timeout y emite el evento al componente padre.
   */
  cerrar(): void {
    if (this.timeoutId !== null) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
    this.cerrado.emit();
  }
}
