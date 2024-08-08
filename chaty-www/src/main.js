import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

import axios from "./http";
import fileserver from "./config/fileserver.js";

const app = createApp(App);

app.use(router);
app.use(ElementPlus);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$fileserver = fileserver;

app.mount("#app");
