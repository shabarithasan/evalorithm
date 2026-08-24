import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Paper,
  LinearProgress,
  Alert,
  CircularProgress,
  Chip,
} from '@mui/material';
import PeopleIcon from '@mui/icons-material/People';
import StorageIcon from '@mui/icons-material/Storage';
import HealthAndSafetyIcon from '@mui/icons-material/HealthAndSafety';
import SpeedIcon from '@mui/icons-material/Speed';
import PageHeader from '../../components/common/PageHeader';
import { monitoringService } from '../../services';
import { AdminMonitoringData } from '../../types';

const AdminMonitoringPage: React.FC = () => {
  const [data, setData] = useState<AdminMonitoringData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());

  const fetchMonitoringData = useCallback(async () => {
    try {
      const response = await monitoringService.getDashboard();
      if (response.success) {
        setData(response.data);
      }
    } catch {
      setError('Failed to load monitoring data');
    } finally {
      setLoading(false);
      setLastRefresh(new Date());
    }
  }, []);

  useEffect(() => {
    fetchMonitoringData();
    const interval = setInterval(fetchMonitoringData, 30000);
    return () => clearInterval(interval);
  }, [fetchMonitoringData]);

  const getHealthColor = (value: number): 'success' | 'warning' | 'error' => {
    if (value < 60) return 'success';
    if (value < 85) return 'warning';
    return 'error';
  };

  if (loading) {
    return (
      <Box>
        <PageHeader title="System Monitoring" subtitle="Real-time system health and performance" />
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      </Box>
    );
  }

  return (
    <Box>
      <PageHeader
        title="System Monitoring"
        subtitle={`Last updated: ${lastRefresh.toLocaleTimeString()} (auto-refresh 30s)`}
      />

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={3}>
          <Card variant="outlined">
            <CardContent sx={{ textAlign: 'center' }}>
              <PeopleIcon sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
              <Typography variant="h3" fontWeight={700} color="primary.main">
                {data?.onlineUsers || 0}
              </Typography>
              <Typography variant="body2" color="text.secondary">Online Users</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card variant="outlined">
            <CardContent sx={{ textAlign: 'center' }}>
              <HealthAndSafetyIcon sx={{ fontSize: 40, color: 'success.main', mb: 1 }} />
              <Typography variant="h3" fontWeight={700} color="success.main">
                {data?.databaseHealth?.status || 'OK'}
              </Typography>
              <Typography variant="body2" color="text.secondary">DB Status</Typography>
              <Typography variant="caption" color="text.secondary">
                Response: {data?.databaseHealth?.responseTime || 0}ms
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card variant="outlined">
            <CardContent sx={{ textAlign: 'center' }}>
              <SpeedIcon sx={{ fontSize: 40, color: 'warning.main', mb: 1 }} />
              <Typography variant="h3" fontWeight={700} color="warning.main">
                {data?.systemHealth?.cpuUsage || 0}%
              </Typography>
              <Typography variant="body2" color="text.secondary">CPU Usage</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card variant="outlined">
            <CardContent sx={{ textAlign: 'center' }}>
              <StorageIcon sx={{ fontSize: 40, color: 'info.main', mb: 1 }} />
              <Typography variant="h3" fontWeight={700} color="info.main">
                {data?.storageUsage ? ((data.storageUsage.used / data.storageUsage.total) * 100).toFixed(0) : 0}%
              </Typography>
              <Typography variant="body2" color="text.secondary">Storage Used</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>System Health</Typography>
              <Box sx={{ mb: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                  <Typography variant="body2">CPU Usage</Typography>
                  <Typography variant="body2" fontWeight={600}>{data?.systemHealth?.cpuUsage || 0}%</Typography>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={data?.systemHealth?.cpuUsage || 0}
                  color={getHealthColor(data?.systemHealth?.cpuUsage || 0)}
                  sx={{ height: 10, borderRadius: 5 }}
                />
              </Box>
              <Box sx={{ mb: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                  <Typography variant="body2">Memory Usage</Typography>
                  <Typography variant="body2" fontWeight={600}>{data?.systemHealth?.memoryUsage || 0}%</Typography>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={data?.systemHealth?.memoryUsage || 0}
                  color={getHealthColor(data?.systemHealth?.memoryUsage || 0)}
                  sx={{ height: 10, borderRadius: 5 }}
                />
              </Box>
              <Box sx={{ mb: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                  <Typography variant="body2">Disk Usage</Typography>
                  <Typography variant="body2" fontWeight={600}>{data?.systemHealth?.diskUsage || 0}%</Typography>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={data?.systemHealth?.diskUsage || 0}
                  color={getHealthColor(data?.systemHealth?.diskUsage || 0)}
                  sx={{ height: 10, borderRadius: 5 }}
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Storage Usage</Typography>
              {data?.storageUsage && (
                <>
                  <Box sx={{ mb: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                      <Typography variant="body2">
                        Used: {(data.storageUsage.used / (1024 * 1024 * 1024)).toFixed(2)} GB
                      </Typography>
                      <Typography variant="body2">
                        Total: {(data.storageUsage.total / (1024 * 1024 * 1024)).toFixed(2)} GB
                      </Typography>
                    </Box>
                    <LinearProgress
                      variant="determinate"
                      value={(data.storageUsage.used / data.storageUsage.total) * 100}
                      color={getHealthColor((data.storageUsage.used / data.storageUsage.total) * 100)}
                      sx={{ height: 10, borderRadius: 5 }}
                    />
                  </Box>
                  <Grid container spacing={1}>
                    <Grid item xs={6}>
                      <Paper variant="outlined" sx={{ p: 1.5, textAlign: 'center' }}>
                        <Typography variant="body2" color="text.secondary">Database</Typography>
                        <Chip label={data.databaseHealth?.status || 'OK'} color="success" size="small" />
                      </Paper>
                    </Grid>
                    <Grid item xs={6}>
                      <Paper variant="outlined" sx={{ p: 1.5, textAlign: 'center' }}>
                        <Typography variant="body2" color="text.secondary">Response Time</Typography>
                        <Typography fontWeight={600}>{data.databaseHealth?.responseTime || 0}ms</Typography>
                      </Paper>
                    </Grid>
                  </Grid>
                </>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AdminMonitoringPage;
