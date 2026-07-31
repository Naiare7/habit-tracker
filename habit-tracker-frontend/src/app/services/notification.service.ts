import { Injectable } from '@angular/core';

/**
 * Servicio que gestiona las notificaciones del navegador.
 * Se encarga de solicitar el permiso, programar recordatorios
 * de habitos y cancelarlos cuando se desactivan.
 */
@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  /**
   * Solicita al usuario el permiso para mostrar notificaciones.
   * @returns Promise con el estado del permiso ('granted', 'denied' o 'default')
   */
  solicitarPermiso(): Promise<NotificationPermission> {
    // Si el navegador no soporta notificaciones, se devuelve 'denied'
    if (!('Notification' in window)) {
      return Promise.resolve('denied');
    }

    // Si ya se concedio el permiso, no hace falta volver a preguntar
    if (Notification.permission === 'granted') {
      return Promise.resolve('granted');
    }

    return Notification.requestPermission();
  }

  /**
   * Programa un recordatorio para la proxima vez que ocurra la hora indicada.
   * Cuando llega el momento, muestra una notificacion con el nombre del habito.
   *
   * @param habitName - Nombre del habito que se recuerda
   * @param time - Hora en formato HH:mm (ej: '09:30')
   * @returns El id del timeout programado, para poder cancelarlo despues
   */
  scheduleReminder(habitName: string, time: string): number {
    const proximaHora = this.calcularProximaHora(time);
    const delay = proximaHora.getTime() - Date.now();

    // Se usa setTimeout porque la Web Notifications API no programa sola
    return window.setTimeout(() => this.mostrarNotificacion(habitName), delay);
  }

  /**
   * Cancela un recordatorio programado previamente.
   *
   * @param timeoutId - El id del timeout devuelto por scheduleReminder
   */
  cancelarRecordatorio(timeoutId: number): void {
    window.clearTimeout(timeoutId);
  }

  /**
   * Calcula la proxima vez que ocurre una hora concreta.
   * Si la hora de hoy ya paso, devuelve la del dia siguiente.
   *
   * @param time - Hora en formato HH:mm
   * @returns Date con la proxima ocurrencia de esa hora
   */
  private calcularProximaHora(time: string): Date {
    const partes = time.split(':');
    const horas = parseInt(partes[0], 10);
    const minutos = parseInt(partes[1], 10);

    const proximaHora = new Date();
    proximaHora.setHours(horas, minutos, 0, 0);

    // Si ya paso la hora de hoy, se programa para manana
    if (proximaHora.getTime() <= Date.now()) {
      proximaHora.setDate(proximaHora.getDate() + 1);
    }

    return proximaHora;
  }

  /**
   * Muestra la notificacion del recordatorio.
   *
   * @param habitName - Nombre del habito que se recuerda
   */
  private mostrarNotificacion(habitName: string): void {
    new Notification('Habit Tracker', {
      body: `Hey, es hora de ${habitName} 🌿`
    });
  }
}
