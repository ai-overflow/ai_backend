<template>
  <div>
    <LoadingOverlay v-if="pageLoading" />
    <div v-if="!page.loadSuccess && !pageLoading">
      There was an error loading this page...
      <p>{{ page.errorMessage }}</p>
    </div>
    <div v-if="page.loadSuccess && !pageLoading">
      <h2>{{ page.title }}</h2>
      <p>
        {{ page.description }}
      </p>
      <div v-for="[name, item] of Object.entries(topLevelInputs)" :key="name">
        <h4>{{ parseParams(item.label) || name }}</h4>
        <InputGenerator :type-info="item" v-model="inputData[name]" />
      </div>
      <div>
        <v-btn @click="() => submitAll()" :loading="loading">Senden</v-btn>
      </div>
    </div>
    <div
      v-if="serverReply && Object.keys(serverReply).length > 0"
      class="mt-10"
    >
      <v-tabs v-model="tab" align-with-title>
        <v-tab
          v-for="index in this.page.projects.filter((e) => !!serverReply[e.id])"
          :key="index.id"
        >
          {{ index.yaml.name }}
        </v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item
          v-for="index in this.page.projects.filter((e) => !!serverReply[e.id])"
          :key="index.id"
        >
          <v-row>
            <v-col v-if="page.layout === 'DOUBLE'" class="half-image">
              <v-img :src="previewImage" />
            </v-col>
            <v-col>
              <div class="pa-5">
                <div
                  v-for="[inputName, inputItem] of Object.entries(
                    index.yaml.input
                  ).filter((e) => !Object.keys(topLevelInputs).includes(e[0]))"
                  :key="inputName"
                >
                  <h4>{{ inputItem.label || inputName }}</h4>
                  <InputGenerator
                    :type-info="inputItem"
                    v-model="projectInputData[index.id][inputName]"
                  />
                </div>
                <v-btn @click="() => submit(index)"
                  ><v-icon left>mdi-refresh</v-icon>Aktualisieren</v-btn
                >
              </div>
              <div class="public-tab-item">
                <div
                  v-for="[name, item] of Object.entries(index.yaml.output)"
                  :key="name"
                >
                  <h4>{{ parseParams(item.label) || name }}</h4>
                  <div
                    v-if="item.repeat && item.repeat.iterator"
                    class="limited-height-container"
                  >
                    <div
                      v-for="[i, el] of [
                        ...projectParsers[index.id].parseIterator(
                          item.repeat.iterator
                        ),
                      ].entries()"
                      :key="i"
                    >
                      <OutputGenerator
                        :output="item"
                        :inputVars="inputData"
                        :outputVars="serverReply[index.id]"
                        :iterator="el"
                        :title="
                          projectParsers[index.id].parseIterator(
                            item.repeat.title
                          )[i]
                        "
                        :customParser="projectParsers[index.id]"
                      />
                    </div>
                  </div>
                  <div v-else class="limited-height-container">
                    <OutputGenerator
                      :output="item"
                      :inputVars="inputData"
                      :outputVars="serverReply[index.id]"
                      :customParser="projectParsers[index.id]"
                    />
                  </div>
                </div>
              </div>
            </v-col>
          </v-row>
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
import { defaultParamGenerator, toBase64 } from "@shared/helper/utility";

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
        this.page.layout = e.data.pageLayout;
        this.page.loadSuccess = true;
        this.page.errorMessage = "";

        for (let key of Object.values(this.page.projects)) {
          this.projectInputData[key.id] = {};
        }
        console.log(this.projectInputData);
      })
      .catch((e) => {
        this.page.loadSuccess = false;
        this.page.errorMessage = e.response.data?.message;
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
      projectInputData: {},
      serverReply: {},
      loading: false,
      projectParsers: {},
      previewImage: "",
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
    submitAll() {
      this.loading = true;
      let elements = Object.keys(this.page.projects).length;
      for (const [ObjKey, value] of Object.entries(this.page.projects)) {
        this.submit(value).finally(() => {
          if (--elements <= 0) this.loading = false;
        });
      }
    },
    submit(value) {
      if (!this.projectParsers[value.id]) {
        this.$set(this.projectParsers, value.id, new ParamParser());
      }

      let defaultParams = defaultParamGenerator(value.yaml);
      this.inputData = { ...this.inputData, ...defaultParams, ...this.projectInputData[value.id] };
      paramParser.input = this.inputData;
      this.projectParsers[value.id].input = this.inputData;

      return proxyRequest(
        "/api/v1/public/proxy/",
        value.yaml.connection[value.yaml.entryPoint],
        value.id
      ).then((e) => {
        let content = generateDataFromResponse(e);
        let el = {};
        el[value.yaml.entryPoint] = {
          success: true,
          value: content,
          contentType: e.headers["content-type"],
        };

        let copy = new ParamParser();
        copy.connection = el;
        copy.input = this.projectParsers[value.id].input;
        this.$set(this.projectParsers, value.id, copy);
        this.$set(this.serverReply, value.id, el);
      });
    },
    parseIterator(str) {
      return paramParser.parseIterator(str);
    },
    parseParams(str) {
      return paramParser.parseParams(str);
    },
  },
  watch: {
    inputData: function () {
      if (!this.page.layout || this.page.layout !== "DOUBLE") return;

      const objFile =
        this.inputData[
          Object.keys(this.inputData).filter(
            (e) =>
              this.topLevelInputs[e] && this.topLevelInputs[e].type === "image"
          )
        ];
      toBase64(objFile).then((e) => {
        this.previewImage = e;
      });
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
.half-image {
  max-width: 50%;
}
</style>