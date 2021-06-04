import stateObj from './state';
type stateType = ReturnType<typeof stateObj>;

export const isLoggedIn = (state: stateType) => state.status.loggedIn;
export const getAuthToken = (state: stateType) => state.status.authToken;