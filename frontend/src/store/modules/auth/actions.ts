import { Store } from "vuex";
import AuthService from "@/service/AuthService";
import { AuthTokenType } from "./state";

export const login = ({ commit, getters }, loginRequest: {username: string, password: string}) => {
    console.log("called")
    return AuthService.login(loginRequest).then(
        (jwtResponse: AuthTokenType) => {

            commit('loginSuccess', jwtResponse);
            return Promise.resolve(jwtResponse);
        },
        (error: any) => {
            commit('loginFailure');
            return Promise.reject(error);
        }
    );
};

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