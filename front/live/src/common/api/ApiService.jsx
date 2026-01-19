import RefreshApi from "./RefreshApi";
import { API_END_POINT } from "./Api";

/**
 * API 호출 서비스.
 */
const ApiService = {
  manager_message: {
    get_list: () => RefreshApi.post(API_END_POINT.manager_message.get_list),
    read_message: (id) => RefreshApi.post(`${API_END_POINT.manager_message.read_message}/${id}`),
    read_message_all: () => RefreshApi.post(API_END_POINT.manager_message.read_message_all),
    delete_message: (id) => RefreshApi.post(`${API_END_POINT.manager_message.delete_message}/${id}`),
    delete_message_all: () => RefreshApi.post(API_END_POINT.manager_message.delete_message_all)
  },
  alert: {
    get_list: () => RefreshApi.post(API_END_POINT.alert.get_list),
    get_read: (id) => RefreshApi.post(`${API_END_POINT.alert.read_alert}/${id}`),
    get_read_all: () => RefreshApi.post(API_END_POINT.alert.read_alert_all),
    get_delete: (id) => RefreshApi.post(`${API_END_POINT.alert.delete_alert}/${id}`),
    get_delete_all: () => RefreshApi.post(API_END_POINT.alert.delete_alert_all)
  },
  user: {
    login: (dto) => RefreshApi.post(API_END_POINT.user.login, dto),
    logout: () => RefreshApi.post(API_END_POINT.user.logout),
    register: (dto) => RefreshApi.post(API_END_POINT.user.register, dto),
    info: () => RefreshApi.get(API_END_POINT.user.info),
    update: (dto) => RefreshApi.post(API_END_POINT.user.update, dto),
    reset_password: (dto) => RefreshApi.post(API_END_POINT.user.reset_password, dto),
    delete: (dto) => RefreshApi.post(API_END_POINT.user.delete, dto)
  },

  mail: {
    send_verification: (dto) => RefreshApi.post(API_END_POINT.mail.sendVerification, dto),
    check_verification: (dto) => RefreshApi.post(API_END_POINT.mail.checkVerification, dto),
    send_mail_login_id: (dto) => RefreshApi.post(API_END_POINT.mail.sendMailLoginId, dto),
    search_login_id: (dto) => RefreshApi.post(API_END_POINT.mail.getLoginId, dto),
    send_mail_password: (dto) => RefreshApi.post(API_END_POINT.mail.sendMailPass, dto),
    search_password: (dto) => RefreshApi.post(API_END_POINT.mail.getPass, dto)
  },

  profile_image: {
    upload: (formData) => RefreshApi.post(API_END_POINT.user_profile_image.upload, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
    delete: () => RefreshApi.post(API_END_POINT.user_profile_image.delete),
    get_info: (userId) => RefreshApi.get(`${API_END_POINT.user_profile_image.get_info}?user_id=${userId}`),
    download: (userId) => RefreshApi.get(`${API_END_POINT.user_profile_image.download}?user_id=${userId}`, {
      responseType: 'blob'
    }),
    read_image: (userId) => RefreshApi.get(`${API_END_POINT.user_profile_image.read_image}?user_id=${userId}`)
  },

  channel: {
    create: (dto) => RefreshApi.post(API_END_POINT.channel.create, dto),
    update: (dto) => RefreshApi.post(API_END_POINT.channel.update, dto),
    delete: (dto) => RefreshApi.post(API_END_POINT.channel.delete, dto),
    info: (id) => RefreshApi.get(`${API_END_POINT.channel.info}/${id}`),
    my_channel: () => RefreshApi.get(API_END_POINT.channel.my_channel),
    search: (dto) => RefreshApi.post(API_END_POINT.channel.search, dto)
  },

  subscription: {
    is_subscribed: (channel_name) => RefreshApi.get(`${API_END_POINT.subscription.is_subscribed}?channel_name=${channel_name}`),
    user_list: (dto) => RefreshApi.post(API_END_POINT.subscription.user_list, dto),
    my_subscription: (dto) => RefreshApi.post(API_END_POINT.subscription.my_subscription, dto),
    channel_list: (dto) => RefreshApi.post(API_END_POINT.subscription.channel_list, dto),
    my_channel: (dto) => RefreshApi.post(API_END_POINT.subscription.my_channel, dto),
    insert: (dto) => RefreshApi.post(API_END_POINT.subscription.insert, dto),
    update: (dto) => RefreshApi.post(API_END_POINT.subscription.update, dto),
    delete: (dto) => RefreshApi.post(API_END_POINT.subscription.delete, dto)
  },

  cover: {
    upload: (formData) => RefreshApi.post(API_END_POINT.cover.upload, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
    delete: () => RefreshApi.post(API_END_POINT.cover.delete),
    get_info: (channelName) => RefreshApi.get(`${API_END_POINT.cover.get_info}?channel_name=${channelName}`),
    download: (channelName) => RefreshApi.get(`${API_END_POINT.cover.download}?channel_name=${channelName}`, {
      responseType: 'blob'
    }),
    read_image: (channelName) => RefreshApi.get(`${API_END_POINT.cover.read_image}?channel_name=${channelName}`)
  },

  post: {
    read: (id) => RefreshApi.get(`${API_END_POINT.post.read}/${id}`),
    list: (dto) => RefreshApi.post(API_END_POINT.post.list, dto),
    write: (dto) => RefreshApi.post(API_END_POINT.post.write, dto),
    update: (dto) => RefreshApi.post(API_END_POINT.post.update, dto),
    delete: (dto) => RefreshApi.post(API_END_POINT.post.delete, dto)
  }
};

export default ApiService;