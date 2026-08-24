import { createTheme } from '@mui/material/styles';

export const getTheme = (mode: string) =>
  createTheme({
    palette: {
      mode: mode as 'light' | 'dark',
      primary: {
        main: '#0D47A1',
        light: '#1565C0',
        dark: '#0A3D91',
        contrastText: '#fff',
      },
      secondary: {
        main: '#42A5F5',
        light: '#64B5F6',
        dark: '#1E88E5',
      },
      background: {
        default: mode === 'light' ? '#F0F4F8' : '#0A1929',
        paper: mode === 'light' ? '#FFFFFF' : '#132F4C',
      },
    },
    typography: {
      fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
      h4: { fontWeight: 700 },
      h5: { fontWeight: 700 },
      h6: { fontWeight: 600 },
    },
    shape: {
      borderRadius: 12,
    },
    components: {
      MuiCard: {
        styleOverrides: {
          root: {
            boxShadow: '0 2px 16px rgba(13,71,161,0.08)',
            borderRadius: 16,
          },
        },
      },
      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: 'none',
            fontWeight: 600,
            borderRadius: 10,
            padding: '10px 24px',
          },
        },
      },
      MuiTextField: {
        styleOverrides: {
          root: {
            '& .MuiOutlinedInput-root': {
              borderRadius: 10,
            },
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            borderRadius: 16,
          },
        },
      },
    },
  });
