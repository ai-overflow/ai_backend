
export interface AuthTokenType {
    accessToken: string | null,
    tokenExpiration: Date,
    tokenType: string | null
}

const defaultAuthToken: AuthTokenType | null = {
    accessToken: '',
    tokenExpiration: new Date(),
    tokenType: ''
};


const authToken: AuthTokenType | null = JSON.parse(localStorage.getItem('authToken') ?? "null");
const currentDate = new Date();

const initialAuthToken = function () {
    if (!authToken || !authToken.tokenExpiration) {
        return null;
    }

    authToken.tokenExpiration = new Date(authToken.tokenExpiration);

    if (authToken.tokenExpiration < currentDate) {
        return null;
    }
    return authToken;
}();

export default function () {
    return {
        status: {
            authToken: initialAuthToken ? initialAuthToken : defaultAuthToken,
            loggedIn: !!initialAuthToken
        }
    }
};