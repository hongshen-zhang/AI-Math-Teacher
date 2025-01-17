import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    port: 20000,
    proxy: {
      // '/api': 'http://127.0.0.1:10002',
      // '/static': 'http://127.0.0.1:10002',
      '/api': 'https://www.saomiaoshijuan.com',
      '/static': 'https://www.saomiaoshijuan.com',
    }
  },
})
