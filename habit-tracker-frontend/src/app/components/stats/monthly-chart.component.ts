import { Component, Input, OnChanges } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { NgIf } from '@angular/common';
import { MonthlyStats } from '../../models/stats.model';

/**
 * Componente que muestra un grafico de tipo doughnut con el porcentaje
 * de cumplimiento mensual del usuario.
 * Recibe los datos por entrada (presentacional), dibuja el anillo
 * con los colores de la paleta cozy y muestra el porcentaje en grande
 * en el centro del grafico.
 */
@Component({
  selector: 'app-monthly-chart',
  standalone: true,
  imports: [BaseChartDirective, NgIf],
  templateUrl: './monthly-chart.component.html',
  styleUrl: './monthly-chart.component.scss'
})
export class MonthlyChartComponent implements OnChanges {

  /** Datos de cumplimiento del mes (puede ser null si aun no hay datos) */
  @Input() datosMensuales: MonthlyStats | null = null;

  /** Porcentaje de cumplimiento del mes redondeado a entero */
  porcentaje: number = 0;

  /** Configuracion de los datos del grafico de doughnut */
  datosGrafico: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['Cumplido', 'Restante'],
    datasets: [{
      data: [0, 100],
      backgroundColor: ['#A8C5A0', '#E8DDD0'],
      borderWidth: 0
    }]
  };

  /** Opciones de apariencia del grafico con los colores de la paleta cozy */
  opcionesGrafico: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '72%',
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: '#FFF3B0',
        titleColor: '#8B6F47',
        bodyColor: '#8B6F47',
        cornerRadius: 12,
        displayColors: false,
        callbacks: {
          label: (context) => {
            const valor = context.parsed;
            if (context.label === 'Cumplido') {
              return `${valor}% del mes cumplido`;
            }
            return `${valor}% restante`;
          }
        }
      }
    }
  };

  /**
   * Se ejecuta cada vez que cambian los datos de entrada.
   * Actualiza el porcentaje central y los valores del grafico.
   */
  ngOnChanges(): void {
    this.actualizarGrafico();
  }

  /**
   * Actualiza el porcentaje central y la porcion del anillo
   * que representa el cumplimiento del mes.
   */
  private actualizarGrafico(): void {
    if (!this.datosMensuales) {
      return;
    }

    this.porcentaje = Math.round(this.datosMensuales.percentage);
    this.datosGrafico.datasets[0].data = [this.porcentaje, 100 - this.porcentaje];
  }
}
