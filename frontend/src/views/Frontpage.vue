<template>
  <div>
    <v-alert border="left" colored-border type="error" elevation="2" v-if="!!loginErrorMessage">
      Login Error: {{loginErrorMessage}}
    </v-alert>
    <v-form @submit.prevent="login">
      <v-text-field
        v-model="username"
        color="blue darken-2"
        label="Username"
        required
      ></v-text-field>
      <v-text-field
        v-model="password"
        color="blue darken-2"
        label="Password"
        type="password"
        autocomplete="on"
        required
      ></v-text-field>
      <v-btn color="primary" @click="login" type="submit">Login</v-btn>
    </v-form>
  </div>
</template>

<script>
import Vue from "vue";
import { mapActions } from "vuex";

export default Vue.extend({
  name: "Home",

  components: {},
  data() {
    return {
      username: "",
      password: "",
      loginErrorMessage: undefined
    };
  },
  methods: {
    ...mapActions({
      loginAction: "auth/login",
    }),
    login() {
      let loginRequest = {
        username: this.username,
        password: this.password,
      };

      this.loginAction(loginRequest)
        .then(() => {
          this.loginErrorMessage = undefined;
          if (this.$route.query.redirectTo) {
            this.$router.push(this.$route.query.redirectTo);
          } else {
            this.$router.push("/Home").catch((err) => {
              console.log(err.message);
            });
          }
        })
        .catch((error) => {
          console.log("ERROR:", error);
          this.loginErrorMessage = error.response.data.message;
        });
    },
  },
});
</script>
