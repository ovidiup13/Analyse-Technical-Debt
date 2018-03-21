import { Component, OnInit } from "@angular/core";
import { Observable, Subscribable } from "rxjs/Observable";
import { mergeMap, zip, map, mergeAll } from "rxjs/operators";
import { forkJoin } from "rxjs/observable/forkJoin";

@Component({
  selector: "app-root",
  template: `
    <div>
      <app-nav></app-nav>
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {
  constructor() {}
}
