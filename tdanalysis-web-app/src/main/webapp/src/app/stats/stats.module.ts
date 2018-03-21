import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

// components
import { EffortTdStatsComponent } from "./components/effort-td-stats/effort-td-stats.component";
import { RepositoryDetailComponent } from "./components/repository-detail/repository-detail.component";

// services
import { DataService } from "./services/data-service.service";

// containers
import { RepositoryDashboardComponent } from "./containers/repository-dashboard/repository-dashboard.component";
import { RepositoryViewerComponent } from "./containers/repository-viewer/repository-viewer.component";
import { RouterModule, Routes } from "@angular/router";
import { ChartsModule } from "../charts/charts.module";
import { CommitStatsComponent } from "./components/commit-stats/commit-stats.component";
import { IssueStatsComponent } from "./components/issue-stats/issue-stats.component";
import { TdStatsComponent } from "./components/td-stats/td-stats.component";
import { RepoStatsComponent } from './components/repo-stats/repo-stats.component';

const routes: Routes = [
  {
    path: "repositories",
    children: [
      {
        path: "",
        component: RepositoryDashboardComponent
      },
      {
        path: ":id",
        component: RepositoryViewerComponent
      }
    ]
  }
];

@NgModule({
  imports: [CommonModule, RouterModule.forChild(routes), ChartsModule],
  declarations: [
    RepositoryDashboardComponent,
    RepositoryViewerComponent,
    RepositoryDetailComponent,
    EffortTdStatsComponent,
    CommitStatsComponent,
    IssueStatsComponent,
    TdStatsComponent,
    RepoStatsComponent
  ],
  providers: [DataService]
})
export class StatsModule {}
