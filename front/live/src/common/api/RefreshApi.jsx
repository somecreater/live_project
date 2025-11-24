import axios from "axios";
import { API_BASE_URL } from "./Api";

const RefreshApi = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

function handleApiError(error){
  const status = error.response?.status;
  const data = error.response?.data || {};
  const message = data.message || "알 수 없는 오류가 발생했습니다.";
  
  localStorage.setItem("lastError", JSON.stringify(data));

  console.log(data);

  //if (status >= 400) window.location.href='/error/'+status;
  if (status === 401) {
    window.location.href = "/user/login"; 
  }
  return Promise.reject(new Error(message));
}

//토큰 재발급은 서버 로직에서 해결
RefreshApi.interceptors.response.use(
  (response) => response,
  (error) => {
    return handleApiError(error);
  }
);

export default RefreshApi;