import {
  Component,
  OnInit,
  Input,
  OnChanges,
  SimpleChanges
} from "@angular/core";
import { Repository } from "../../models/repository.interface";
import { CommitStats } from "../../models/stats.interface";

@Component({
  selector: "app-commit-stats",
  template: `
    <div>
      <div>
        <h6><b>{{data?.totalCommits}}</b> commits</h6>
        <h6><b>{{data?.numberOfAuthors}}</b> collaborators</h6>
        <h6><b>{{data?.meanTicketsPerCommit | number:'1.1-3'}}</b> tickets referenced per commit</h6>
        <h6><b>{{data?.meanTDItemsPerCommit | number:'1.1-3'}}</b> TD items per commit</h6>
      </div>
      <app-bar-chart [data]="dataset" [options]="chartOptions"></app-bar-chart>
    </div>
  `,
  styleUrls: ["./commit-stats.component.css"]
})
export class CommitStatsComponent implements OnChanges {
  @Input() data: CommitStatsComponent;

  dataset: any;
  chartOptions: any = {
    responsive: true,
    title: {
      display: true,
      name: "Commits with and without issues"
    },
    scales: {
      xAxes: [
        {
          display: true
        }
      ],
      yAxes: [
        {
          display: true,
          ticks: {
            beginAtZero: true
          },
          scaleLabel: {
            display: true,
            labelString: "Number of commits"
          }
        }
      ]
    }
  };

  constructor() {}

  ngOnChanges(changes: SimpleChanges): void {
    const newData = changes.data.currentValue;
    if (newData != null) {
      this.dataset = this.processData(newData);
    }
  }

  processData(stats: CommitStats) {
    const labels = ["with tickets", "without tickets"];
    const data = [stats.commitsWithIssues, stats.commitsWithoutIssues];
    return {
      labels: labels,
      datasets: [{ label: "Commits", data: data, backgroundColor: "#f48942" }]
    };
  }
}
