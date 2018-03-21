import { Component, OnInit, Input, EventEmitter, Output } from "@angular/core";
import { Repository } from "../../models/repository.interface";

@Component({
  selector: "app-repository-detail",
  templateUrl: "./repository-detail.component.html",
  styleUrls: ["./repository-detail.component.css"]
})
export class RepositoryDetailComponent implements OnInit {
  @Input() repository: Repository;

  @Output() view: EventEmitter<Repository> = new EventEmitter();

  constructor() {}

  ngOnInit() {}

  viewRepository() {
    this.view.emit(this.repository);
  }
}
