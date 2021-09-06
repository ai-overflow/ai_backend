<template>
  <v-card class="ma-4 pa-4">
    <h2>
      {{ $route.params.id !== "create" ? page.title : "Neue Seite erstellen" }}
    </h2>
    <v-container v-if="$route.params.id !== 'create'">
      <v-row class="pb-0">
        <v-col class="pb-0"
          ><v-checkbox v-model="iframe.showTitle" label="Show Title"
        /></v-col>
        <v-col class="pb-0"
          ><v-checkbox
            v-model="iframe.showDescription"
            label="Show Description"
        /></v-col>
      </v-row>
      <v-textarea
        :value="
          '<iframe\n' +
          '\tsrc=\'' +
          iframeSrc +
          '\'\n' +
          '\tstyle=\'width: 100%; height: 1000px; border: none\'\n' +
          '\tclass=\'project-iframe\'\n' +
          '></iframe>'
        "
        label="HTML"
        readonly
        @focus="$event.target.select()"
      />
      <v-expansion-panels multiple>
        <v-expansion-panel>
          <v-expansion-panel-header>Example</v-expansion-panel-header>
          <v-expansion-panel-content>
            <iframe :src="iframeSrc" title="example" class="example-iframe" style="width: 600px" />
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
            <v-select
              :items="pageTypes"
              outlined
              label="Page Layout"
              v-model="page.layout"
            ></v-select>
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
              item-text="description"
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
    if (this.$route.params.id !== "create") {
      this.loadProjects()
        .then(() => PageService.getPage(this.$route.params.id))
        .then((e) => {
          this.page.title = e.data.title;
          this.page.description = e.data.description;
          this.page.active = e.data.active;
          this.page.projects.push(
            ...e.data.selectedProjects
              .filter((p) => !!p)
              .map((p) => this.createSelectableProject(p))
          );
          this.page.layout = e.data.pageLayout;
          this.page.topLevelInput = Object.fromEntries(
            Object.entries(e.data.topLevelInput).filter((t) =>
              this.page.projects.map((e) => e.id).includes(t[0])
            )
          );
        });
      console.log(this.page.projects);
    }

    PageService.getAllPageTypes().then((e) => {
      this.pageTypes = e.data;
    });
  },
  data() {
    return {
      descriptionLimit: 60,
      iframe: {
        showTitle: true,
        showDescription: true,
      },
      page: {
        description: "",
        title: "",
        active: true,
        projects: [],
        topLevelInput: [],
        layout: "",
      },
      topLevelInputChips: [],
      projectsLoading: false,
      searchProject: null,
      projectEntries: [],
      forceManualSelect: false,
      pageTypes: [],
    };
  },
  methods: {
    submitPage() {
      let pageObj = {
        title: this.page.title,
        description: this.page.description,
        active: this.page.active,
        pageLayout: this.page.layout,
        topLevelInput: Object.assign({}, this.page.topLevelInput),
        selectedProjects: this.page.projects.map((e) => e.id),
      };

      if (this.$route.params.id !== "create") {
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
            if (!this.page.topLevelInput[el.id]) {
              this.$set(this.page.topLevelInput, el.id, []);
            }

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
    generateDescription(e) {
      return (
        (e.yaml.name.length > this.descriptionLimit
          ? e.yaml.name.slice(0, this.descriptionLimit) + "..."
          : e.yaml.name) +
        " (" +
        dayjs(e.creationDate).format("DD.MM.YYYY, HH:mm") +
        ")"
      );
    },
    createSelectableProject(e) {
      const description = this.generateDescription(e);
      return Object.assign({}, e, { description });
    },
  },
  computed: {
    activeProjects() {
      return this.projectEntries.map((e) => {
        return this.createSelectableProject(e);
      });
    },
    getAvailableTopLevelTypes() {
      return intersection(
        ...this.page.projects
          .filter((e) => !!e && !!e.yaml)
          .map((e) => Object.values(e.yaml.input).map((i) => i.type))
      );
    },
    iframeSrc() {
      const getUrl = window.location;
      const baseUrl = getUrl.protocol + "//" + getUrl.host;

      let subPath = "";
      if (process.env.BASE_URL !== undefined) {
        subPath = process.env.BASE_URL.startsWith("/")
          ? process.env.BASE_URL
          : "/" + process.env.BASE_URL;
        if (subPath.endsWith("/"))
          subPath = subPath.substr(0, subPath.length - 1);
      }

      return (
        baseUrl +
        subPath +
        "/public/page/" +
        this.$route.params.id +
        "?showTitle=" +
        this.iframe.showTitle +
        "&showDescription=" +
        this.iframe.showDescription +
        "&embedded=true"
      );
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