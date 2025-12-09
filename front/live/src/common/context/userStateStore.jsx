import { create } from "zustand";
import ApiService from "../api/ApiService";

export const userStateStore = create((set) => ({
  user: null,
  channel: null,
  isAuthenticated: false,
  defaultProfileImageUrl: '/image/default_user_profile_image.png',
  profileImageUrl: null,

  setUser: (user) => set({ user, isAuthenticated: true }),
  clearUser: () => set({ user: null, isAuthenticated: false }),

  getUserInfo: async () => {
    const response = await ApiService.user.info();
    const data = response.data;

    if (data.result) {
      set({ user: data.user_info, isAuthenticated: true });
      localStorage.setItem("loginId", data.user_info.loginId);
    } else {
      set({ user: null, isAuthenticated: false });
    }
  },
  getUserChannel: async () => {
    const response = await ApiService.channel.my_channel();
    const data = response.data;

    if (data.result) {
      set({ channel: data.my_channel });
      localStorage.setItem("channelName", data.my_channel.name);
    } else {
      set({ channel: null});
    }
  },
  getUserProfile: async (userId) => {
    try {
      const orgUrl = localStorage.getItem("profileImageUrl");
      if (orgUrl) {
        set({ profileImageUrl: orgUrl });
        return;
      } else {
        const response = await ApiService.profile_image.read_image(userId);
        const data = response.data;
        const imageUrl = data.image_url;
        set({ profileImageUrl: imageUrl });
        localStorage.setItem("profileImageUrl", imageUrl);
      }
    } catch (error) {
      set({ profileImageUrl: null });
      console.error(error);
      localStorage.removeItem("profileImageUrl");
    }
  },
  getUserProfileCache: () => {
    const orgUrl = localStorage.getItem("profileImageUrl");
    if (orgUrl) {
      set({ profileImageUrl: orgUrl });
      return;
    }
  },
  clearProfileImage: () => {
    set({ profileImageUrl: null });
  }
}));