import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-home",
  template: `
    <div class="page">
      <h1 class="title">Technical Debt Analysis</h1>
      <p>Welcome to the web application!</p>
      <p>Click on Dashboard to get started.</p>
    </div>
  `
})
export class HomeComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
