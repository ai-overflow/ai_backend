<template>
  <v-card class="ma-4 pa-4">
    <h2>Seiten</h2>
    <v-container>
      <v-btn color="primary" to="/page/create" small>
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
          disabled
        >
          <template v-for="(item, index) in items">
            <v-list-item :key="item.title + index">
              <template>
                <v-list-item-content>
                  <v-list-item-title v-text="item.title"></v-list-item-title>

                  <v-list-item-subtitle class="text--primary"
                    >{{convertDate(item.creationDate)}}</v-list-item-subtitle
                  >

                  <v-list-item-subtitle
                    v-text="item.description"
                  ></v-list-item-subtitle>
                </v-list-item-content>

                <v-list-item-action>
                  <v-list-item-action-text @click="deleteProject(item.id)">
                    <v-icon color="red">mdi-trash-can</v-icon>
                  </v-list-item-action-text>

                  <v-icon color="indigo darken-3" @click="$router.push('/page/' + item.id)">
                    mdi-lead-pencil
                  </v-icon>
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
import dayjs from "dayjs";

export default {
  created() {
    PageService.getAllPages().then((e) => {
      this.items = e.data;
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
    convertDate(date) {
      return dayjs(date).format("DD.MM.YYYY");
    },
  }
};
</script>

<style>
</style>