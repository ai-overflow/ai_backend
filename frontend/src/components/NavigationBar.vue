<template>
  <v-navigation-drawer
    :permanent="!$vuetify.breakpoint.xsOnly"
    expand-on-hover
    dark
    app
  >
    <v-list nav dense>
      <v-list-item class="mb-2">
        <v-list-item-icon>
          <v-icon>mdi-atom</v-icon>
        </v-list-item-icon>
        <v-list-item-title>Administration</v-list-item-title>
      </v-list-item>
    </v-list>
    <v-divider></v-divider>
    <v-list nav dense>
      <div v-for="item in routes" :key="item.name">
        <v-list-item link :to="item.path" v-if="item.meta.showInNav">
          <v-list-item-icon>
            <v-icon>{{ item.meta.icon }}</v-icon>
          </v-list-item-icon>
          <v-list-item-title>{{ item.name }}</v-list-item-title>
        </v-list-item>
      </div>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { routes } from "@/router/index";

export default {
  created() {
    console.log("ok", routes)
  },
  data() {
    return {
      routes: routes,
    };
  },
};
</script>

<style>
</style>