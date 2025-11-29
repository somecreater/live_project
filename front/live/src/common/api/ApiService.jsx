import RefreshApi from "./RefreshApi";
import { API_END_POINT,createHeaders } from "./Api";

const ApiService={
  user:{
    login: (dto) => RefreshApi.post(API_END_POINT.user.login, dto, {
      headers: createHeaders(),
      withCredentials: true, 
    }),
    logout: () => RefreshApi.post(API_END_POINT.user.logout,null, {
      headers: createHeaders(),
      withCredentials: true, 
    }),
    register: (dto) => RefreshApi.post(API_END_POINT.user.register, dto, {
      headers: createHeaders(),
      withCredentials: true, 
    }),
    info: () => RefreshApi.get(API_END_POINT.user.info,{
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
  mail:{
    send_verification: (dto) => RefreshApi.post(API_END_POINT.mail.sendVerification, dto, {
      headers: createHeaders(),
      withCredentials: true, 
    }),
    check_verification: (dto) => RefreshApi.post(API_END_POINT.mail.checkVerification, dto, {
      headers: createHeaders(),
      withCredentials: true, 
    }),
    send_mail_login_id: (dto) => RefreshApi.post(API_END_POINT.mail.sendMailLoginId, dto,{
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
    search_password: (dto) => RefreshApi.post(API_END_POINT.mail.getPass, dto,{
      headers: createHeaders(),
      withCredentials: true, 
    })
  },
  profile_image:{
    upload: (dto)=> RefreshApi.post(API_END_POINT.user_profile_image.upload, dto, {
      withCredentials: true, 
    }),
    delete: ()=> RefreshApi.post(API_END_POINT.user_profile_image.delete,{
      withCredentials: true, 
    }),
    get_info:(param)=> RefreshApi.get(`${API_END_POINT.user_profile_image.get_info}?user_id=${param}`,{
      headers: createHeaders(),
      withCredentials: true, 
    }),
    download:(param)=> RefreshApi.get(`${API_END_POINT.user_profile_image.download}?user_id=${param}`,{
      responseType: 'blob',
      withCredentials: true, 
    }),
    read_image:(param)=> RefreshApi.get(`${API_END_POINT.user_profile_image.read_image}?user_id=${param}`,{
      headers: createHeaders(),
      withCredentials: true, 
    })
  }

}

export default ApiService;