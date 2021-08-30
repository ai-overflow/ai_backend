<template>
  <div>
    <LoadingOverlay v-if="pageLoading" />
    <div v-if="!page.loadSuccess && !pageLoading">
      Die aufgerufene Seite kann nicht geladen werden. Bitte versuchen Sie es
      später erneut.
      <p>{{ page.errorMessage }}</p>
    </div>
    <v-alert v-if="replyInfo" border="left" type="info" dark text class="ma-10">
      {{ replyInfo }}
    </v-alert>
    <div v-if="page.loadSuccess && !pageLoading">
      <h2
        v-if="
          !this.$route.query.showTitle ||
          this.$route.query.showTitle.toLowerCase() !== 'false'
        "
      >
        {{ page.title }}
      </h2>
      <p
        v-if="
          !this.$route.query.showDescription ||
          this.$route.query.showDescription.toLowerCase() !== 'false'
        "
      >
        {{ page.description }}
      </p>
      <div v-for="[name, item] of Object.entries(topLevelInputs)" :key="name">
        <h4>{{ parseParams(item.label) || name }}</h4>
        <InputGenerator
          :type-info="item"
          v-model="inputData[name]"
          @change="TopLevelInputListener"
        />
      </div>
      <div>
        <v-btn
          @click="() => submitAll()"
          :loading="loading"
          v-if="Object.keys(this.topLevelInputs).length > 1"
          >Senden</v-btn
        >
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
          :eager="true"
        >
          <v-row>
            <v-col>
              <v-expansion-panels>
                <v-expansion-panel>
                  <v-expansion-panel-header>
                    Projektbeschreibung
                  </v-expansion-panel-header>
                  <v-expansion-panel-content>
                    <VueMarkdown :source="index.yaml.description" />
                  </v-expansion-panel-content>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-col>
          </v-row>
          <v-row>
            <v-col
              v-if="page.layout === 'DOUBLE'"
              class="half-image"
              xs="12"
              sm="12"
              md="6"
            >
              <!-- TODO: Make this a div -->
              <div
                class="preview-image"
                :ref="`showCaseImage_${index.id.replaceAll('-', '_')}`"
                :style="{
                  backgroundImage: 'url(' + previewImage + ')',
                  backgroundSize: 'cover',
                }"
              ></div>
              <!--<img :src="previewImage" alt="" class="preview-image" :ref="`showCaseImage_${index.id.replaceAll('-', '_')}`" />-->
            </v-col>
            <v-col xs="12" sm="12" md="6">
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
              <div
                :class="{
                  publicTabItem: true,
                  embeddedTabItem: $route.query.embedded === 'true',
                  pageTabItem: $route.query.embedded === 'true',
                }"
              >
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
                      <div v-if="Array.isArray(el)">
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
                  </div>
                  <div
                    v-else
                    :class="{
                      'limited-height-container':
                        !$route.query.embedded === 'true',
                    }"
                  >
                    <OutputGenerator
                      :output="item"
                      :inputVars="inputData"
                      :outputVars="serverReply[index.id]"
                      :customParser="projectParsers[index.id]"
                      :canvas="
                        $refs['showCaseImage_' + index.id.replaceAll('-', '_')]
                      "
                    />
                  </div>
                </div>
              </div>
            </v-col>
          </v-row>
        </v-tab-item>
      </v-tabs-items>
    </div>
    <div
      v-if="
        loading &&
        Object.keys(serverReply).length === 0 &&
        Object.keys(this.topLevelInputs).length <= 1
      "
    >
      <v-skeleton-loader
        class="mx-auto"
        type="image, article"
      ></v-skeleton-loader>
    </div>
  </div>
</template>

