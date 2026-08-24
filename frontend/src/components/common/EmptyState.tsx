import React from 'react';
import { Box, Typography } from '@mui/material';
import InboxIcon from '@mui/icons-material/Inbox';

interface EmptyStateProps {
  icon?: React.ReactNode;
  title?: string;
  message?: string;
}

const EmptyState: React.FC<EmptyStateProps> = ({
  icon,
  title = 'No data found',
  message = 'There are no records to display.',
}) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        py: 8,
        gap: 1,
      }}
    >
      {icon || <InboxIcon sx={{ fontSize: 64, color: 'grey.300' }} />}
      <Typography variant="h6" color="text.secondary">
        {title}
      </Typography>
      <Typography variant="body2" color="text.secondary">
        {message}
      </Typography>
    </Box>
  );
};

export default EmptyState;
