/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        display: ['"Playfair Display"', 'serif'],
        body: ['"DM Sans"', 'sans-serif'],
      },
      colors: {
        ink: {
          DEFAULT: '#1a1a2e',
          light: '#16213e',
          muted: '#0f3460',
        },
        gold: {
          DEFAULT: '#e2b04a',
          light: '#f5d98e',
          dark: '#b8860b',
        },
        cream: {
          DEFAULT: '#faf6f0',
          dark: '#f0e8d8',
        }
      }
    }
  },
  plugins: [],
}
