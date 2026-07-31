import { Component, Input, OnChanges } from '@angular/core';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { DailyStats } from '../../models/stats.model';

/**
 * Datos que necesita una celda del calendario para dibujarse.
 * Si no hay datos del dia (null), la celda se pinta en gris.
 */
interface DiaCalendario {
  numero: number;
  fecha: string;
  fechaFormateada: string;
  datos: DailyStats | null;
  claseColor: string;
}

/**
 * Componente que muestra un calendario mensual con el nivel de cumplimiento
 * de cada dia. Recibe los datos por entrada (presentacional) y colorea cada
 * dia segun el porcentaje de habitos completados con la paleta cozy.
 */
@Component({
  selector: 'app-habit-calendar',
  standalone: true,
  imports: [NgClass, NgFor, NgIf],
  templateUrl: './habit-calendar.component.html',
  styleUrl: './habit-calendar.component.scss'
})
export class HabitCalendarComponent implements OnChanges {

  /** Datos de cumplimiento de los dias del mes con registros */
  @Input() datosCalendario: DailyStats[] = [];

  /** Anho del mes a mostrar (por defecto el actual) */
  @Input() anho: number = new Date().getFullYear();

  /** Mes a mostrar (por defecto el actual, 1 = enero, 12 = diciembre) */
  @Input() mes: number = new Date().getMonth() + 1;

  /** Iniciales de los dias de la semana (la semana empieza en lunes) */
  diasSemana: string[] = ['L', 'M', 'X', 'J', 'V', 'S', 'D'];

  /** Celdas de la cuadricula: null para los huecos del primer dia, luego cada dia del mes */
  celdas: (DiaCalendario | null)[] = [];

  /**
   * Se ejecuta cada vez que cambian los datos o el mes/anho de entrada.
   * Reconstruye la cuadricula del calendario.
   */
  ngOnChanges(): void {
    this.construirCalendario();
  }

  /**
   * Construye la cuadricula del mes: calcula cuantos dias tiene el mes,
   * cuantos huecos van antes del primer dia (semana empieza en lunes)
   * y clasifica cada dia segun su nivel de cumplimiento.
   */
  private construirCalendario(): void {
    const totalDias = new Date(this.anho, this.mes, 0).getDate();
    const huecosIniciales = (new Date(this.anho, this.mes - 1, 1).getDay() + 6) % 7;

    // Mapa de fecha (YYYY-MM-DD) a sus datos para buscar rapidamente por dia
    const datosPorFecha = new Map<string, DailyStats>();
    for (const dato of this.datosCalendario) {
      datosPorFecha.set(dato.date, dato);
    }

    const diasDelMes: DiaCalendario[] = [];
    for (let dia = 1; dia <= totalDias; dia++) {
      const fecha = this.formatearFecha(this.anho, this.mes, dia);
      const datos = datosPorFecha.get(fecha) ?? null;

      diasDelMes.push({
        numero: dia,
        fecha: fecha,
        fechaFormateada: this.formatearFechaLarga(this.anho, this.mes, dia),
        datos: datos,
        claseColor: this.obtenerClaseColor(datos)
      });
    }

    // Junta los huecos iniciales con los dias del mes
    this.celdas = [
      ...Array.from({ length: huecosIniciales }, () => null),
      ...diasDelMes
    ];
  }

  /**
   * Clasifica un dia segun su nivel de cumplimiento para asignarle un color.
   * Sin datos -> gris, 0% -> rojo suave, 100% -> verde, resto -> amarillo.
   *
   * @param datos - Datos del dia (null si no tiene registros)
   * @returns La clase CSS con el color correspondiente
   */
  private obtenerClaseColor(datos: DailyStats | null): string {
    if (!datos) {
      return 'sin-datos';
    }
    if (datos.percentage === 0) {
      return 'cero';
    }
    if (datos.percentage === 100) {
      return 'completo';
    }
    return 'parcial';
  }

  /**
   * Convierte una fecha en formato YYYY-MM-DD con ceros a la izquierda.
   * Se construye manualmente para evitar desfases de zona horaria.
   *
   * @param anho - El anho
   * @param mes - El mes (1 = enero, 12 = diciembre)
   * @param dia - El dia del mes
   * @returns La fecha en formato YYYY-MM-DD
   */
  private formatearFecha(anho: number, mes: number, dia: number): string {
    const mesTexto = mes.toString().padStart(2, '0');
    const diaTexto = dia.toString().padStart(2, '0');
    return `${anho}-${mesTexto}-${diaTexto}`;
  }

  /**
   * Formatea una fecha para mostrarla en el tooltip, por ejemplo "Lunes, 12 de julio".
   *
   * @param anho - El anho
   * @param mes - El mes (1 = enero, 12 = diciembre)
   * @param dia - El dia del mes
   * @returns La fecha larga en espanhol con la primera letra en mayuscula
   */
  private formatearFechaLarga(anho: number, mes: number, dia: number): string {
    const fecha = new Date(anho, mes - 1, dia);
    const texto = fecha.toLocaleDateString('es-ES', {
      weekday: 'long',
      day: 'numeric',
      month: 'long'
    });
    return texto.charAt(0).toUpperCase() + texto.slice(1);
  }

  /**
   * Redondea un porcentaje para mostrarlo en el tooltip sin decimales.
   *
   * @param porcentaje - El porcentaje a redondear
   * @returns El porcentaje redondeado al entero mas cercano
   */
  redondearPorcentaje(porcentaje: number): number {
    return Math.round(porcentaje);
  }

  /**
   * Devuelve el indice de cada celda para que Angular pueda hacer seguimiento
   * de los elementos de la cuadricula.
   *
   * @param indice - El indice de la celda en la lista
   * @returns El propio indice
   */
  trackByIndex(indice: number): number {
    return indice;
  }
}
