import axios from "axios";
import { API_BASE_URL } from "./Api";

const RefreshApi = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

RefreshApi.interceptors.response.use(
  (response) => response,
  (error) => {
    const status=error.response?.status;
    const key = 'lastError';
    const payload = error.response?.data || {}
    localStorage.setItem(key, JSON.stringify(payload));

    if (status >= 400) window.location.href='/error/'+status;
    return Promise.reject(error);
  }

);

export default RefreshApi;