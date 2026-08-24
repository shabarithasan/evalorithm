import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Divider,
  CircularProgress,
  Chip,
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import DoneAllIcon from '@mui/icons-material/DoneAll';
import LoginIcon from '@mui/icons-material/Login';
import SystemUpdateIcon from '@mui/icons-material/SystemUpdate';
import SchoolIcon from '@mui/icons-material/School';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import EmptyState from '../../components/common/EmptyState';
import { notificationService } from '../../services';
import { Notification } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const NotificationsPage: React.FC = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [markingAll, setMarkingAll] = useState(false);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      const response = await notificationService.getAll();
      if (response.success) {
        setNotifications(response.data);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationService.markAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
      );
    } catch {
      // Handle error
    }
  };

  const handleMarkAllAsRead = async () => {
    setMarkingAll(true);
    try {
      await notificationService.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    } catch {
      // Handle error
    } finally {
      setMarkingAll(false);
    }
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'LOGIN': return <LoginIcon />;
      case 'SYSTEM': return <SystemUpdateIcon />;
      case 'ACADEMIC': return <SchoolIcon />;
      default: return <NotificationsIcon />;
    }
  };

  const getNotificationColor = (type: string): string => {
    switch (type) {
      case 'LOGIN': return '#1565C0';
      case 'SYSTEM': return '#D32F2F';
      case 'ACADEMIC': return '#2E7D32';
      default: return '#757575';
    }
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4">Notifications</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
            {unreadCount > 0 ? `${unreadCount} unread notification(s)` : 'All caught up!'}
          </Typography>
        </Box>
        {unreadCount > 0 && (
          <Button
            variant="outlined"
            startIcon={markingAll ? <CircularProgress size={18} /> : <DoneAllIcon />}
            onClick={handleMarkAllAsRead}
            disabled={markingAll}
          >
            Mark All Read
          </Button>
        )}
      </Box>

      {notifications.length === 0 ? (
        <EmptyState title="No notifications" message="You're all caught up!" />
      ) : (
        <Card>
          <CardContent sx={{ p: 0 }}>
            <List disablePadding>
              {notifications.map((notification, index) => (
                <React.Fragment key={notification.id}>
                  <ListItem
                    sx={{
                      py: 2,
                      px: 3,
                      bgcolor: notification.read ? 'transparent' : 'action.hover',
                      cursor: 'pointer',
                      '&:hover': { bgcolor: 'grey.50' },
                    }}
                    onClick={() => !notification.read && handleMarkAsRead(notification.id)}
                  >
                    <ListItemAvatar>
                      <Avatar
                        sx={{
                          bgcolor: `${getNotificationColor(notification.type)}15`,
                          color: getNotificationColor(notification.type),
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
                            <Box sx={{ width: 8, height: 8, borderRadius: '50%', bgcolor: 'primary.main' }} />
                          )}
                        </Box>
                      }
                      secondary={
                        <>
                          <Typography variant="body2" color="text.secondary" component="span">
                            {notification.message}
                          </Typography>
                          <Box sx={{ display: 'flex', gap: 1, mt: 0.5 }}>
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
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default NotificationsPage;
