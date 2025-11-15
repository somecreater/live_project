import axios from "axios";
import { API_BASE_URL } from "./Api";

const RefreshApi = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

//토큰 재발급은 서버 로직에서 해결
RefreshApi.interceptors.response.use(
  (response) => response,
  (error) => {
    const status=error.response?.status;
    const key = 'lastError';
    const payload = error.response?.data || {}
    localStorage.setItem(key, JSON.stringify(payload));

    //if (status >= 400) window.location.href='/error/'+status;
    console.log(payload);
    if(status == 401) window.location.href= 'user/login';
    return Promise.reject(error);
  }

);

export default RefreshApi;