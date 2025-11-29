import { create } from "zustand";
import ApiService from "../api/ApiService";

export const userStateStore = create((set)=>({
  user: null,
  isAuthenticated: false,
  defaultProfileImageUrl: '/image/default_user_profile_image.png',
  profileImageUrl: null,

  setUser: (user) => set({user, isAuthenticated: true}),
  clearUser: () => set({user: null, isAuthenticated: false}),

  getUserInfo: async ()=> {
    const response= await ApiService.user.info();
    const data=response.data;
    
    if(data.result){
      set({ user: data.user_info, isAuthenticated: true });
    } else {
      set({ user: null, isAuthenticated: false });
    }
  },
  getUserProfile: async (userId)=> {
    try{
      const response= await ApiService.profile_image.read_image(userId);
      const data=response.data;
      const imageUrl=data.image_url;
      set({profileImageUrl:imageUrl});
    }catch(error){
      set({profileImageUrl:null});
      console.error(error);
    }
  }
}));