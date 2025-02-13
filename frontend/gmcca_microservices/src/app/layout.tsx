import * as React from 'react';
import { Suspense } from 'react';
import { AppRouterCacheProvider } from '@mui/material-nextjs/v14-appRouter';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from '@/theme';
import ToolBar from '@/components/ToolBar';

export default function RootLayout(props: { children: React.ReactNode }) {
  return (
    <Suspense fallback={<div>Cargando...</div>}>
      <html lang="es">
        <head>
          <title>GMCCA Microservices</title>
        </head>
        <body>
          <AppRouterCacheProvider options={{ enableCssLayer: true }}>
            <ThemeProvider theme={theme}>
              {/* CssBaseline kickstart an elegant, consistent, and simple baseline to build upon. */}
              <CssBaseline />
              <ToolBar />
              {props.children}
            </ThemeProvider>
          </AppRouterCacheProvider>
        </body>
      </html>
    </Suspense>
  );
}