<script>
import PageService from "@/service/PublicPageService";
import LoadingOverlay from "@/components/LoadingOverlay";
import InputGenerator from "@shared/components/input/InputGenerator";
import OutputGenerator from "@shared/components/output/OutputGenerator";
import VueMarkdown from "vue-markdown-render";
import {
  proxyRequest,
  generateDataFromResponse,
} from "@shared/helper/connection";
import { ParamParser, paramParser } from "@shared/helper/paramParser";
import {
  defaultParamGenerator,
  toBase64,
  scaleToSize,
  readSize,
  resizeFile,
} from "@shared/helper/utility";

import SockJS from "sockjs-client";
import * as Stomp from "stompjs";

export default {
  created() {
    let sock = new SockJS(process.env.VUE_APP_ROOT_API + "/ws/stomp");
    this.wsClient = Stomp.over(sock);
    this.wsClient.debug = false;

    // Web Socket set up
    this.wsClient.connect({}, (frame) => {
      // todo subscribe again
      this.wsClient.subscribe("/topic/upload", (payload) => {
        const message = JSON.parse(payload.body);
        if(message.success) {
          console.log("got response from server...")
          this.handleUploadAfterCache(message.id);
        } else {
          console.log("Fatal error during cache upload...");
        }
        payload.ack();
      });
    });
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
        errorMessage: undefined,
      },
      replyInfo: undefined,
      inputData: [],
      projectInputData: {},
      serverReply: {},
      loading: false,
      projectParsers: {},
      previewImage: "",
      topLevelInputMap: {},
      wsClient: undefined,
    };
  },
  components: {
    LoadingOverlay,
    InputGenerator,
    OutputGenerator,
    VueMarkdown,
  },
  computed: {
    getNamesForTopLEvelInput() {
      let obj = {};
      for(const [key, value] of Object.entries(this.topLevelInputMap)) {
        obj[key] = {};
        for(const inner of value) {
          obj[key][inner.id] = inner.name;
        }
      }
      return obj;
    },
    topLevelInputs() {
      let el = [];

      for (const [ObjKey, value] of Object.entries(this.page.topLevelInput)) {
        let inputs = this.page.projects
          .filter((e) => e.id === ObjKey)
          .map((e) => e.yaml.input)[0];
        if (!inputs) continue;

        const filtered = Object.keys(inputs)
          .filter((key) => value.includes(key))
          .reduce((obj, key) => {
            obj[key] = inputs[key];
            return obj;
          }, {});
        el.push({ v: filtered, id: ObjKey });
      }

      el.forEach((v) => {
        let counter = 0;
        Object.keys(el[0].v).forEach((upperKeys) => {
          if (!this.topLevelInputMap[upperKeys]) {
            this.$set(this.topLevelInputMap, upperKeys, []);
          }
          this.topLevelInputMap[upperKeys].push({
            id: v.id,
            name: Object.keys(v.v)[counter],
          });
          counter++;
        });
      });
      return el[0].v;
    },
  },
  methods: {
    submitAll() {
      // web socket request
      this.generateWebSocketData(Object.values(this.page.projects)).then(
        (data) => {
          this.wsClient.send("/app/upload", {}, JSON.stringify(data))
        }
      );
    },
    handleUploadAfterCache(cacheId) {
      this.replyInfo = undefined;
      this.serverReply = {};
      this.loading = true;
      let elements = Object.keys(this.page.projects).length;

      // classic http request
      for (const [ObjKey, value] of Object.entries(this.page.projects)) {
        this.submit(value, cacheId).finally(() => {
          if (--elements <= 0) {
            this.loading = false;
            if (Object.keys(this.serverReply).length < 1) {
              this.replyInfo =
                "No Model returned a valid result for this image. Please try another image.";
            }
          }
        });
      }
    },
    async generateWebSocketData(values) {
      let newInputData =
        await this.generateInputDataFromTopLevelWithoutDuplicates(values);
      return { data: newInputData };
    },
    submit(value, cacheId) {
      this.generateParserIfNeeded(value);
      let defaultParams = defaultParamGenerator(value.yaml);

      // overwrite default params with user input
      let newInputData = this.generateInputDataFromTopLevel(value);
      this.inputData = {
        ...this.inputData,
        ...defaultParams,
        ...this.projectInputData[value.id],
        ...newInputData,
      };


      var shallowInputCopy = Object.assign({}, this.inputData);
      for(const topLevelValue of Object.values(this.getNamesForTopLEvelInput)) {
        if(shallowInputCopy[topLevelValue[value.id]] instanceof File) {
          delete shallowInputCopy[topLevelValue[value.id]];
        }
      }

      paramParser.input = shallowInputCopy;
      this.projectParsers[value.id].input = shallowInputCopy;

      return proxyRequest(
        "/api/v1/public/proxy/",
        value.yaml.connection[value.yaml.entryPoint],
        value.id,
        cacheId
      ).then((e) => {
        let content = generateDataFromResponse(e);
        let el = {};
        el[value.yaml.entryPoint] = {
          success: true,
          value: content,
          contentType: e.headers["content-type"],
        };

        let copy = new ParamParser(
          process.env.VUE_APP_DEBUG?.toLowerCase() === "true"
        );
        copy.connection = el;
        copy.input = this.projectParsers[value.id].input;
        this.$set(this.projectParsers, value.id, copy);
        this.$set(this.serverReply, value.id, el);
      });
    },
    generateParserIfNeeded(value) {
      if (!this.projectParsers[value.id]) {
        this.$set(
          this.projectParsers,
          value.id,
          new ParamParser(process.env.VUE_APP_DEBUG?.toLowerCase() === "true")
        );
      }
    },
    generateInputDataFromTopLevel(value) {
      let newInputData = {};
      for (let [k, v] of Object.entries(this.inputData)) {
        for (let [nk, nv] of Object.entries(this.topLevelInputMap)) {
          if (k === nk) {
            let newName = nv
              .filter((e) => e.id === value.id)
              .map((e) => e.name)[0];
            newInputData[newName] = this.inputData[k];
          }
        }
      }
      return newInputData;
    },
    async generateInputDataFromTopLevelWithoutDuplicates(values) {
      if (!Array.isArray(values)) return {};

      let newInputData = {};
      for (let [nk, nv] of Object.entries(this.topLevelInputMap)) {
        newInputData[nk] = {};
        newInputData[nk].aliasList = [];
        for (let [k, v] of Object.entries(this.inputData)) {
          if (k === nk) {
            if (!!this.inputData[k] && this.inputData[k] instanceof File) {
              newInputData[nk].base64Data = await resizeFile(
                this.inputData[k],
                500
              );

              for (let value of values) {
                let newName = nv
                  .filter((e) => e.id === value.id)
                  .map((e) => e.name)[0];
                newInputData[k].aliasList.push({ id: value.id, name: newName });
              }
            }
          }
        }
      }
      //todo: wait for promise to resolve
      return newInputData;
    },
    parseIterator(str) {
      return paramParser.parseIterator(str);
    },
    parseParams(str) {
      return paramParser.parseParams(str);
    },
    TopLevelInputListener(e) {
      this.$nextTick(() => {
        if (Object.keys(this.topLevelInputs).length === 1) {
          this.submitAll();
        }
      });
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
      if (objFile) {
        toBase64(objFile)
          .then((e) => {
            this.previewImage = e;
            return e;
          })
          .then((e) => readSize(e))
          .then((e) => {
            const size = scaleToSize(e, 400);
          })
          .catch((e) => {
            console.log(e);
          });
      }
    },
  },
};
</script>

<style>
.limited-height-container {
  max-height: 500px;
}
.public-tab-item {
}
.page-tab-item {
  overflow-y: auto;
  overflow-x: hidden;
}
.embedded-tab-item {
  overflow: hidden;
}
.half-image {
  max-width: 50%;
}
.preview-image {
  max-width: 100%;
  position: sticky;
  top: 0;
}
</style>