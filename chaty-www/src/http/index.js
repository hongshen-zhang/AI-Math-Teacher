import config from "../config";
import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router";
import { useUserStore } from "../store";
import { debounce } from "../utils/api";

const onAuthFailed = debounce(() => {
  let userStore = useUserStore();
  // userStore.showLogin(true)
  router.push("/login")
  ElMessage.error("未查询到登录信息，请重新登录!");
})

const instance = axios.create({
  baseURL: config.apiUrl,
  timeout: 10 * 60000,
  withCredentials: true,
});

instance.defaults.headers.post["Content-Type"] =
  "application/json;charset=UTF-8";

instance.interceptors.response.use(
  function (response) {
    // 对响应数据做点什么
    if (response.data.code === 200) {
      return response.data;
    } else {
      ElMessage.error(response.data.message);
      return Promise.reject(response.data);
    }
  },
  function (error) {
    if (error.response.status === 401) {
      onAuthFailed();
      return Promise.reject(error)
    }
    
    ElMessage.error(error.message);
    return Promise.reject(error);
  }
);

export default instance;
