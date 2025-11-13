import { create } from "zustand";
import ApiService from "../api/ApiService";

export const userStateStore = create((set)=>({
  user: null,
  isAuthenticated: false,

  setUser: (user) => set({user, isAuthenticated: true}),
  clearUser: () => set({user: null, isAuthenticated: false}),

  getUserInfo: async ()=> {
    const response= await ApiService.user.info();
    const data=response.data;
    
    if(data.result){
      set({user: data.user_info});
    }else{
      set({user: null});
    }
  }
}));