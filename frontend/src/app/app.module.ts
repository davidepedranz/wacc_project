import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Angular Material + Flex
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MdAutocompleteModule,
           MdButtonModule,
           MdButtonToggleModule,
           MdCardModule,
           MdCheckboxModule,
           MdChipsModule,
           MdDatepickerModule,
           MdDialogModule,
           MdExpansionModule,
           MdGridListModule,
           MdIconModule,
           MdInputModule,
           MdListModule,
           MdMenuModule,
           MdNativeDateModule,
           MdPaginatorModule,
           MdProgressBarModule,
           MdProgressSpinnerModule,
           MdRadioModule,
           MdRippleModule,
           MdSelectModule,
           MdSidenavModule,
           MdSliderModule,
           MdSlideToggleModule,
           MdSnackBarModule,
           MdSortModule,
           MdTableModule,
           MdTabsModule,
           MdToolbarModule,
           MdTooltipModule,
           MdStepperModule, } from '@angular/material';
import {HttpModule} from '@angular/http';
import {CdkTableModule} from '@angular/cdk/table';

@NgModule({
  exports: [
    CdkTableModule,
    MdAutocompleteModule,
    MdButtonModule,
    MdButtonToggleModule,
    MdCardModule,
    MdCheckboxModule,
    MdChipsModule,
    MdStepperModule,
    MdDatepickerModule,
    MdDialogModule,
    MdExpansionModule,
    MdGridListModule,
    MdIconModule,
    MdInputModule,
    MdListModule,
    MdMenuModule,
    MdNativeDateModule,
    MdPaginatorModule,
    MdProgressBarModule,
    MdProgressSpinnerModule,
    MdRadioModule,
    MdRippleModule,
    MdSelectModule,
    MdSidenavModule,
    MdSliderModule,
    MdSlideToggleModule,
    MdSnackBarModule,
    MdSortModule,
    MdTableModule,
    MdTabsModule,
    MdToolbarModule,
    MdTooltipModule,
  ]
})
export class MaterialModule {}

// Reactive Angular (ngrx)
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

// application status
import { reducers } from './store/reducers';
import { AuthenticationEffects } from './store/authentication/authentication.effects';

// authentication
import { AuthenticationGuard } from './guards/authentication/authentication.guard';
import { AuthenticationService } from './services/authentication/authentication.service';

// routing
import { AppRoutes } from './app.routing';

// UI components
import { AppComponent } from './components/app/app.component';
import { LoginComponent } from './components/login/login.component';
import { LoginFormComponent } from './components/login-form/login-form.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { UsersComponent } from './components/users/users.component';
import { ComponentsTableComponent } from './components/components-table/components-table.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    LoginComponent,
    NavbarComponent,
    UsersComponent,
    LoginFormComponent,
    ComponentsTableComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    FlexLayoutModule,

    // router
    RouterModule.forRoot(AppRoutes, {
      enableTracing: false
    }),

    // application status, using ngrx
    StoreModule.forRoot(reducers),
    EffectsModule.forRoot([AuthenticationEffects]),
    StoreDevtoolsModule.instrument({
      maxAge: 25
    }),

    // UI elements
    MaterialModule
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService
  ],
  bootstrap: [
    AppComponent,
    ComponentsTableComponent
  ]
})
export class AppModule { }
