<template>
  <v-card class="ma-4 pa-4">
    <v-data-iterator
      :items="items"
      :items-per-page.sync="itemsPerPage"
      :page.sync="page"
      :search="search"
      hide-default-footer
    >
      <template v-slot:header>
        <v-toolbar dark color="primary" class="mb-1">
          <v-text-field
            v-model="search"
            clearable
            flat
            solo-inverted
            hide-details
            prepend-inner-icon="mdi-magnify"
            label="Search"
          >
            <template v-slot:append>
              <v-btn color="blue lighten-2" to="/createProject">
                <v-icon>mdi-book-plus</v-icon>
              </v-btn>
            </template>
          </v-text-field>
        </v-toolbar>
      </template>

      <template v-slot:default="props">
        <v-row>
          <v-col
            v-for="item in props.items"
            :key="item.yaml.name + item.creationDate"
            cols="12"
            sm="12"
            md="6"
            lg="4"
          >
            <v-card :disabled="!!item.disabled">
              <v-card-title class="subheading font-weight-bold">
                {{ item.yaml.name }}
                <v-spacer></v-spacer>
                <v-btn
                  @click="deleteProject(item.id)"
                  small
                  plain
                  icon
                  color="red"
                >
                  <v-icon>mdi-trash-can</v-icon>
                </v-btn>
              </v-card-title>

              <v-divider></v-divider>

              <v-list dense>
                <v-list-item>
                  <v-list-item-content> Erstellt: </v-list-item-content>
                  <v-list-item-content class="align-end">
                    {{ convertDate(item.creationDate) }}
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-content> Git URL: </v-list-item-content>
                  <v-list-item-content class="align-end">
                    {{ item.gitUrl }}
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-card>
          </v-col>
        </v-row>
      </template>
      <template v-slot:footer>
        <v-row class="mt-2" align="center" justify="center">
          <span class="grey--text">Items per page</span>
          <v-menu offset-y>
            <template v-slot:activator="{ on, attrs }">
              <v-btn
                dark
                text
                color="primary"
                class="ml-2"
                v-bind="attrs"
                v-on="on"
              >
                {{ itemsPerPage }}
                <v-icon>mdi-chevron-down</v-icon>
              </v-btn>
            </template>
            <v-list>
              <v-list-item
                v-for="(number, index) in itemsPerPageArray"
                :key="index"
                @click="updateItemsPerPage(number)"
              >
                <v-list-item-title>{{ number }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>

          <v-spacer></v-spacer>

          <span class="mr-4 grey--text">
            Page {{ page }} of {{ numberOfPages }}
          </span>
          <v-btn-toggle dark v-if="numberOfPages > 1">
            <v-btn color="primary darken-3" @click="formerPage">
              <v-icon>mdi-chevron-left</v-icon>
            </v-btn>
            <v-btn color="primary darken-3" @click="nextPage">
              <v-icon>mdi-chevron-right</v-icon>
            </v-btn>
          </v-btn-toggle>
        </v-row>
      </template>
    </v-data-iterator>
  </v-card>
</template>

<script>
import ProjectService from "@/service/ProjectService";
import dayjs from "dayjs";

export default {
  data() {
    return {
      search: "",
      itemsPerPage: 8,
      itemsPerPageArray: [8, 12, 24],
      page: 1,
      items: [],
    };
  },
  created() {
    ProjectService.getAllProjects().then((response) => {
      this.items = response.data;
    });
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.items.length / this.itemsPerPage);
    },
  },
  methods: {
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1;
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1;
    },
    updateItemsPerPage(number) {
      this.itemsPerPage = number;
    },
    convertDate(date) {
      return dayjs(date).format("DD.MM.YYYY, HH:mm");
    },
    deleteProject(id) {
      let itemIndex = this.items.findIndex((e) => e.id === id);

      let newVal = this.items[itemIndex];
      newVal.disabled = true;

      this.$set(this.items, itemIndex, newVal);
      ProjectService.deleteProject(id)
        .then(() => {
          this.items = this.items.filter((e) => e.id !== id);
        })
        .catch((e) => {
          newVal.disabled = false;
          this.$set(this.items, itemIndex, newVal);
        });
    },
  },
};
</script>

<style>
</style>