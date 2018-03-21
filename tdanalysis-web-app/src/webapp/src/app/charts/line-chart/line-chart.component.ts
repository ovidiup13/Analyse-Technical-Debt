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
  selector: "app-line-chart",
  template: `
    <div>
        <canvas class="chart" width="800" height="400" #lineChart></canvas>
    </div>
  `
})
export class LineChartComponent implements OnChanges, AfterViewInit {
  @Input() data: any;
  @Input() options: any;

  @ViewChild("lineChart") chart: ElementRef;

  lineChart: Chart;

  constructor() {}

  ngAfterViewInit(): void {
    this.initChart();
  }

  initChart() {
    const canvas: HTMLCanvasElement = this.chart.nativeElement;
    const ctx: CanvasRenderingContext2D = canvas.getContext("2d");
    this.lineChart = new Chart(ctx, {
      type: "line",
      data: this.data || [],
      options: this.options || {}
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.lineChart == null) {
      return;
    } else {
      if (changes.data != null && changes.data.currentValue != null) {
        this.lineChart.data = changes.data.currentValue;
        this.lineChart.update();
      }

      if (changes.options != null && changes.options.currentValue != null) {
        this.lineChart.options = changes.options.currentValue;
        this.lineChart.update();
      }
    }
  }
}
