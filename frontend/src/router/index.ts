import Vue from 'vue'
import VueRouter, { NavigationGuardNext, Route, RouteConfig } from 'vue-router'
import Home from '../views/Home.vue'
import Frontpage from '../views/Frontpage.vue'
import store from '../store/index';

Vue.use(VueRouter)

function checkAuthentification<V extends Vue>(to: Route, from: Route, next: NavigationGuardNext<V>) {
  console.log(store.getters);
  if (store.getters['auth/isLoggedIn']) {
    next()
  } else {
    next({
      name: "login",
      query: {
        redirectTo: to.fullPath
      }
    });
  }
}

function redirectIfAuthenticated<V extends Vue>(to: Route, from: Route, next: NavigationGuardNext<V>) {
  store.getters['auth/isLoggedIn'] ?
      next({name: "home"}) : next();
}

export const routes: Array<RouteConfig> = [
  {
    path: "/",
    name: "Home",
    component: Home,
    meta: {
      icon: "mdi-view-dashboard",
      showInNav: true
    },
    beforeEnter: checkAuthentification
  },
  {
    path: '/projects',
    name: 'Projects',
    component: () => import(/* webpackChunkName: "about" */ '../views/Projects.vue'),
    meta: {
      icon: "mdi-book-open-page-variant",
      showInNav: true
    }
  },
  {
    path: '/Login',
    name: 'login',
    // route level code-splitting
    // this generates a separate chunk (login.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: Frontpage,
    //beforeEnter: redirectIfAuthenticated,
    meta: {
      showInNav: false
    }
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
