import Vue from 'vue'
import VueRouter, { NavigationGuardNext, Route, RouteConfig } from 'vue-router'
import Home from '../views/Home.vue'
import Frontpage from '../views/Frontpage.vue'
import store from '../store/index';

Vue.use(VueRouter)

function checkAuthentification<V extends Vue>(to: Route, from: Route, next: NavigationGuardNext<V>) {
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
    path: '/pages',
    name: 'Pages',
    component: () => import(/* webpackChunkName: "about" */ '../views/Pages.vue'),
    beforeEnter: checkAuthentification,
    meta: {
      icon: "mdi-book-open-page-variant",
      showInNav: true
    }
  },
  {
    path: '/createPage',
    name: 'Create Page',
    component: () => import(/* webpackChunkName: "about" */ '../views/CreatePage.vue'),
    beforeEnter: checkAuthentification,
    meta: {
      showInNav: false
    }
  },
  {
    path: '/Login',
    name: 'login',
    component: Frontpage,
    beforeEnter: redirectIfAuthenticated,
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
