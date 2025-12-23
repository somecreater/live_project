import RefreshApi from "./RefreshApi";
import { API_END_POINT, createHeaders } from "./Api";

const ApiService = {
  user: {
    login: (dto) => RefreshApi.post(API_END_POINT.user.login, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    logout: () => RefreshApi.post(API_END_POINT.user.logout, null, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    register: (dto) => RefreshApi.post(API_END_POINT.user.register, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    info: () => RefreshApi.get(API_END_POINT.user.info, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    update: (dto) => RefreshApi.post(API_END_POINT.user.update, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    reset_password: (dto) => RefreshApi.post(API_END_POINT.user.reset_password, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    delete: (dto) => RefreshApi.post(API_END_POINT.user.delete, dto, {
      headers: createHeaders(),
      withCredentials: true,
    })
  },
  mail: {
    send_verification: (dto) => RefreshApi.post(API_END_POINT.mail.sendVerification, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    check_verification: (dto) => RefreshApi.post(API_END_POINT.mail.checkVerification, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    send_mail_login_id: (dto) => RefreshApi.post(API_END_POINT.mail.sendMailLoginId, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    search_login_id: (dto) => RefreshApi.post(API_END_POINT.mail.getLoginId, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    send_mail_password: (dto) => RefreshApi.post(API_END_POINT.mail.sendMailPass, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    search_password: (dto) => RefreshApi.post(API_END_POINT.mail.getPass, dto, {
      headers: createHeaders(),
      withCredentials: true,
    })
  },
  profile_image: {
    upload: (dto) => RefreshApi.post(API_END_POINT.user_profile_image.upload, dto, {
      withCredentials: true,
    }),
    delete: () => RefreshApi.post(API_END_POINT.user_profile_image.delete, {
      withCredentials: true,
    }),
    get_info: (param) => RefreshApi.get(`${API_END_POINT.user_profile_image.get_info}?user_id=${param}`, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    download: (param) => RefreshApi.get(`${API_END_POINT.user_profile_image.download}?user_id=${param}`, {
      responseType: 'blob',
      withCredentials: true,
    }),
    read_image: (param) => RefreshApi.get(`${API_END_POINT.user_profile_image.read_image}?user_id=${param}`, {
      headers: createHeaders(),
      withCredentials: true,
    })
  },
  channel: {
    create: (dto) => RefreshApi.post(API_END_POINT.channel.create, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    update: (dto) => RefreshApi.post(API_END_POINT.channel.update, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    delete: (dto) => RefreshApi.post(API_END_POINT.channel.delete, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    info: (id) => RefreshApi.get(`${API_END_POINT.channel.info}/${id}`, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    my_channel: () => RefreshApi.get(API_END_POINT.channel.my_channel, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    search: (dto) => RefreshApi.post(API_END_POINT.channel.search, dto, {
      headers: createHeaders(),
      withCredentials: true,
    })
  },
  subscription: {
    user_list: (dto)=> RefreshApi.post(API_END_POINT.subscription.user_list, dto,{
      headers: createHeaders(),
      withCredentials: true,
    }),
    my_subscription: (dto)=> RefreshApi.post(API_END_POINT.subscription.my_subscription, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    channel_list: (dto) => RefreshApi.post(API_END_POINT.subscription.channel_list, dto,{
      headers: createHeaders(),
      withCredentials: true,
    }),
    my_channel: (dto) => RefreshApi.post(API_END_POINT.subscription.my_channel, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    insert: (dto) => RefreshApi.post(API_END_POINT.subscription.insert, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    update: (dto) => RefreshApi.post(API_END_POINT.subscription.update, dto, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    delete: (dto) => RefreshApi.post(API_END_POINT.subscription.delete, dto, {
      headers: createHeaders(),
      withCredentials: true,
    })
  },
  cover: {
    upload: (dto) => RefreshApi.post(API_END_POINT.cover.upload, dto, {
      withCredentials: true,
    }),
    delete: () => RefreshApi.post(API_END_POINT.cover.delete, {
      withCredentials: true,
    }),
    get_info: (param) => RefreshApi.get(`${API_END_POINT.cover.get_info}?channel_name=${param}`, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    download: (param) => RefreshApi.get(`${API_END_POINT.cover.download}?channel_name=${param}`, {
      responseType: 'blob',
      withCredentials: true,
    }),
    read_image: (param) => RefreshApi.get(`${API_END_POINT.cover.read_image}?channel_name=${param}`, {
      headers: createHeaders(),
      withCredentials: true,
    })
  },
  post: {
    read: (id) => RefreshApi.get(`${API_END_POINT.post.read}/${id}`, {
      headers: createHeaders(),
      withCredentials: true,
    }),
    list: (dto) => RefreshApi.post(`${API_END_POINT.post.list}`,dto,{
      headers: createHeaders(),
      withCredentials: true,
    }),
    write: (dto) => RefreshApi.post(`${API_END_POINT.post.write}`,dto,{
      headers: createHeaders(),
      withCredentials: true,
    }),
    update: (dto) => RefreshApi.post(`${API_END_POINT.post.update}`, dto,{
      headers: createHeaders(),
      withCredentials: true,
    }),
    delete: (dto) => RefreshApi.post(`${API_END_POINT.post.delete}`, dto,{
      headers: createHeaders(),
      withCredentials: true,
    })
  }
}

export default ApiService;