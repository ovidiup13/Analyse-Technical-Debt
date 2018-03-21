import {
  Component,
  OnInit,
  Input,
  OnChanges,
  SimpleChanges
} from "@angular/core";
import { IssueStats } from "../../models/stats.interface";

@Component({
  selector: "app-issue-stats",
  template: `
    <div>
      <app-bar-chart [data]="issueData" [options]="options"></app-bar-chart>
    </div>
  `
})
export class IssueStatsComponent implements OnChanges {
  @Input() issueStats: IssueStats[];

  issueData: any;
  options: any = {
    responsive: true,
    title: {
      display: true,
      name: "Number of commits per issue"
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

  ngOnInit() {}

  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
    const dataChanges = changes.issueStats.currentValue;
    if (dataChanges != null) {
      this.issueData = this.processData(dataChanges);
    }
  }

  private processData(stats: IssueStats[]) {
    console.log(stats);

    const labels = ["1 commit", "2-5 commits", "5-10 commits", "10+ commits"];

    const single = stats.filter(stat => stat.totalCommits === 1).length;

    console.log("Number of issues with a single commit: ", single);

    const double = stats.filter(
      stat => stat.totalCommits >= 2 && stat.totalCommits < 5
    ).length;

    console.log("Number of issues with 2-5 commits: ", double);

    const triple = stats.filter(
      stat => stat.totalCommits >= 5 && stat.totalCommits < 10
    ).length;

    const quadruple = stats.filter(stat => stat.totalCommits >= 10).length;

    return {
      labels: labels,
      datasets: [
        {
          label: "Commits",
          data: [single, double, triple, quadruple],
          backgroundColor: "#f48942"
        }
      ]
    };
  }
}
