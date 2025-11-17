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
  }

}

export default ApiService;