export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';


export const API_END_POINT = {
  user: {
    login: `${API_BASE_URL}/api/user/login`,
    kakaoLogin: `${API_BASE_URL}/oauth2/authorization/kakao`,
    googleLogin: `${API_BASE_URL}/oauth2/authorization/google`,
    logout: `${API_BASE_URL}/api/user/logout`,
    register: `${API_BASE_URL}/api/user/register`,
    info: `${API_BASE_URL}/api/user/info`,
    update: `${API_BASE_URL}/api/user/update`,
    reset_password: `${API_BASE_URL}/api/user/reset_password`,
    delete: `${API_BASE_URL}/api/user/delete`
  },
  mail: {
    sendVerification: `${API_BASE_URL}/public/mail/send_verification`,
    checkVerification: `${API_BASE_URL}/public/mail/verification`,
    sendMailLoginId: `${API_BASE_URL}/public/mail/SearchId`,
    getLoginId: `${API_BASE_URL}/public/mail/GetId`,
    sendMailPass: `${API_BASE_URL}/public/mail/SearchPassword`,
    getPass: `${API_BASE_URL}/public/mail/GetPassword`
  },
  user_profile_image: {
    upload: `${API_BASE_URL}/api/profile_image/upload`,
    delete: `${API_BASE_URL}/api/profile_image/delete`,
    get_info: `${API_BASE_URL}/api/profile_image/get_info`,
    download: `${API_BASE_URL}/api/profile_image/download`,
    read_image: `${API_BASE_URL}/api/profile_image/get_image`
  },
  channel: {
    create: `${API_BASE_URL}/api/channel/create`,
    update: `${API_BASE_URL}/api/channel/update`,
    delete: `${API_BASE_URL}/api/channel/delete`,
    info: `${API_BASE_URL}/api/channel/info`,
    search: `${API_BASE_URL}/api/channel/search`
  }
}


export const createHeaders = () => ({
  'Content-Type': 'application/json',
});