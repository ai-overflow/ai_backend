<template>
  <v-card class="ma-4 pa-4">
    <h2>{{ $route.params.id ? page.title : "Neue Seite erstellen" }}</h2>
    <v-container v-if="$route.params.id">
      <v-text-field
        :value="'<iframe src=\'' + iframeSrc + '\' />'"
        label="HTML"
        readonly
      />
      <v-expansion-panels v-model="panel" :disabled="disabled" multiple>
        <v-expansion-panel>
          <v-expansion-panel-header>Example</v-expansion-panel-header>
          <v-expansion-panel-content>
            <iframe :src="iframeSrc" title="example" class="example-iframe" />
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-container>
    <v-container> <v-divider></v-divider> </v-container>
    <v-container>
      <form>
        <v-row>
          <v-col>
            <v-text-field
              label="Page Title"
              placeholder="Title"
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
            <h3>Select Top-Level Inputs:</h3>
            <v-chip
              v-for="label of getAvailableTopLevelTypes"
              :key="label"
              class="mr-2"
              dark
              :color="topLevelInputChips[label]"
              @click="selectTopLevel(label)"
            >
              {{ label }}
            </v-chip>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-checkbox
              v-model="forceManualSelect"
              label="Force Manual Select"
            ></v-checkbox>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <div v-for="project in page.projects" :key="project.id">
              <h4>{{ project.yaml.name }}</h4>
              <v-checkbox
                dense
                v-for="[inputName, value] of Object.entries(project.yaml.input)"
                :key="inputName"
                :disabled="
                  !forceManualSelect ||
                  !getAvailableTopLevelTypes.includes(value.type)
                "
                v-model="page.topLevelInput[project.id]"
                :value="inputName"
              >
                <!---->
                <template v-slot:label>
                  <strong>[{{ inputName }}] {{ value.label }} </strong>
                  <v-spacer></v-spacer>
                  <span
                    :style="{
                      color: !getAvailableTopLevelTypes.includes(value.type)
                        ? 'red'
                        : '',
                    }"
                  >
                    {{ value.type }}
                  </span>
                </template>
              </v-checkbox>
            </div>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-btn color="primary" @click="submitPage">Submit</v-btn>
          </v-col>
        </v-row>
      </form>
    </v-container>
  </v-card>
</template>

<script>
import ProjectService from "@/service/ProjectService";
import PageService from "@/service/PageService";
import dayjs from "dayjs";
import { intersection } from "@/utility/utils";

export default {
  created() {
    if (this.$route.params.id) {
      this.loadProjects()
        .then(() => PageService.getPage(this.$route.params.id))
        .then((e) => {
          this.page.title = e.data.title;
          this.page.description = e.data.description;
          this.page.active = e.data.active;
          this.page.projects = e.data.selectedProjects;
          this.page.topLevelInput = e.data.topLevelInput;
        });
    }
  },
  data() {
    return {
      descriptionLimit: 60,
      page: {
        description: "",
        title: "",
        active: true,
        projects: [],
        topLevelInput: [],
      },
      topLevelInputChips: [],
      projectsLoading: false,
      searchProject: null,
      projectEntries: [],
      forceManualSelect: false,
    };
  },
  methods: {
    submitPage() {
      let pageObj = {
        title: this.page.title,
        description: this.page.description,
        active: this.page.active,
        topLevelInput: Object.assign({}, this.page.topLevelInput),
        selectedProjects: this.page.projects.map((e) => e.id),
      };

      if (this.$route.params.id) {
        PageService.updatePage(this.$route.params.id, pageObj).then(() => {
          console.log("TODO: Show done message");
          this.$router.push("/pages");
        });
      } else {
        PageService.addPage(pageObj).then(() => {
          console.log("TODO: show done message");
          this.$router.push("/pages");
        });
      }
    },
    selectTopLevel(inputType) {
      let valueChangeTo = undefined;
      for (const el of Object.values(this.projectEntries)) {
        for (const [name, input] of Object.entries(el.yaml.input)) {
          if (input.type === inputType) {
            let index = this.page.topLevelInput[el.id].indexOf(name);
            if (
              index === -1 &&
              (valueChangeTo === undefined || valueChangeTo === true)
            ) {
              this.page.topLevelInput[el.id].push(name);
              valueChangeTo = true;
            } else if (
              index > -1 &&
              (valueChangeTo === undefined || valueChangeTo === false)
            ) {
              this.page.topLevelInput[el.id].splice(index, 1);
              valueChangeTo = false;
            }
          }
        }
      }
      this.topLevelInputChips[inputType] = valueChangeTo
        ? "primary"
        : "secondary";
    },
    loadProjects() {
      this.projectsLoading = true;
      // Lazily load input items
      return ProjectService.getAllProjects()
        .then((response) => {
          this.projectEntries = response.data;

          for (const dbObj of Object.values(response.data)) {
            this.$set(this.page.topLevelInput, dbObj.id, []);
          }
        })
        .finally(() => (this.projectsLoading = false));
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
    getAvailableTopLevelTypes() {
      return intersection(
        ...this.page.projects.map((e) =>
          Object.values(e.yaml.input).map((i) => i.type)
        )
      );
    },
    iframeSrc() {
      const getUrl = window.location;
      const baseUrl = getUrl.protocol + "//" + getUrl.host;

      return baseUrl + "/public/page/" + this.$route.params.id;
    },
  },
  watch: {
    searchProject() {
      // Items have already been loaded
      if (this.page.projects.length > 0) return;

      if (this.projectsLoading) return;

      this.loadProjects();
    },
  },
};
</script>

<style>
.example-iframe {
  border: none;
  overflow: hidden;
  min-width: 500px;
  min-height: 600px;
}
</style>