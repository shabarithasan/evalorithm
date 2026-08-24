import React, { useState, useEffect } from 'react';
import { Box, Grid, Typography } from '@mui/material';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import EventIcon from '@mui/icons-material/Event';
import AssessmentIcon from '@mui/icons-material/Assessment';
import StatsCard from '../../components/common/StatsCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { dashboardService } from '../../services';
import { StudentDashboardData } from '../../types';

const StudentDashboard: React.FC = () => {
  const [data, setData] = useState<StudentDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await dashboardService.getStudentDashboard();
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
        Student Dashboard
      </Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard
            title="Enrolled Subjects"
            value={data.enrolledSubjects}
            icon={<MenuBookIcon />}
            color="#1565C0"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard
            title="Upcoming Exams"
            value={data.upcomingExams}
            icon={<EventIcon />}
            color="#D32F2F"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard
            title="Recent Results"
            value={data.recentResults?.length || 0}
            icon={<AssessmentIcon />}
            color="#2E7D32"
          />
        </Grid>
      </Grid>

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
        <Box sx={{ p: 3, bgcolor: 'background.paper', borderRadius: 3, boxShadow: '0 2px 12px rgba(0,0,0,0.08)' }}>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Welcome to EVALORITHM
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Track your academic progress, view enrolled subjects, and check upcoming exams from this dashboard.
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default StudentDashboard;
