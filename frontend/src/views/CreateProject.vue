<template>
  <v-card class="ma-4 pa-4">
    <h2>Add Project from Git URL</h2>
    <v-alert
      v-if="errorMessage"
      border="left"
      type="error"
      icon="mdi-cloud-alert"
      dark
      text
      class="ma-10"
    >
      {{ errorMessage }}
    </v-alert>
    <v-text-field
      v-model="gitUrl"
      flat
      hide-details
      prepend-inner-icon="mdi-book"
      label="Git-URL"
      placeholder="http://github.com/User/Project.git"
    >
      <template v-slot:append-outer>
        <v-btn @click="addGitProject" small :loading="gitDownloadInProgress">
          <v-icon>mdi-book-plus</v-icon>
        </v-btn>
      </template>
    </v-text-field>
    <span v-if="gitData"> Cloned Repository: {{ gitData.yaml.name }} </span>
  </v-card>
</template>

<script>
import ProjectService from "@/service/ProjectService";

export default {
  data() {
    return {
      gitUrl: "",
      gitData: undefined,
      errorMessage: undefined,
      gitDownloadInProgress: false,
    };
  },
  methods: {
    addGitProject() {
      this.gitDownloadInProgress = true;
      this.gitData = undefined;
      this.errorMessage = undefined;

      ProjectService.addProject(this.gitUrl)
        .then((e) => {
          this.gitData = e.data;
        })
        .catch((error) => {
          console.log("catch");
          this.errorMessage = error.response.data.message;
        })
        .finally(() => {
          this.gitDownloadInProgress = false;
        });
    },
  },
};
</script>

<style>
</style>