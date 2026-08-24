import React from 'react';
import {
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Typography,
  Box,
  Divider,
  Chip,
} from '@mui/material';
import LoginIcon from '@mui/icons-material/Login';
import SystemUpdateIcon from '@mui/icons-material/SystemUpdate';
import SchoolIcon from '@mui/icons-material/School';
import { Notification } from '../../types';
import { formatDateTime } from '../../utils/helpers';

interface NotificationListProps {
  notifications: Notification[];
}

const getNotificationIcon = (type: string) => {
  switch (type) {
    case 'LOGIN':
      return <LoginIcon />;
    case 'SYSTEM':
      return <SystemUpdateIcon />;
    case 'ACADEMIC':
      return <SchoolIcon />;
    default:
      return <SystemUpdateIcon />;
  }
};

const getNotificationColor = (type: string): string => {
  switch (type) {
    case 'LOGIN':
      return '#1565C0';
    case 'SYSTEM':
      return '#D32F2F';
    case 'ACADEMIC':
      return '#2E7D32';
    default:
      return '#757575';
  }
};

const NotificationList: React.FC<NotificationListProps> = ({ notifications }) => {
  if (notifications.length === 0) {
    return (
      <Box sx={{ py: 4, textAlign: 'center' }}>
        <Typography color="text.secondary">No notifications</Typography>
      </Box>
    );
  }

  return (
    <List disablePadding>
      {notifications.map((notification, index) => (
        <React.Fragment key={notification.id}>
          <ListItem
            alignItems="flex-start"
            disablePadding
            sx={{
              py: 1.5,
              opacity: notification.read ? 0.6 : 1,
            }}
          >
            <ListItemAvatar>
              <Avatar
                sx={{
                  bgcolor: `${getNotificationColor(notification.type)}15`,
                  color: getNotificationColor(notification.type),
                  width: 40,
                  height: 40,
                }}
              >
                {getNotificationIcon(notification.type)}
              </Avatar>
            </ListItemAvatar>
            <ListItemText
              primary={
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Typography variant="subtitle2" sx={{ fontWeight: notification.read ? 400 : 600 }}>
                    {notification.title}
                  </Typography>
                  {!notification.read && (
                    <Box
                      sx={{
                        width: 8,
                        height: 8,
                        borderRadius: '50%',
                        bgcolor: 'primary.main',
                      }}
                    />
                  )}
                </Box>
              }
              secondary={
                <>
                  <Typography variant="body2" color="text.secondary" component="span">
                    {notification.message}
                  </Typography>
                  <br />
                  <Box sx={{ display: 'flex', gap: 1, mt: 0.5, alignItems: 'center' }}>
                    <Chip
                      label={notification.type}
                      size="small"
                      sx={{
                        height: 20,
                        fontSize: '0.7rem',
                        bgcolor: `${getNotificationColor(notification.type)}15`,
                        color: getNotificationColor(notification.type),
                      }}
                    />
                    <Typography variant="caption" color="text.secondary">
                      {formatDateTime(notification.createdAt)}
                    </Typography>
                  </Box>
                </>
              }
            />
          </ListItem>
          {index < notifications.length - 1 && <Divider />}
        </React.Fragment>
      ))}
    </List>
  );
};

export default NotificationList;
