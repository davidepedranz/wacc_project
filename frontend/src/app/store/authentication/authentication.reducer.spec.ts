import { reducer, initialState, State } from './authentication.reducer';
import * as AuthenticationActions from './authentication.actions';

describe('Authentication Store', () => {
    describe('reducer', () => {
        it('should have a meaningful initial state', () => {
            const expectedState: State = {
                redirectPageAfterLogin: '/',
                loginPending: false,
                loginError: false,
                token: null
            };
            expect(initialState).toEqual(expectedState);
        });
        it('should return the initial state if invoked with "undefined"', () => {
            const actualState = reducer(undefined, {} as AuthenticationActions.All);
            expect(actualState).toEqual(initialState);
        });
        it('should change the state on a login request', () => {
            const expectedState: State = {
                ...initialState,
                loginError: false,
                loginPending: true
            };
            const actualState = reducer(initialState, new AuthenticationActions.Login({ username: 'a', password: 'b' }));
            expect(actualState).toEqual(expectedState);
        });
        it('should change the state on a login success event', () => {
            const expectedState: State = {
                ...initialState,
                loginError: false,
                loginPending: false,
                token: 'a'
            };
            const actualState = reducer(initialState, new AuthenticationActions.LoginSuccess('a'));
            expect(actualState).toEqual(expectedState);
        });
        it('should change the state on a login failure event', () => {
            const expectedState: State = {
                ...initialState,
                loginError: true,
                loginPending: false
            };
            const actualState = reducer(initialState, new AuthenticationActions.LoginFailure());
            expect(actualState).toEqual(expectedState);
        });
        it('should change the state on a logout request', () => {
            const actualState = reducer(initialState, new AuthenticationActions.Logout());
            expect(actualState).toEqual(initialState);
        });
        it('should change the state on a login redirect request', () => {
            const expectedState: State = {
                ...initialState,
                redirectPageAfterLogin: '/page/x'
            };
            const actualState = reducer(initialState, new AuthenticationActions.LoginRedirect('/page/x'));
            expect(actualState).toEqual(expectedState);
        });
    });
});
