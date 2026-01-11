import { create } from 'zustand';
import ApiService from '../api/ApiService';

export const imageCacheStore = create((set, get) => ({
    cache: new Map(), // userId -> imageUrl
    pendingRequests: new Map(), // userId -> Promise<url>

    // 이미지 URL 가져오기 (캐시 확인 -> API 호출)
    getImageUrl: async (userId) => {
        if (!userId) return null;

        const { cache, pendingRequests } = get();

        if (cache.has(userId)) {
            return cache.get(userId);
        }

        if (pendingRequests.has(userId)) {
            return pendingRequests.get(userId);
        }

        const promise = (async () => {
            try {
                // console.log(`[ImageCache] Miss for ${userId}, fetching...`);
                const response = await ApiService.profile_image.read_image(userId);
                if (response.data && response.data.image_url) {
                    const url = response.data.image_url;

                    // 캐시 업데이트 및 pending 제거
                    set((state) => {
                        const newCache = new Map(state.cache);
                        newCache.set(userId, url);

                        const newPending = new Map(state.pendingRequests);
                        newPending.delete(userId);

                        return { cache: newCache, pendingRequests: newPending };
                    });
                    return url;
                }
            } catch (error) {
                // 실패 시 pending 제거
                set((state) => {
                    const newPending = new Map(state.pendingRequests);
                    newPending.delete(userId);
                    return { pendingRequests: newPending };
                });
            }
            return null;
        })();

        set((state) => {
            const newPending = new Map(state.pendingRequests);
            newPending.set(userId, promise);
            return { pendingRequests: newPending };
        });

        return promise;
    },

    // 캐시 수동 설정 (예: 프로필 변경 시)
    setImageUrl: (userId, url) => {
        set((state) => {
            const newCache = new Map(state.cache);
            newCache.set(userId, url);
            return { cache: newCache };
        });
    },

    clearCache: () => set({ cache: new Map(), pendingRequests: new Map() })
}));
