<template>
  <v-app class="main">
    <v-app-bar color="primary" dark app v-if="showFrame">
      <v-toolbar-title> AI Administration </v-toolbar-title>
      <v-spacer />
      <v-icon small @click="logoutAction"> mdi-logout </v-icon>
    </v-app-bar>
    <navigation-bar v-if="showFrame" />
    <v-main>
      <v-container>
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import Vue from "vue";
import NavigationBar from "./components/NavigationBar.vue";
import { mapActions } from "vuex";

export default Vue.extend({
  name: "App",
  components: {
    NavigationBar,
  },
  data: () => ({
    //
  }),
  computed: {
    showFrame() {
      //prevent flickering if route isn't fully loaded yet
      if(!this.$route.name) return false;
      return (this.$route.meta.showFrame !== undefined ? !!this.$route.meta.showFrame : true);
    },
  },
  methods: {
    ...mapActions({
      logout: "auth/logout",
    }),
    logoutAction() {
      this.logout();
      this.$router.push({ name: "login" });
    }
  },
});
</script>
<style>
html {
  overflow: auto;
}
</style>
