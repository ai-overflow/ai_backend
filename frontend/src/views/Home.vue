<template>
  <v-card class="ma-4 pa-4">
    <v-autocomplete
      v-model="selectedProject"
      :items="projects"
      item-text="yaml.name"
      outlined
      chips
      small-chips
      label="Outlined"
      return-object
    ></v-autocomplete>
    {{ statData }}
    <div v-if="datacollection">
    </div>
  </v-card>
</template>

<script lang="ts">
import Vue from "vue";
import StatisticService from "@/service/StatisticService";
import ProjectService from "@/service/ProjectService";

export default Vue.extend({
  name: "Home",

  components: {
  },

  data() {
    return {
      projects: [],
      selectedProject: [],
      statData: {},
      datacollection: null,
    };
  },
  created() {
    ProjectService.getAllProjects().then((response) => {
      this.projects = response.data;
    });
  },
  methods: {
  },
  watch: {
    selectedProject: function () {
      console.log(this.selectedProject);
      StatisticService.getProjectStats(this.selectedProject.id).then((e) => {
        let count = {};
        e.data.entries
          .map((e) => {
            let date = new Date(e.timestamp);
            return (
              date.getDate() +
              "." +
              (date.getMonth() + 1) +
              "." +
              date.getFullYear()
            );
          })
          .forEach(function (x) {
            count[x] = (count[x] || 0) + 1;
          });
        this.statData = count;
      });
    },
  },
});
</script>
