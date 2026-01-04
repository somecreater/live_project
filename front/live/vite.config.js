import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  
  // SockJS 호환성을 위한 설정
  define: {
    // global 객체를 window로 폴리필
    global: 'window',
    // process.env도 정의 (필요한 경우)
    'process.env': {}
  },
  
  // 추가 최적화 설정
  optimizeDeps: {
    include: ['sockjs-client']
  }
})
