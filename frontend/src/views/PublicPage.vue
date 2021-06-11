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
        <v-btn @click="submit">Senden</v-btn>
      </div>
    </div>
  </div>
</template>

<script>
import PageService from "@/service/PublicPageService";
import LoadingOverlay from "@/components/LoadingOverlay";
import InputGenerator from "@shared/components/input/InputGenerator";
import { proxyRequest } from "@shared/helper/connection";
import { paramParser } from "@shared/helper/paramParser";

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
      pageLoading: false,
      page: {
        loadSuccess: true,
        title: "",
        description: "",
      },
      inputData: [],
    };
  },
  components: {
    LoadingOverlay,
    InputGenerator,
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
      console.log(this.inputData);
       for (const [ObjKey, value] of Object.entries(this.page.projects)) {
         console.log(ObjKey, value.yaml.connection[value.yaml.entryPoint]);
         proxyRequest("/api/v1/public/proxy/", value.yaml.connection[value.yaml.entryPoint]).then(e => {
           console.log(e);
        })
       }
      /**/
    },
  },
};
</script>

<style>
</style>