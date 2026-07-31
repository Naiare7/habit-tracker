import { Component, Input, OnChanges } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ScriptableContext } from 'chart.js';
import { NgIf } from '@angular/common';
import { WeeklyStatsEntry } from '../../models/stats.model';

/**
 * Componente que muestra un grafico de barras con los habitos completados
 * cada dia de la semana actual.
 * Recibe los datos por entrada (presentacional) y dibuja el grafico
 * con los colores de la paleta cozy de la aplicacion.
 */
@Component({
  selector: 'app-weekly-chart',
  standalone: true,
  imports: [BaseChartDirective, NgIf],
  templateUrl: './weekly-chart.component.html',
  styleUrl: './weekly-chart.component.scss'
})
export class WeeklyChartComponent implements OnChanges {

  /** Datos de la semana: un elemento por dia con el numero de habitos completados */
  @Input() datosSemanales: WeeklyStatsEntry[] = [];

  /** Configuracion de los datos del grafico de barras */
  datosGrafico: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: this.crearDegradadoBarras,
      borderRadius: 8,
      maxBarThickness: 48
    }]
  };

  /** Opciones de apariencia del grafico con los colores de la paleta cozy */
  opcionesGrafico: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
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
            const cantidad = context.parsed.y;
            return cantidad === 1 ? '1 hábito' : `${cantidad} hábitos`;
          }
        }
      }
    },
    scales: {
      x: {
        grid: { color: 'rgba(139, 111, 71, 0.1)' },
        ticks: { color: '#8B6F47', font: { family: 'Nunito Sans', size: 14 } }
      },
      y: {
        beginAtZero: true,
        ticks: {
          color: '#8B6F47',
          stepSize: 1,
          font: { family: 'Nunito Sans', size: 14 }
        },
        grid: { color: 'rgba(139, 111, 71, 0.1)' }
      }
    }
  };

  /**
   * Se ejecuta cada vez que cambian los datos de entrada.
   * Actualiza las etiquetas y valores del grafico.
   */
  ngOnChanges(): void {
    this.actualizarGrafico();
  }

  /**
   * Actualiza las etiquetas (dias de la semana) y los valores
   * (habitos completados) del grafico con los datos recibidos.
   */
  private actualizarGrafico(): void {
    this.datosGrafico.labels = this.datosSemanales.map(dia => dia.dayName);
    this.datosGrafico.datasets[0].data =
      this.datosSemanales.map(dia => dia.completedCount);
  }

  /**
   * Crea un degradado suave de la paleta cozy para las barras del grafico.
   * Va del lila pastel en la base al melocoton en la parte superior.
   * @param contexto - Contexto que Chart.js pasa a las opciones scriptables
   * @returns Un degradado lineal de dos colores de la paleta
   */
  private crearDegradadoBarras(contexto: ScriptableContext<'bar'>): string | CanvasGradient {
    const { ctx, chartArea } = contexto.chart;

    // Si el area del grafico aun no se ha calculado, usa un color plano
    if (!chartArea) {
      return '#FFB347';
    }

    const degradado = ctx.createLinearGradient(0, chartArea.bottom, 0, chartArea.top);
    degradado.addColorStop(0, '#C3B1E1');
    degradado.addColorStop(1, '#FFB347');
    return degradado;
  }
}
