<template>
  <v-card class="ma-4 pa-4">
    <h2>Seiten</h2>
    <v-container>
      <v-btn color="primary" to="/createPage" small>
        <v-icon dark left> mdi-book-plus-multiple </v-icon>
        Hinzufügen
      </v-btn>
    </v-container>
    <v-container>
      <span v-if="items.length === 0">Keine Seite vorhanden</span>
      <v-list two-line>
        <v-list-item-group
          v-model="selected"
          active-class="indigo--text"
          multiple
        >
          <template v-for="(item, index) in items">
            <v-list-item :key="item.title + index" :to="'/page/' + item.id">
              <template v-slot:default="{ active }">
                <v-list-item-content>
                  <v-list-item-title v-text="item.title"></v-list-item-title>

                  <v-list-item-subtitle class="text--primary"
                    >TODO</v-list-item-subtitle
                  >

                  <v-list-item-subtitle
                    v-text="item.description"
                  ></v-list-item-subtitle>
                </v-list-item-content>

                <v-list-item-action @click="deleteProject(item.id)">
                  <v-list-item-action-text>
                    <v-icon color="red">mdi-trash-can</v-icon>
                  </v-list-item-action-text>

                  <v-icon v-if="!active" color="grey lighten-1">
                    mdi-send-outline
                  </v-icon>

                  <v-icon v-else color="indigo darken-3"> mdi-send </v-icon>
                </v-list-item-action>
              </template>
            </v-list-item>

            <v-divider v-if="index < items.length - 1" :key="index"></v-divider>
          </template>
        </v-list-item-group>
      </v-list>
    </v-container>
  </v-card>
</template>

<script>
import PageService from "@/service/PageService";

export default {
  created() {
    PageService.getAllPages().then((e) => {
      this.items = e.data;
      console.log(e.data);
    });
  },
  data() {
    return {
      selected: [2],
      items: [],
    };
  },
  methods: {
    deleteProject(id) {
      PageService.deletePage(id);
    },
  },
};
</script>

<style>
</style>