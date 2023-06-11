import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './components/home/home.component';
import { ChartsComponent } from './components/charts/charts.component';
import { LoginComponent } from './components/login/login.component';


const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'charts', component: ChartsComponent },
  { path: 'login', component: LoginComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
