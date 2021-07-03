<template>
  <v-card class="ma-4 pa-4">
    <v-alert
      border="left"
      type="error"
      icon="mdi-cloud-alert"
      v-if="errorMessage"
      dark
      text
      class="errorContainer"
      >{{ errorMessage }}</v-alert
    >
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
              <v-btn color="blue lighten-2" to="/project/create">
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
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                      @click="deleteProject(item.id)"
                      small
                      plain
                      icon
                      color="red"
                      v-bind="attrs"
                      v-on="on"
                    >
                      <v-icon>mdi-trash-can</v-icon>
                    </v-btn>
                  </template>
                  <span>Delete Project</span>
                </v-tooltip>
                {{ item.yaml.name }}
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                      :to="'/project/' + item.id"
                      small
                      plain
                      icon
                      color="primary"
                      v-bind="attrs"
                      v-on="on"
                    >
                      <v-icon>mdi-pencil</v-icon>
                    </v-btn>
                  </template>
                  <span>Edit Project</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                      @click="stopContainer(item.id)"
                      small
                      plain
                      icon
                      v-if="checkIfRunning(item.projectPath)"
                      color="red"
                      :loading="statusLoading[item.id]"
                      v-bind="attrs"
                      v-on="on"
                    >
                      <v-icon> mdi-pause </v-icon>
                    </v-btn>
                    <v-btn
                      @click="startContainer(item.id)"
                      small
                      plain
                      icon
                      v-else
                      color="green"
                      :loading="statusLoading[item.id]"
                      v-bind="attrs"
                      v-on="on"
                    >
                      <v-icon>mdi-play</v-icon>
                    </v-btn>
                  </template>
                  <span>Start/Stop Container</span>
                </v-tooltip>
              </v-card-title>

              <v-divider></v-divider>

              <v-list dense>
                <v-list-item>
                  <v-list-item-content> Container Status: </v-list-item-content>
                  <v-list-item-content class="align-end">
                    <span
                      v-if="checkIfRunning(item.projectPath)"
                      class="green--text"
                    >
                      Aktiv
                    </span>
                    <span class="red--text" v-else>Inaktiv</span>
                  </v-list-item-content>
                </v-list-item>
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
                <v-list-item>
                  <v-list-item-content> Services: </v-list-item-content>
                  <v-list-item-content class="align-end">
                    <ul>
                      <li
                        v-for="[port, service] in Object.entries(
                          item.accessInfo
                        )"
                        :key="port"
                      >
                        <strong>{{ port }}:</strong> http://{{
                          service.hostname
                        }}:{{ service.port }}
                      </li>
                    </ul>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-content> Container Stats: </v-list-item-content>
                  <v-list-item-content class="align-end">
                    <ul
                      v-if="
                        item.serviceNames &&
                        item.serviceNames.length > 0 &&
                        item.serviceNames[0].serviceName
                      "
                      class="break-word"
                    >
                      <li v-for="el of item.serviceNames" :key="el.serviceName">
                        {{ el.serviceName }}
                        <v-progress-linear
                          color="primary"
                          height="15"
                          :value="el.memory.used_percentage"
                          v-if="el.memory"
                        >
                          <template>
                            <span class="white--text">
                              {{
                                Math.round(
                                  (el.memory.used / Math.pow(1024, 3)) * 100
                                ) / 100
                              }}
                              /
                              {{
                                Math.round(
                                  (el.memory.total / Math.pow(1024, 3)) * 100
                                ) / 100
                              }}GB
                            </span>
                          </template>
                        </v-progress-linear>
                      </li>
                    </ul>
                    <ul
                      v-if="
                        item.serviceNames &&
                        item.serviceNames.length > 0 &&
                        !item.serviceNames[0].serviceName
                      "
                      class="break-word"
                    >
                      <li v-for="el of item.serviceNames" :key="el.serviceName">
                        {{ el }}
                      </li>
                    </ul>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-content> Inference Models: </v-list-item-content>
                  <v-list-item-content class="align-end">
                    <ul>
                      <li v-for="model in item.activeModels" :key="model">
                        {{ model }}
                      </li>
                    </ul>
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
import DockerService from "@/service/DockerService";
import dayjs from "dayjs";

export default {
  data() {
    return {
      search: "",
      itemsPerPage: 8,
      itemsPerPageArray: [8, 12, 24],
      page: 1,
      items: [],
      runningProjects: [],
      statusLoading: [],
      errorMessage: undefined,
    };
  },
  created() {
    ProjectService.getAllProjects()
      .then((response) => {
        this.items = response.data;
      })
      .then(() => DockerService.getContainerStats())
      .then((response) => this.addContainerStats(response.data));
    this.loadContainerStatus();
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.items.length / this.itemsPerPage);
    },
  },
  methods: {
    startContainer(id) {
      this.errorMessage = undefined;
      this.$set(this.statusLoading, id, true);
      DockerService.startContainer(id)
        .then((response) => {
          if (!response.data.success) {
            this.errorMessage = response.data.output;
            console.log(this.errorMessage);
          }
          return this.loadContainerStatus(true);
        })
        .then(() => {
          this.$set(this.statusLoading, id, false);
        })
        .then(() => DockerService.getContainerStats())
        .then((response) => this.addContainerStats(response.data))
        .catch((e) => {
          this.errorMessage =
            e.response?.data?.message ??
            "An unknown error occured while starting a container";
        });
    },
    stopContainer(id) {
      this.errorMessage = undefined;
      this.$set(this.statusLoading, id, true);
      DockerService.stopContainer(id)
        .then((response) => {
          return this.loadContainerStatus();
        })
        .then(() => {
          this.$set(this.statusLoading, id, false);
        })
        .then(() => DockerService.getContainerStats())
        .then((response) => this.addContainerStats(response.data))
        .catch((e) => {
          this.errorMessage = e.response.data?.message;
        });
    },
    loadContainerStatus(appendErrorMessage = false) {
      if (!appendErrorMessage) this.errorMessage = undefined;
      else this.errorMessage = this.errorMessage ?? "";

      return DockerService.getRunningContainers()
        .then((response) => {
          this.runningProjects = response.data;
        })
        .catch((e) => {
          let msg =
            e.response?.data?.message ??
            "An unknown error occured while checking container state";
          if (appendErrorMessage) this.errorMessage += msg;
          else this.errorMessage = msg;
        });
    },
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
    checkIfRunning(pathName) {
      let match = pathName.match(/([^/|\\]*)\/*$/)[1];
      return this.runningProjects.some((e) => e.startsWith(match));
    },
    addContainerStats(response = []) {
      for (const key of Object.keys(this.items)) {
        if (!this.items[key].serviceNames) return;
        const projectName =
          this.items[key].projectPath.match(/([^/|\\]*)\/*$/)[1];
        this.items[key].serviceNames = this.items[key].serviceNames.map((e) => {
          if (e.serviceName) e = e.serviceName;
          const fullServiceName = projectName + "_" + e;
          const matchingService = response.find((s) =>
            s.name.startsWith(fullServiceName)
          );
          if (matchingService) return { serviceName: e, ...matchingService };
          return { serviceName: e };
        });
      }
    },
  },
};
</script>

<style>
.errorContainer {
  white-space: pre;
}
.break-word {
  word-break: break-all;
}
</style>