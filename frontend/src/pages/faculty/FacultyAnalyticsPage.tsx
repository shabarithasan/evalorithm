import React, { useState, useEffect } from 'react';
import { Box, Typography, Card, CardContent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Grid, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { analyticsService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import StatsCard from '../../components/common/StatsCard';
import LeaderboardTable from '../../components/analytics/LeaderboardTable';
import LoadingScreen from '../../components/common/LoadingScreen';
import { FacultyAnalyticsData, LeaderboardItem } from '../../types';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import PeopleIcon from '@mui/icons-material/People';
import AssignmentIcon from '@mui/icons-material/Assignment';

const FacultyAnalyticsPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const facultyId = user?.userId || 0;
  const [dashboard, setDashboard] = useState<FacultyAnalyticsData[]>([]);
  const [topPerformers, setTopPerformers] = useState<LeaderboardItem[]>([]);
  const [lowPerformers, setLowPerformers] = useState<LeaderboardItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (facultyId) fetchData();
  }, [facultyId]);

  const fetchData = async () => {
    try {
      const [dashRes, topRes, lowRes] = await Promise.allSettled([
        analyticsService.getFacultyDashboard(facultyId),
        analyticsService.getTopPerformers(facultyId, 0),
        analyticsService.getLowPerformers(facultyId, 0),
      ]);

      if (dashRes.status === 'fulfilled' && dashRes.value.success) {
        setDashboard(Array.isArray(dashRes.value.data) ? dashRes.value.data : [dashRes.value.data]);
      }
      if (topRes.status === 'fulfilled' && topRes.value.success) setTopPerformers(topRes.value.data || []);
      if (lowRes.status === 'fulfilled' && lowRes.value.success) setLowPerformers(lowRes.value.data || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  const totalStudents = dashboard.reduce((s, d) => s + (d.totalStudents || 0), 0);
  const avgScore = dashboard.length > 0 ? dashboard.reduce((s, d) => s + (d.averageClassScore || 0), 0) / dashboard.length : 0;
  const totalExams = dashboard.reduce((s, d) => s + (d.totalExams || 0), 0);

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>Faculty Analytics</Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard title="Total Students" value={totalStudents} icon={<PeopleIcon />} color="#1565C0" />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard title="Avg Class Score" value={`${avgScore.toFixed(1)}%`} icon={<TrendingUpIcon />} color="#2E7D32" />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard title="Total Exams" value={totalExams} icon={<AssignmentIcon />} color="#FF9800" />
        </Grid>
      </Grid>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2 }}>Subject Analysis</Typography>
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Subject</TableCell>
                  <TableCell align="right">Exams</TableCell>
                  <TableCell align="right">Students</TableCell>
                  <TableCell align="right">Avg Score</TableCell>
                  <TableCell align="right">Pass Rate</TableCell>
                  <TableCell align="center">Action</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {dashboard.map((d) => (
                  <TableRow key={d.subjectName} hover>
                    <TableCell>{d.subjectName}</TableCell>
                    <TableCell align="right">{d.totalExams}</TableCell>
                    <TableCell align="right">{d.totalStudents}</TableCell>
                    <TableCell align="right">{d.averageClassScore?.toFixed(1)}%</TableCell>
                    <TableCell align="right">{d.passRate?.toFixed(1)}%</TableCell>
                    <TableCell align="center">
                      <Button size="small" variant="outlined" onClick={() => navigate(`/faculty/analytics/subject/${d.subjectId || 0}`)}>
                        Details
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
                {dashboard.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={6} align="center">No subject data available</TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <LeaderboardTable data={topPerformers} title="Top Performers" />
        </Grid>
        <Grid item xs={12} md={6}>
          <LeaderboardTable data={lowPerformers} title="At-Risk Students" />
        </Grid>
      </Grid>
    </Box>
  );
};

export default FacultyAnalyticsPage;
