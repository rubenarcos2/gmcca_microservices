'use client';
import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Button from '@mui/material/Button';
import MenuItem, { MenuItemClassKey, MenuItemProps } from '@mui/material/MenuItem';
import Badge from '@mui/material/Badge';
import NotificationsIcon from '@mui/icons-material/Notifications';
import Logo from '@/components/Logo';
import Tooltip from '@mui/material/Tooltip';
import { usePathname, useRouter } from 'next/navigation';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function ToolBar() {
  const pathname = usePathname();
  const router = useRouter();

  const pages = [
    { name: 'Productos', url: '/' },
    { name: 'Pedidos', url: '/orders' },
  ];

  const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(null);
  const [anchorElNotifications, setAnchorElNotifications] = React.useState<null | HTMLElement>(
    null
  );

  const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElNav(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleOpenNotificationsMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElNotifications(event.currentTarget);
  };

  const handleCloseNotificationsMenu = () => {
    setAnchorElNotifications(null);
  };

  const handleClickNotificationsMenu = (index: number) => {
    console.log(index);
    responses.splice(index, 1);
    setAnchorElNotifications(null);
  };

  //Websocket notifications
  const [message, setMessage] = React.useState('');
  const [responses, setResponses] = React.useState<message[]>([]);

  type message = {
    text: String;
    timestamp: Date;
  };

  React.useEffect(() => {
    // Connect to the WebSocket server
    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      stompClient.subscribe('/topic/messages', msg => {
        const newMessage: message = {
          text: msg.body,
          timestamp: new Date(),
        };

        // Acumular el mensaje recibido en el estado
        setResponses(prevResponses => [...prevResponses, newMessage]);
      });
    });

    return () => {
      stompClient.disconnect();
    };
  }, []);

  return (
    <AppBar position="fixed">
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <Logo />
          <Typography
            variant="h6"
            noWrap
            component="a"
            sx={{
              mr: 2,
              display: { xs: 'none', md: 'flex' },
              fontFamily: 'monospace',
              fontWeight: 700,
              letterSpacing: '.3rem',
              color: 'inherit',
              textDecoration: 'none',
            }}>
            GMCCA
          </Typography>

          <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
              color="inherit">
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'left',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'left',
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{ display: { xs: 'block', md: 'none' } }}>
              {pages.map(page => (
                <MenuItem key={page.name} onClick={handleCloseNavMenu}>
                  <Typography sx={{ textAlign: 'center' }}>{page.name}</Typography>
                </MenuItem>
              ))}
            </Menu>
          </Box>
          <Typography
            variant="h5"
            noWrap
            component="a"
            href="./"
            sx={{
              mr: 2,
              display: { xs: 'flex', md: 'none' },
              flexGrow: 1,
              fontFamily: 'monospace',
              fontWeight: 700,
              letterSpacing: '.3rem',
              color: 'inherit',
              textDecoration: 'none',
            }}>
            GMCCA
          </Typography>
          <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
            {pages.map(page => {
              pathname;
              return page.url == pathname ? (
                <Button
                  variant="contained"
                  disabled
                  key={page.name}
                  onClick={() => {
                    handleCloseNavMenu(), router.push(page.url);
                  }}
                  sx={{
                    my: 2,
                    color: 'white',
                    display: 'block',
                    marginRight: '10px',
                  }}>
                  {page.name}
                </Button>
              ) : (
                <Button
                  variant="contained"
                  key={page.name}
                  onClick={() => {
                    handleCloseNavMenu(), router.push(page.url);
                  }}
                  sx={{ my: 2, color: 'white', display: 'block', marginRight: '10px' }}>
                  {page.name}
                </Button>
              );
            })}
          </Box>
          <Box sx={{ flexGrow: 0 }}>
            <Tooltip title="Notificaciones">
              <IconButton
                size="large"
                aria-label={'Hay' + responses.length + ' notificación pendiente de leer'}
                color="inherit"
                onClick={handleOpenNotificationsMenu}>
                <Badge badgeContent={responses.length} color="error">
                  <NotificationsIcon />
                </Badge>
              </IconButton>
            </Tooltip>
            {responses.length > 0 && (
              <Menu
                sx={{ mt: '45px' }}
                id="menu-notifications"
                anchorEl={anchorElNotifications}
                anchorOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                open={Boolean(anchorElNotifications)}
                onClose={handleCloseNotificationsMenu}>
                {responses.map((res, index) => (
                  <MenuItem key={index} onClick={() => handleClickNotificationsMenu(index)}>
                    <Typography>
                      {res?.timestamp?.toLocaleTimeString()} {res?.text}
                    </Typography>
                  </MenuItem>
                ))}
              </Menu>
            )}
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
}
export default ToolBar;
