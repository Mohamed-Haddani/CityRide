import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// En dev, tout appel vers /api est transmis au backend Spring Boot (port 8080).
// Cela evite les problemes de CORS pendant le developpement.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8081'
    }
  }
})
