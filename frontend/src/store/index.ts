import Vue from 'vue';
import Vuex from 'vuex';
import auth from './modules/auth/index';

Vue.use(Vuex);

const store = new Vuex.Store({
    modules: {
        auth: auth,
    },
    strict: process.env.DEV
});

export default store;