export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
export const API_BASE_URL_2 = import.meta.env.VITE_API_BASE_URL_2 || 'http://localhost:8081';
export const API_BASE_URL_3 = import.meta.env.VITE_API_BASE_URL_3 || 'http://localhost:8082';

export const ALERT_BASE_URL = import.meta.env.VITE_ALERT_BASE_URL || 'http://localhost:8083';

export const API_END_POINT = {
  alert: {
    alert_connect: `${API_BASE_URL}/notify`,
    alert_subscribe: `/user/queue/alerts`,
    get_list: `${API_BASE_URL}/api/alert/get_list`,
  },
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
    my_channel: `${API_BASE_URL}/api/channel/my_channel`,
    search: `${API_BASE_URL}/api/channel/search`
  },
  subscription: {
    is_subscribed: `${API_BASE_URL}/api/subscription/is_subscription`,
    user_list: `${API_BASE_URL}/api/subscription/user`,
    my_subscription: `${API_BASE_URL}/api/subscription/my_subscription`,
    channel_list: `${API_BASE_URL}/api/subscription/channel`,
    my_channel: `${API_BASE_URL}/api/subscription/my_channel`,
    insert: `${API_BASE_URL}/api/subscription/insert`,
    update: `${API_BASE_URL}/api/subscription/update`,
    delete: `${API_BASE_URL}/api/subscription/delete`
  },
  cover: {
    upload: `${API_BASE_URL}/api/cover_image/upload`,
    delete: `${API_BASE_URL}/api/cover_image/delete`,
    get_info: `${API_BASE_URL}/api/cover_image/get_info`,
    download: `${API_BASE_URL}/api/cover_image/download`,
    read_image: `${API_BASE_URL}/api/cover_image/get_image`
  },
  post: {
    read: `${API_BASE_URL}/api/post/read`,
    list: `${API_BASE_URL}/api/post/list`,
    write: `${API_BASE_URL}/api/post/write`,
    update: `${API_BASE_URL}/api/post/update`,
    delete: `${API_BASE_URL}/api/post/delete`
  }
};
