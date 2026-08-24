import React, { useState, useEffect } from 'react';
import { Box, Typography, Card, CardContent, Chip, Alert } from '@mui/material';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import LoadingScreen from '../../components/common/LoadingScreen';
import { insightService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import { SmartNotification, LearningPriorityLevel } from '../../types';

const priorityColors: Record<LearningPriorityLevel, 'error' | 'warning' | 'info' | 'default'> = {
  CRITICAL: 'error',
  HIGH: 'warning',
  MEDIUM: 'info',
  LOW: 'default',
};

const SmartNotificationsPage: React.FC = () => {
  const { user } = useAuth();
  const studentId = user?.userId || 0;
  const [notifications, setNotifications] = useState<SmartNotification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (studentId) fetchNotifications();
  }, [studentId]);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const res = await insightService.getSmartNotifications(studentId);
      if (res.data?.success) setNotifications(res.data.data || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load notifications');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>Smart Notifications</Typography>

      {notifications.length === 0 ? (
        <Alert severity="info">No smart notifications at this time.</Alert>
      ) : (
        notifications.map((n) => (
          <Card key={n.id} sx={{ mb: 2, borderLeft: 4, borderColor: n.priority === 'CRITICAL' ? 'error.main' : 'primary.main' }}>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Box sx={{ display: 'flex', gap: 1.5 }}>
                  <NotificationsActiveIcon sx={{ color: 'primary.main', mt: 0.5 }} />
                  <Box>
                    <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{n.title}</Typography>
                    <Typography variant="body2" color="text.secondary">{n.message}</Typography>
                    <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
                      {new Date(n.generatedAt).toLocaleString()}
                    </Typography>
                  </Box>
                </Box>
                <Chip label={n.priority} size="small" color={priorityColors[n.priority]} />
              </Box>
            </CardContent>
          </Card>
        ))
      )}
    </Box>
  );
};

export default SmartNotificationsPage;
