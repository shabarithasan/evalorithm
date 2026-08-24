import React, { useState, useEffect } from 'react';
import { Box, Grid, Card, CardContent, Typography, List, ListItem, ListItemText, ListItemAvatar, Avatar } from '@mui/material';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import QuizIcon from '@mui/icons-material/Quiz';
import HourglassBottomIcon from '@mui/icons-material/HourglassBottom';
import StatsCard from '../../components/common/StatsCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { dashboardService } from '../../services';
import { FacultyDashboardData } from '../../types';

const FacultyDashboard: React.FC = () => {
  const [data, setData] = useState<FacultyDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await dashboardService.getFacultyDashboard();
      if (response.success) {
        setData(response.data);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;
  if (!data) return null;

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Faculty Dashboard
      </Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard
            title="Assigned Subjects"
            value={data.assignedSubjects}
            icon={<MenuBookIcon />}
            color="#1565C0"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard
            title="Question Count"
            value={data.questionCount}
            icon={<QuizIcon />}
            color="#2E7D32"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard
            title="Pending Questions"
            value={data.pendingQuestions}
            icon={<HourglassBottomIcon />}
            color="#D32F2F"
          />
        </Grid>
      </Grid>

      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Quick Overview
          </Typography>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2, bgcolor: 'grey.50', borderRadius: 2 }}>
              <Typography variant="body1">Subjects to manage</Typography>
              <Typography variant="h6" color="primary">{data.assignedSubjects}</Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2, bgcolor: 'grey.50', borderRadius: 2 }}>
              <Typography variant="body1">Total questions created</Typography>
              <Typography variant="h6" color="primary">{data.questionCount}</Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2, bgcolor: 'grey.50', borderRadius: 2 }}>
              <Typography variant="body1">Questions pending review</Typography>
              <Typography variant="h6" color="error">{data.pendingQuestions}</Typography>
            </Box>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default FacultyDashboard;
