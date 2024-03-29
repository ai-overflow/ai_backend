import axios from 'axios';
import { parseOptionsToAxiosConfig } from './ServiceUtils';
import store from '@/store/index';

class AuthService {

    private static readonly API_PATH = 'api/v1/a/authenticate';

    public login(loginRequest: { username: string, password: string }, options?: any) {
        return axios
            .post(AuthService.API_PATH, loginRequest, parseOptionsToAxiosConfig(options))
            .then(response => {
                //JwtResponse
                response.data.tokenExpiration = new Date(response.data.tokenExpiration);
                return response.data;
            });
    }

    public authHeader() {
        const authToken = store.getters['auth/getAuthToken'];
        if (authToken && authToken.accessToken) {
            return authToken.tokenType + ' ' + authToken.accessToken;
        } else {
            return '';
        }
    }

    public refreshToken(options?: any) {
        return axios
            .get(AuthService.API_PATH + 'refreshToken', parseOptionsToAxiosConfig(options))
            .then(response => {
                //JwtResponse
                response.data.tokenExpiration = new Date(response.data.tokenExpiration);
                return response.data;
            });
    }
}

export default new AuthService();