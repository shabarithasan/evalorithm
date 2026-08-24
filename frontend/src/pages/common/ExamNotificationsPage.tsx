import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
  IconButton,
} from '@mui/material';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ScheduleIcon from '@mui/icons-material/Schedule';
import AssignmentIcon from '@mui/icons-material/Assignment';
import MarkEmailReadIcon from '@mui/icons-material/MarkEmailRead';
import LoadingScreen from '../../components/common/LoadingScreen';
import { examNotificationService } from '../../services';
import { ExamNotification } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const ExamNotificationsPage: React.FC = () => {
  const [notifications, setNotifications] = useState<ExamNotification[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      const res = await examNotificationService.getAll();
      if (res.success) setNotifications(res.data);
    } catch {
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await examNotificationService.markAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      );
    } catch {}
  };

  const handleMarkAllAsRead = async () => {
    try {
      await examNotificationService.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
    } catch {}
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'EXAM_PUBLISHED': return <AssignmentIcon />;
      case 'EXAM_REMINDER': return <ScheduleIcon />;
      case 'EXAM_COMPLETED': return <CheckCircleIcon />;
      case 'RESULT_PUBLISHED': return <CheckCircleIcon />;
      default: return <NotificationsActiveIcon />;
    }
  };

  const getNotificationColor = (type: string): string => {
    switch (type) {
      case 'EXAM_PUBLISHED': return '#1565C0';
      case 'EXAM_REMINDER': return '#E65100';
      case 'EXAM_COMPLETED': return '#2E7D32';
      case 'RESULT_PUBLISHED': return '#7B1FA2';
      default: return '#757575';
    }
  };

  if (loading) return <LoadingScreen />;

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>Exam Notifications</Typography>
          <Typography variant="body2" color="text.secondary">
            {unreadCount > 0 ? `${unreadCount} unread notification${unreadCount > 1 ? 's' : ''}` : 'All caught up!'}
          </Typography>
        </Box>
        {unreadCount > 0 && (
          <IconButton onClick={handleMarkAllAsRead} color="primary">
            <MarkEmailReadIcon />
          </IconButton>
        )}
      </Box>

      <Card>
        <CardContent>
          {notifications.length === 0 ? (
            <Box sx={{ textAlign: 'center', py: 6 }}>
              <NotificationsActiveIcon sx={{ fontSize: 48, color: 'grey.300', mb: 2 }} />
              <Typography color="text.secondary">No notifications</Typography>
            </Box>
          ) : (
            <List>
              {notifications.map((notification) => (
                <ListItem
                  key={notification.id}
                  sx={{
                    mb: 1,
                    borderRadius: 1,
                    border: '1px solid',
                    borderColor: notification.isRead ? 'grey.200' : `${getNotificationColor(notification.notificationType)}30`,
                    bgcolor: notification.isRead ? 'transparent' : `${getNotificationColor(notification.notificationType)}05`,
                    cursor: 'pointer',
                    '&:hover': { bgcolor: 'grey.50' },
                  }}
                  onClick={() => !notification.isRead && handleMarkAsRead(notification.id)}
                >
                  <ListItemIcon sx={{ color: getNotificationColor(notification.notificationType) }}>
                    {getNotificationIcon(notification.notificationType)}
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Typography variant="body2" sx={{ fontWeight: notification.isRead ? 400 : 600 }}>
                          {notification.title}
                        </Typography>
                        {!notification.isRead && (
                          <Box sx={{ width: 8, height: 8, borderRadius: '50%', bgcolor: 'primary.main' }} />
                        )}
                      </Box>
                    }
                    secondary={
                      <Box>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                          {notification.message}
                        </Typography>
                        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                          <Chip label={notification.examTitle} size="small" variant="outlined" />
                          <Typography variant="caption" color="text.secondary">
                            {formatDateTime(notification.sentAt)}
                          </Typography>
                        </Box>
                      </Box>
                    }
                  />
                </ListItem>
              ))}
            </List>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default ExamNotificationsPage;
