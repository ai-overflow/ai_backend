<template>
  <v-app>
    <v-app-bar color="primary" dark app v-if="!showFrontPage">
      <v-toolbar-title> AI Administration </v-toolbar-title>
      <v-spacer />
      <v-icon small @click="logoutAction"> mdi-logout </v-icon>
    </v-app-bar>
    <navigation-bar v-if="!showFrontPage" />
    <v-main>
      <v-container>
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script lang="ts">
import Vue from "vue";
import NavigationBar from "@/components/NavigationBar";
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
    showFrontPage() {
      return ["landing", "login"].includes(this.$route.name);
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
