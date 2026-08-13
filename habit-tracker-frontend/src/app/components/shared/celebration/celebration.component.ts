import { Component, Input, Output, EventEmitter, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { NgIf } from '@angular/common';
import confetti from 'canvas-confetti';

/**
 * Modal animado de celebracion con confeti.
 * Recibe un mensaje y un emoji como parametros y muestra una tarjeta
 * centrada sobre un fondo oscurecido. Se cierra automaticamente a los
 * 3 segundos o al hacer clic en cualquier parte.
 *
 * Uso: el componente llama al metodo publico abrir() cuando quiere
 * celebrar un logro o una meta cumplida.
 */
@Component({
  selector: 'app-celebration',
  standalone: true,
  imports: [NgIf],
  templateUrl: './celebration.component.html',
  styleUrl: './celebration.component.scss'
})
export class CelebrationComponent implements OnDestroy {

  /** Mensaje motivador que se muestra en el modal */
  @Input() mensaje: string = '';

  /** Emoji grande que representa la celebracion */
  @Input() emoji: string = '🎉';

  /** Evento que se emite cuando el modal se cierra, para encadenar celebraciones */
  @Output() cerrado = new EventEmitter<void>();

  /** Canvas donde se dibuja el confeti */
  @ViewChild('confettiCanvas', { static: false })
  canvasRef!: ElementRef<HTMLCanvasElement>;

  /** Controla si el modal esta visible */
  visible: boolean = false;

  /** Temporizador para cerrar el modal automaticamente a los 3 segundos */
  private timer: ReturnType<typeof setTimeout> | null = null;

  /** Lanzador de confeti asociado al canvas del modal */
  private confetiLanzador: confetti.CreateTypes | null = null;

  /**
   * Abre el modal, dispara el confeti y programa el cierre automatico.
   */
  abrir(): void {
    this.visible = true;

    // Espera un tick para que Angular renderice el modal y el canvas exista
    setTimeout(() => this.dispararConfeti(), 0);

    this.timer = setTimeout(() => {
      this.cerrar();
    }, 3000);
  }

  /**
   * Cierra el modal, detiene el temporizador de cierre y avisa al padre
   * para que pueda mostrar la siguiente celebracion si la hay.
   */
  cerrar(): void {
    this.visible = false;
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = null;
    }
    this.cerrado.emit();
  }

  /**
   * Lanza el confeti desde el centro, la izquierda y la derecha del canvas.
   */
  private dispararConfeti(): void {
    if (!this.canvasRef?.nativeElement) {
      return;
    }

    if (!this.confetiLanzador) {
      this.confetiLanzador = confetti.create(this.canvasRef.nativeElement, {
        resize: true,
        useWorker: true
      });
    }

    this.confetiLanzador({
      particleCount: 80,
      spread: 70,
      origin: { y: 0.6 }
    });

    this.confetiLanzador({
      particleCount: 40,
      angle: 60,
      spread: 55,
      origin: { x: 0, y: 0.8 }
    });

    this.confetiLanzador({
      particleCount: 40,
      angle: 120,
      spread: 55,
      origin: { x: 1, y: 0.8 }
    });
  }

  /**
   * Al destruir el componente limpia el temporizador y el confeti.
   */
  ngOnDestroy(): void {
    this.cerrar();
    confetti.reset();
  }
}
