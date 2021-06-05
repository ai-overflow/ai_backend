<template>
  <div>
    <form>
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
      <v-btn color="primary" @click="login">Login</v-btn>
    </form>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { mapActions } from "vuex";

export default Vue.extend({
  name: "Home",

  components: {},
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    ...mapActions({
      loginAction: "auth/login",
    }),
    login() {
      console.log(this.$store);
      let loginRequest = {
        username: this.username,
        password: this.password,
      };

      this.loginAction(loginRequest).then(() => {
        if (this.$route.query.redirectTo) {
          this.$router.push(this.$route.query.redirectTo);
        } else {
          this.$router.push("/Home").catch((err) => {
            this.$log.error(err.message);
          });
        }
      })
      .catch((err) => {
          console.log(err);
      });
    },
  },
});
</script>
