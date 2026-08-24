import React, { useState, useEffect } from 'react';
import { Box, Grid, Card, CardContent, Typography } from '@mui/material';
import BusinessIcon from '@mui/icons-material/Business';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import PeopleIcon from '@mui/icons-material/People';
import SchoolIcon from '@mui/icons-material/School';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import StatsCard from '../../components/common/StatsCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { dashboardService } from '../../services';
import { DashboardData } from '../../types';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const AdminDashboard: React.FC = () => {
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await dashboardService.getAdminDashboard();
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

  const chartData = {
    labels: ['Departments', 'Subjects', 'Faculty', 'Students', 'Questions'],
    datasets: [
      {
        label: 'Count',
        data: [
          data.totalDepartments,
          data.totalSubjects,
          data.totalFaculty,
          data.totalStudents,
          data.totalQuestions,
        ],
        backgroundColor: [
          'rgba(21, 101, 192, 0.8)',
          'rgba(30, 136, 229, 0.8)',
          'rgba(66, 165, 245, 0.8)',
          'rgba(100, 181, 246, 0.8)',
          'rgba(144, 202, 249, 0.8)',
        ],
        borderColor: [
          '#1565C0',
          '#1E88E5',
          '#42A5F5',
          '#64B5F6',
          '#90CAF9',
        ],
        borderWidth: 1,
        borderRadius: 6,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
    },
    scales: {
      y: {
        beginAtZero: true,
        grid: { color: 'rgba(0,0,0,0.05)' },
      },
      x: {
        grid: { display: false },
      },
    },
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Dashboard
      </Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard
            title="Departments"
            value={data.totalDepartments}
            icon={<BusinessIcon />}
            color="#1565C0"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard
            title="Subjects"
            value={data.totalSubjects}
            icon={<MenuBookIcon />}
            color="#1E88E5"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard
            title="Faculty"
            value={data.totalFaculty}
            icon={<PeopleIcon />}
            color="#2E7D32"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard
            title="Students"
            value={data.totalStudents}
            icon={<SchoolIcon />}
            color="#D32F2F"
          />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Overview
              </Typography>
              <Box sx={{ height: 350 }}>
                <Bar data={chartData} options={chartOptions} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Quick Stats
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" color="text.secondary">Total Questions</Typography>
                  <Typography variant="h6" color="primary">{data.totalQuestions}</Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" color="text.secondary">Active Faculty</Typography>
                  <Typography variant="h6" color="primary">{data.totalFaculty}</Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" color="text.secondary">Enrolled Students</Typography>
                  <Typography variant="h6" color="primary">{data.totalStudents}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AdminDashboard;
