import stateObj, { AuthTokenType } from './state';
type stateType = ReturnType<typeof stateObj>;

export const loginSuccess = (state: stateType, jwtResponse: AuthTokenType) => {
    const onlyToken = Object.assign({}, jwtResponse);
    //delete onlyToken.user;
    state.status.loggedIn = true;
    state.status.authToken = onlyToken;
    //state.status.user = jwtResponse.user;
    localStorage.setItem('authToken', JSON.stringify(onlyToken));
    //localStorage.setItem('user', JSON.stringify(jwtResponse.user));
};
export const logout = (state: stateType) => {
    state.status.loggedIn = false;
    //state.status.user = null;
    state.status.authToken = null;
    localStorage.removeItem('authToken');
    //localStorage.removeItem('user');
};

export const loginFailure = (state: stateType) => {
    logout(state);
};