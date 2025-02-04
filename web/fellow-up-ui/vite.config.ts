import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  server: {
    proxy: {
      // backend
      '/api': {
        target: 'http://localhost:8080/api',
        changeOrigin: true,
      }
    }
  },
  plugins: [react()],
})
