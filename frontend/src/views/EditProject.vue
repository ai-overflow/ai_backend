<template>
  <v-card class="ma-4 pa-4">
    <div v-if="!!projectInfo">
      <h2>{{ projectInfo.yaml.name }}</h2>
      <v-container>
        <form>
          <v-row>
            <v-col class="pb-0">
              <v-text-field
                label="ID"
                v-model="projectInfo.id"
                outlined
                disabled
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col class="pb-0 pt-0">
              <v-text-field
                label="Name"
                v-model="projectInfo.yaml.name"
                outlined
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col class="pb-0 pt-0">
              <v-text-field
                label="Created"
                :value="convertDate(projectInfo.creationDate)"
                outlined
                disabled
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col class="pb-0 pt-0">
              <v-text-field
                label="GitUrl"
                placeholder="http://github.com/User/Project.git"
                v-model="projectInfo.gitUrl"
                outlined
                class="append-helper"
              >
                <template v-slot:append-outer class="ma-0">
                  <div class="vertical-center">
                    <v-tooltip bottom>
                      <template v-slot:activator="{ on, attrs }">
                        <v-btn
                          x-large
                          dark
                          color="primary"
                          v-bind="attrs"
                          v-on="on"
                          @click="reloadProject"
                          :loading="reloading"
                          ><v-icon>mdi-refresh</v-icon></v-btn
                        >
                      </template>
                      <span>Reload Project from Git URL</span>
                    </v-tooltip>
                  </div>
                </template>
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col class="pb-0 pt-0">
              <v-textarea
                outlined
                label="Description"
                v-model="projectInfo.yaml.description"
              ></v-textarea>
            </v-col>
          </v-row>
          <v-row>
            <v-col class="pb-0 pt-0">
              <v-text-field
                label="Path"
                v-model="projectInfo.projectPath"
                outlined
                disabled
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-btn color="primary" @click="submitPage">Submit</v-btn>
            </v-col>
          </v-row>
        </form>
        <div class="ma-10"></div>
        <h3>Service Information</h3>
        <v-list two-line subheader>
          <v-subheader>Identifying Ports</v-subheader>
          <v-list-item
            v-for="[port, info] in Object.entries(projectInfo.accessInfo)"
            :key="port"
          >
            <v-list-item-content>
              <v-list-item-title>{{ port }}</v-list-item-title>
              <v-list-item-subtitle>
                http://{{ info.hostname }}:{{ info.port }}
              </v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>
        <div class="ma-4"></div>
        <v-list two-line subheader>
          <v-subheader>Available Models</v-subheader>
          <v-list-item-group v-model="activeModels" multiple>
            <v-list-item v-for="model in projectInfo.activeModels" :key="model">
              <template v-slot:default="{ active }">
                <v-list-item-action>
                  <v-checkbox
                    :input-value="active"
                    color="primary"
                  ></v-checkbox>
                </v-list-item-action>

                <v-list-item-content>
                  <v-list-item-title>{{ model }}</v-list-item-title>
                  <v-list-item-subtitle>
                    Status: {{ isActiveModel(model) ? "online" : "offline" }}
                  </v-list-item-subtitle>
                </v-list-item-content>
              </template>
            </v-list-item>
          </v-list-item-group>
        </v-list>
      </v-container>
    </div>
    <div v-else class="text-center">
      <v-progress-circular
        :size="70"
        :width="7"
        color="primary"
        indeterminate
      ></v-progress-circular>
    </div>
  </v-card>
</template>

<script>
import ProjectService from "@/service/ProjectService";
import InferenceService from "@/service/InferenceService";
import dayjs from "dayjs";

export default {
  data() {
    return {
      projectInfo: undefined,
      loading: true,
      activeModels: [],
      reloading: false,
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    loadData() {
      this.loading = true;
      ProjectService.getProject(this.$route.params.id)
        .then((e) => {
          this.projectInfo = e.data;
          this.loading = false;

          return InferenceService.getStatus();
        })
        .then((e) => {
          //this.projectInfo.activeModels.findIndex(e => e.data)
          let activeData = e.data
            .map((d) =>
              this.projectInfo.activeModels.findIndex(
                (a) => d.name === a && d.state === "READY"
              )
            )
            .filter((e) => e !== -1);
          this.activeModels = activeData;
        });
    },
    convertDate(date) {
      return dayjs(date).format("DD.MM.YYYY, HH:mm");
    },
    updateModelStatus(name) {
      console.log(name);
    },
    submitPage() {
      return false;
    },
    isActiveModel(model) {
      return this.activeModels
        .map((e) => this.projectInfo.activeModels[e])
        .includes(model);
    },
    reloadProject() {
      this.reloading = true;
      ProjectService.reloadProject(this.projectInfo.id, this.projectInfo.gitUrl)
        .then((e) => {
          this.loadData();
        })
        .finally((e) => (this.reloading = false));
    },
  },
  watch: {
    activeModels: function (newValue, oldValue) {
      let addDifference = newValue.filter((x) => !oldValue.includes(x));
      let removeDifference = oldValue.filter((x) => !newValue.includes(x));
      if (addDifference.length > 0) {
        let diffValue = addDifference.map(
          (e) => this.projectInfo.activeModels[e]
        );
        InferenceService.activateModel(diffValue[0]);
      } else if (removeDifference.length > 0) {
        let diffValue = removeDifference.map(
          (e) => this.projectInfo.activeModels[e]
        );
        InferenceService.deactivateModel(diffValue[0]);
      }
    },
  },
};
</script>

<style>
.append-helper .v-input__append-outer {
  margin: 0 !important;
  justify-content: center;
  align-items: center;
}
</style>