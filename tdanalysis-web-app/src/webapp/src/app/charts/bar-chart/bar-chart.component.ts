import {
  Component,
  OnInit,
  Input,
  OnChanges,
  SimpleChanges,
  ElementRef,
  ViewChild,
  AfterViewInit
} from "@angular/core";

import * as Chart from "chart.js";

@Component({
  selector: "app-bar-chart",
  template: `
    <div>
        <canvas class="chart" width="800" height="400" #barChart></canvas>
    </div>
  `
})
export class BarChartComponent implements OnChanges, AfterViewInit {
  @Input() data: any;
  @Input() options: any;

  @ViewChild("barChart") chart: ElementRef;

  barChart: Chart;

  constructor() {}

  ngAfterViewInit(): void {
    this.initChart();
  }

  initChart() {
    const canvas: HTMLCanvasElement = this.chart.nativeElement;
    const ctx: CanvasRenderingContext2D = canvas.getContext("2d");
    this.barChart = new Chart(ctx, {
      type: "bar",
      data: this.data || [],
      options: this.options || {}
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.barChart == null) {
      return;
    } else {
      if (changes.data != null && changes.data.currentValue != null) {
        this.barChart.data = changes.data.currentValue;
        this.barChart.update();
      }

      if (changes.options != null && changes.options.currentValue != null) {
        this.barChart.options = changes.options.currentValue;
        this.barChart.update();
      }
    }
  }
}
