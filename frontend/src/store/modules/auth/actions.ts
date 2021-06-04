export const refreshToken = ({ commit }, options) => {
    /*return AuthService.refreshToken(options)
        .then(jwtResponse => {
                commit('loginSuccess', jwtResponse);
                return Promise.resolve(jwtResponse);
            },
            error => {
                commit('loginFailure');
                return Promise.reject(error);
            });*/
};

export const logout = ({ commit }) => {
    commit('logout');
};