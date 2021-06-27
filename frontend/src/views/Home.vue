<template>
  <v-card class="ma-4 pa-4">
    <v-autocomplete
      v-model="selectedProject"
      :items="projects"
      item-text="yaml.name"
      outlined
      chips
      small-chips
      label="Select project to inspect"
      return-object
    ></v-autocomplete>
    <div v-if="statData">
      <h3>Project Executions</h3>
      <apexchart
        width="500"
        type="bar"
        :options="options"
        :series="series"
      ></apexchart>
    </div>
  </v-card>
</template>

<script lang="ts">
import Vue from "vue";
import StatisticService from "@/service/StatisticService";
import ProjectService from "@/service/ProjectService";
import apexchart from "vue-apexcharts";

export default Vue.extend({
  name: "Home",

  components: {
    apexchart,
  },

  data() {
    return {
      projects: [],
      selectedProject: [],
      statData: undefined,
      datacollection: null,
      options: {
        chart: {
          id: "vuechart-example",
        },
        xaxis: {
          categories: [],
        },
      },
      series: [
        {
          name: "Executions",
          data: [],
        },
      ],
    };
  },
  created() {
    ProjectService.getAllProjects().then((response) => {
      this.projects = response.data;
    });
  },
  methods: {},
  watch: {
    selectedProject: function () {
      console.log(this.selectedProject);
      StatisticService.getProjectStats(this.selectedProject.id).then((e) => {
        let count = {};
        e.data.entries
          .map((e) => {
            let date = new Date(e.timestamp);
            console.log(date);
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
        this.options = {
          ...this.options,
          xaxis: { categories: Object.keys(this.statData) },
        };
        this.series = [{ name: this.series[0].name, data: Object.values(this.statData) }];
      });
    },
  },
});
</script>
