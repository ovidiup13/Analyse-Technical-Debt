import {
  Component,
  OnInit,
  Input,
  OnChanges,
  SimpleChanges
} from "@angular/core";
import { IssueStats, SimpleStats } from "../../models/stats.interface";
import { StatsCalculator } from "../../helpers/stats-calculator";

@Component({
  selector: "app-td-stats",
  template: `
    <div>
      <div>
        <h4>Technical Debt Stats</h4>
        <h6>Mean: <b>{{tdStats?.mean | number:'1.1-3'}} TD items</b></h6>
        <h6>Standard deviation: <b>{{tdStats?.std | number:'1.1-3'}} TD items</b></h6>
      </div>      
      <app-line-chart [data]="dataset" [options]="options"></app-line-chart>
    </div>
  `
})
export class TdStatsComponent implements OnChanges {
  @Input() data: IssueStats[];

  dataset: any;
  options: any;

  tdStats: SimpleStats;

  constructor() {}

  ngOnChanges(changes: SimpleChanges): void {
    const newData = changes.data.currentValue;
    if (newData != null) {
      this.dataset = this.processData(newData);
    }
  }

  private processData(stats: IssueStats[]) {
    const labels = stats.map(issue => issue.issueKey);
    const data = stats.map(issue => issue.technicalDebt);

    const td_mean = StatsCalculator.getMean(data);
    this.tdStats = {
      mean: td_mean,
      std: StatsCalculator.getStandardDeviation(td_mean, data)
    };

    return {
      labels: labels,
      datasets: [
        {
          label: "Technical Debt Count",
          data: data,
          backgroundColor: "#f48942"
        }
      ]
    };
  }
}
