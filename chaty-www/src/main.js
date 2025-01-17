import "./assets/main.css";
// croppejs
import 'cropperjs/dist/cropper.css';

import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

import axios from "./http";
import fileserver from "./config/fileserver.js";

// markdown
// 预览插件
import VMdPreview from '@kangc/v-md-editor/lib/preview';
import '@kangc/v-md-editor/lib/style/preview.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';
// Latex 格式
import VueMarkdownEditor from '@kangc/v-md-editor';
import createKatexPlugin from '@kangc/v-md-editor/lib/plugins/katex/cdn';
// 高亮
import hljs from 'highlight.js';

import "@/assets/index.css"
import "@/assets/index1.scss"

import { createPinia } from "pinia";

import cn from '@/locales/cn'
import en from '@/locales/en'
import { createI18n } from 'vue-i18n'
import permissionDirective from '@/directives/permission';

import * as pdfjsLib from 'pdfjs-dist';
pdfjsLib.GlobalWorkerOptions.workerSrc  = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.10.377/pdf.worker.min.js';

const app = createApp(App);

app.use(router);
app.use(ElementPlus);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$fileserver = fileserver;

VMdPreview.use(githubTheme, {
  Hljs: hljs,
});
VMdPreview.use(createKatexPlugin());
app.use(VMdPreview);

const pinia = createPinia();
app.use(pinia)

const i18n = createI18n({
  locale: 'cn',
  messages: {
    en, cn
  }
})
app.use(i18n)

app.directive('permission', permissionDirective);

app.mount("#app");
