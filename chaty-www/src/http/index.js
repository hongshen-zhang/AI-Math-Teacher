import config from "../config";
import axios from "axios";
import { ElMessage } from "element-plus";

const instance = axios.create({
  baseURL: config.apiUrl,
  timeout: 3 * 60000,
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
    ElMessage.error(error.message);
    return Promise.reject(error);
  }
);

export default instance;
