import RefreshApi from "./RefreshApi";
import { API_END_POINT } from "./Api";

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
    })
  }

}

export default ApiService;