import * as React from 'react';
import Typography from '@mui/material/Typography';
import MuiLink from '@mui/material/Link';

export default function Copyright() {
  return (
    <Typography
      variant="body2"
      align="center"
      sx={{
        color: 'text.secondary',
        margin: '25px',
      }}>
      {'Copyright © '}
      <MuiLink color="inherit" href="https://rarcos.com/">
        rarcos.com
      </MuiLink>{' '}
      {new Date().getFullYear()}.
    </Typography>
  );
}
