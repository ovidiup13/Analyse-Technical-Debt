import { Component, OnInit, Input } from "@angular/core";
import { Repository } from "../../models/repository.interface";

@Component({
  selector: "app-repo-stats",
  template: `
    <div>
      <h6>Git URI: <b>{{repository.uri}}</b></h6>
      <h6>Issue Tracker URI: <b>{{repository.issueTrackerURI}}</b></h6>
      <h6>Build command: <b>{{repository.buildCommand}}</b></h6>
    </div>
  `
})
export class RepoStatsComponent implements OnInit {
  @Input() repository: Repository;

  constructor() {}

  ngOnInit() {}
}
