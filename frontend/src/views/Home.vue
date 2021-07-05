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
      <v-row>
        <v-col>
          <h3>Project Executions</h3>
          <apexchart
            width="500"
            type="bar"
            :options="options"
            :series="series"
          ></apexchart>
        </v-col>
        <v-col>
          <v-card color="primary" dark>
            <v-card-title class="text-h5">
              Average Execution Time
            </v-card-title>

            <v-card-text>
              <v-row align="center">
                <v-col class="text-h2" cols="6">
                  {{ Math.round(executionData.avg * 100) / 100 }}ms
                </v-col>
                <v-col class="text-h2" cols="6">
                  ±{{ Math.round(executionData.std * 100) / 100 }}ms
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-card>
</template>

<script>
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
      executionData: undefined,
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
        let executionTimes = e.data.entries.map((e) => e.executionTimeMs);
        const sum = executionTimes.reduce((a, b) => a + b, 0);
        const avg = sum / executionTimes.length || 0;
        const variance =
          executionTimes.reduce((a, b) => a + Math.pow(b - avg, 2), 0) / sum;
        this.executionData = { avg: avg, std: Math.sqrt(variance) };

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
        this.series = [
          { name: this.series[0].name, data: Object.values(this.statData) },
        ];
      });
    },
  },
});
</script>
