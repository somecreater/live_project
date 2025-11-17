export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';


export const API_END_POINT={
  user:{
    login: `${API_BASE_URL}/api/user/login`,
    logout: `${API_BASE_URL}/api/user/login`,
    register: `${API_BASE_URL}/api/user/register`,
    info: `${API_BASE_URL}/api/user/info`,
    update: `${API_BASE_URL}/api/user/update`,
    reset_password: `${API_BASE_URL}/api/user/reset_password`,
    delete: `${API_BASE_URL}/api/user/delete`
  }
}

export const createHeaders = () => ({
  'Content-Type': 'application/json',
});