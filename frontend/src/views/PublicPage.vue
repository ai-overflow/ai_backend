<template>
  <div>
    <LoadingOverlay v-if="pageLoading" />
    <div v-if="!page.loadSuccess && !pageLoading">
      There was an error loading this page...
    </div>
    <div v-if="page.loadSuccess && !pageLoading">
      <h2>{{ page.title }}</h2>
      <p>
        {{ page.description }}
      </p>
      <div v-for="[name, item] of Object.entries(topLevelInputs)" :key="name">
        <h4>{{ item.label || name }}</h4>
        <InputGenerator :type-info="item" v-model="inputData[name]" />
      </div>
      <div>
        <v-btn @click="() => submit(page.id)" :loading="loading">Senden</v-btn>
      </div>
    </div>
    <div v-if="serverReply && Object.keys(serverReply).length > 0">
      <v-tabs v-model="tab" align-with-title>
        <v-tab v-for="index in this.page.projects" :key="index.id">
          {{ index.yaml.name }}
        </v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item
          v-for="index in this.page.projects"
          :key="index.id"
          class="public-tab-item"
        >
          <div
            v-for="[name, item] of Object.entries(index.yaml.output)"
            :key="name"
            class="mt-10"
          >
            <OutputGenerator
              :output="item"
              :inputVars="inputData"
              :outputVars="serverReply[index.id]"
              :customParser="projectParsers[index.id]"
            />
          </div>
        </v-tab-item>
      </v-tabs-items>
    </div>
  </div>
</template>

<script>
import PageService from "@/service/PublicPageService";
import LoadingOverlay from "@/components/LoadingOverlay";
import InputGenerator from "@shared/components/input/InputGenerator";
import OutputGenerator from "@shared/components/output/OutputGenerator";
import {
  proxyRequest,
  generateDataFromResponse,
} from "@shared/helper/connection";
import { ParamParser, paramParser } from "@shared/helper/paramParser";
import { defaultParamGenerator } from "@shared/helper/utility";

export default {
  created() {
    paramParser.input = this.inputData;

    this.pageLoading = true;
    PageService.getPage(this.$route.params.id)
      .then((e) => {
        this.page.title = e.data.title;
        this.page.description = e.data.description;
        this.page.active = e.data.active;
        this.page.projects = e.data.selectedProjects;
        this.page.topLevelInput = e.data.topLevelInput;
        this.page.loadSuccess = true;
      })
      .catch(() => {
        this.page.loadSuccess = false;
      })
      .finally(() => {
        this.pageLoading = false;
      });
  },
  data() {
    return {
      tab: null,
      pageLoading: false,
      page: {
        loadSuccess: true,
        title: "",
        description: "",
      },
      inputData: [],
      serverReply: {},
      loading: false,
      projectParsers: {},
    };
  },
  components: {
    LoadingOverlay,
    InputGenerator,
    OutputGenerator,
  },
  computed: {
    topLevelInputs() {
      let el = [];

      for (const [ObjKey, value] of Object.entries(this.page.topLevelInput)) {
        const inputs = this.page.projects
          .filter((e) => e.id === ObjKey)
          .map((e) => e.yaml.input)[0];

        const filtered = Object.keys(inputs)
          .filter((key) => value.includes(key))
          .reduce((obj, key) => {
            obj[key] = inputs[key];
            return obj;
          }, {});
        el.push(filtered);
      }
      return el[0];
    },
  },
  methods: {
    submit() {
      this.loading = true;
      let elements = Object.keys(this.page.projects).length;
      for (const [ObjKey, value] of Object.entries(this.page.projects)) {
        if (!this.projectParsers[value.id]) {
          this.$set(this.projectParsers, value.id, new ParamParser());
        }

        let defaultParams = defaultParamGenerator(value.yaml);
        this.inputData = { ...this.inputData, ...defaultParams };
        paramParser.input = this.inputData;
        this.projectParsers[value.id].input = this.inputData;

        proxyRequest(
          "/api/v1/public/proxy/",
          value.yaml.connection[value.yaml.entryPoint],
          value.id
        )
          .then((e) => {
            let content = generateDataFromResponse(e);
            let el = {};
            el[value.yaml.entryPoint] = {
              success: true,
              value: content,
              contentType: e.headers["content-type"],
            };

            //this.projectParsers[value.id].connection = el;
            let copy = new ParamParser();
            copy.connection = el;
            copy.input = this.projectParsers[value.id].input;
            this.$set(this.projectParsers, value.id, copy);
            this.$set(this.serverReply, value.id, el);
          })
          .finally(() => {
            if (--elements <= 0) this.loading = false;
          });
      }
    },
  },
};
</script>

<style>
.public-tab-item {
  overflow-y: auto;
  overflow-x: hidden;
  max-height: 500px;
}
</style>