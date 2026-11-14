import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  base: './', // Essencial para abrir o index.html direto via file:// ou qualquer servidor estático
  build: {
    outDir: '../dist',
    emptyOutDir: true
  }
})
