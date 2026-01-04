import axios from "axios";
import { API_BASE_URL } from "./Api";

const RefreshApi = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

function handleApiError(error) {
  const status = error.response?.status;
  const data = error.response?.data || {};
  const message = data.message || "알 수 없는 오류가 발생했습니다.";

  console.error(`[API Error] ${status}:`, data);

  if (status === 401) {
    // 세션 만료 시 로그인 페이지로 이동 (이미 로그인 페이지가 아니면)
    if (!window.location.pathname.includes('/user/login')) {
      window.location.href = "/user/login";
    }
  }

  return Promise.reject(new Error(message));
}

// 요청 인터셉터
RefreshApi.interceptors.request.use(
  (config) => {
    // 필요한 경우 여기서 토큰 등을 동적으로 추가할 수 있음
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터
RefreshApi.interceptors.response.use(
  (response) => response,
  (error) => handleApiError(error)
);

export default RefreshApi;