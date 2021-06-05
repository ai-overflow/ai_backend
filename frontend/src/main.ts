import Vue from 'vue';
import Vuex from 'vuex';
import App from './App.vue';
import router from './router';
import vuetify from './plugins/vuetify';
import store from './store';
import axios from 'axios';
import AuthService from './service/AuthService';

Vue.config.productionTip = false

axios.defaults.headers.common['Accept'] = 'application/json';
axios.defaults.headers.common['Content-Type'] = 'application/json; charset=UTF-8';
axios.defaults.withCredentials = true;
axios.defaults.timeout = 10000;
axios.defaults.baseURL = process.env.VUE_APP_ROOT_API;
axios.interceptors.request.use(cfg => {
    cfg.headers.Authorization = AuthService.authHeader();
    return cfg;
});

new Vue({
  router,
  vuetify,
  store,
  render: h => h(App)
}).$mount('#app')
