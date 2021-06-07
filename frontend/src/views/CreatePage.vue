<template>
  <v-card class="ma-4 pa-4">
    <h2>Neue Seite erstellen</h2>
    <form>
      <v-row>
        <v-col>
          <v-text-field
            label="Page Title"
            placeholder="Placeholder"
            v-model="page.title"
            outlined
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-textarea
            outlined
            name="input-7-4"
            label="Description"
            v-model="page.description"
          ></v-textarea>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-checkbox v-model="page.active" label="Page Active"></v-checkbox>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-autocomplete
            v-model="page.projects"
            :items="activeProjects"
            :loading="projectsLoading"
            :search-input.sync="searchProject"
            hide-no-data
            hide-selected
            item-text="Description"
            item-value="API"
            label="Projects"
            placeholder="Start typing to search Projects"
            prepend-icon="mdi-database-search"
            return-object
            multiple
            chips
          ></v-autocomplete>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-btn color="primary" @click="submitPage">Submit</v-btn>
        </v-col>
      </v-row>
    </form>
  </v-card>
</template>

<script>
import ProjectService from "@/service/ProjectService";
import dayjs from "dayjs";

export default {
  data() {
    return {
      descriptionLimit: 60,
      page: {
        description: "",
        title: "",
        active: true,
        projects: [],
      },
      projectsLoading: false,
      searchProject: null,
      projectEntries: [],
    };
  },
  methods: {
    submitPage() {
      console.log("submit!");
    },
  },
  computed: {
    activeProjects() {
      return this.projectEntries.map((e) => {
        const Description =
          (e.yaml.name.length > this.descriptionLimit
            ? e.yaml.name.slice(0, this.descriptionLimit) + "..."
            : e.yaml.name) +
          " (" +
          dayjs(e.creationDate).format("DD.MM.YYYY, HH:mm") +
          ")";

        return Object.assign({}, e, { Description });
      });
    },
  },
  watch: {
    searchProject(value) {
      // Items have already been loaded
      if (this.page.projects.length > 0) return;

      if (this.projectsLoading) return;

      this.projectsLoading = true;

      // Lazily load input items
      ProjectService.getAllProjects()
        .then((response) => {
          console.log(response.data);
          this.projectEntries = response.data;
        })
        .finally(() => (this.projectsLoading = false));
    },
  },
};
</script>

<style>
</style>