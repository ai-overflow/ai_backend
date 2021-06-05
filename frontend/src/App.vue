<template>
  <v-app>
    <v-app-bar color="primary" dark app v-if="!showFrontPage">
      <v-toolbar-title>AI Administration</v-toolbar-title>
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
});
</script>
