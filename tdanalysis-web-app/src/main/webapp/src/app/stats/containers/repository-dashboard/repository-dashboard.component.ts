import { Component, OnInit } from "@angular/core";
import { Repository } from "../../models/repository.interface";
import { Observable } from "rxjs/Observable";
import { DataService } from "../../services/data-service.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-repository-dashboard",
  templateUrl: "./repository-dashboard.component.html",
  styleUrls: ["./repository-dashboard.component.css"]
})
export class RepositoryDashboardComponent implements OnInit {
  repositories$: Observable<Repository[]>;

  constructor(private router: Router, private dataService: DataService) {}

  ngOnInit() {
    this.repositories$ = this.dataService.getRepositories();
  }

  viewRepository(event: Repository) {
    this.router.navigate([`/repositories`, event.id]);
  }
}
