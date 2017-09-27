import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { StoreModule } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';

import { RouterModule } from '@angular/router';
import { AppRoutes } from '../../app.routing';

import { reducers } from '../reducers';
import * as AuthenticationActions from './authentication.actions';
import { AuthenticationEffects } from './authentication.effects';
import { AuthenticationService } from '../../services/authentication.service';
import { TokenService } from '../../services/token.service';

describe('AuthenticationEffects', () => {
    let effects: AuthenticationEffects;
    // let actions: Observable<any>;
    let actions: ReplaySubject<any>;

    let tokenService: TokenService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                StoreModule.forRoot(reducers),
                RouterTestingModule.withRoutes([])
            ],
            providers: [
                AuthenticationEffects,
                provideMockActions(() => actions),
                AuthenticationService,
                TokenService
            ],
        });
        effects = TestBed.get(AuthenticationEffects);
        tokenService = TestBed.get(TokenService);
    });
    
    describe('removeToken$', () => {
        it('should remove the token from the local storage', () => {
            tokenService.saveToken('some-token');
            actions = new ReplaySubject(1);
            actions.next(new AuthenticationActions.Logout());
            effects.loadToken$.subscribe(_ => {
                const actual = tokenService.readToken();
                expect(actual).toBeNull();
            });
        })
    });
});
