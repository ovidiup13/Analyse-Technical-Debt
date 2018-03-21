import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { HttpClientModule } from "@angular/common/http";
import { RouterModule, Routes } from "@angular/router";

// components
import { NotFoundComponent } from "./not-found.component";
import { AppComponent } from "./app.component";

// custom modules
import { StatsModule } from "./stats/stats.module";
import { HomeComponent } from "./home.component";
import { NavComponent } from './nav/nav.component';

const routes: Routes = [
  {
    path: "",
    component: HomeComponent,
    pathMatch: "full" // match empty string
  },
  {
    path: "**", // any routes that do not exist in the application
    component: NotFoundComponent
  }
];

@NgModule({
  declarations: [AppComponent, NotFoundComponent, HomeComponent, NavComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
    StatsModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
