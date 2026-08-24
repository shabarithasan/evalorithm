import React, { useState, useEffect } from 'react';
import { Box, Grid, Typography, Card, CardContent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import StatsCard from '../../components/common/StatsCard';
import GrowthLineChart from '../../components/analytics/GrowthLineChart';
import DepartmentPieChart from '../../components/analytics/DepartmentPieChart';
import LeaderboardTable from '../../components/analytics/LeaderboardTable';
import LoadingScreen from '../../components/common/LoadingScreen';
import { analyticsService } from '../../services';
import { AdminAnalyticsData, LeaderboardItem } from '../../types';
import PeopleIcon from '@mui/icons-material/People';
import PersonIcon from '@mui/icons-material/Person';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import AssignmentIcon from '@mui/icons-material/Assignment';

const AdminAnalyticsPage: React.FC = () => {
  const [data, setData] = useState<AdminAnalyticsData | null>(null);
  const [facultyPerf, setFacultyPerf] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [overviewRes, growthRes, deptRes, topRes, lowRes, facultyRes] = await Promise.allSettled([
        analyticsService.getAdminOverview(),
        analyticsService.getStudentGrowth(),
        analyticsService.getDepartmentPerformance(),
        analyticsService.getTopPerformersGlobal(),
        analyticsService.getLowPerformersGlobal(),
        analyticsService.getFacultyPerformance(),
      ]);

      const overview = overviewRes.status === 'fulfilled' ? overviewRes.value.data : null;
      const growth = growthRes.status === 'fulfilled' ? growthRes.value.data : [];
      const dept = deptRes.status === 'fulfilled' ? deptRes.value.data : [];
      const top = topRes.status === 'fulfilled' ? topRes.value.data : [];
      const low = lowRes.status === 'fulfilled' ? lowRes.value.data : [];
      const faculty = facultyRes.status === 'fulfilled' ? facultyRes.value.data : [];

      if (overview) {
        setData({
          ...overview,
          studentGrowth: growth || [],
          departmentPerformance: dept || [],
          topPerformers: top || [],
          lowPerformers: low || [],
        });
      }
      setFacultyPerf(faculty || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>Analytics Overview</Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard title="Total Students" value={data?.totalStudents || 0} icon={<PeopleIcon />} color="#1565C0" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard title="Total Faculty" value={data?.totalFaculty || 0} icon={<PersonIcon />} color="#2E7D32" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard title="Total Subjects" value={data?.totalSubjects || 0} icon={<MenuBookIcon />} color="#9C27B0" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard title="Total Exams" value={data?.totalExams || 0} icon={<AssignmentIcon />} color="#FF9800" />
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 1 }}>Overall Pass Rate</Typography>
              <Typography variant="h3" color="primary" sx={{ fontWeight: 700 }}>
                {data?.overallPassRate?.toFixed(1) || 0}%
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Average Score: {data?.averageScore?.toFixed(1) || 0}%
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <DepartmentPieChart
            data={(data?.departmentPerformance || []).map((d) => ({
              departmentName: d.departmentName,
              value: d.averageScore,
            }))}
            title="Department Performance"
          />
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <GrowthLineChart data={data?.studentGrowth || []} title="Student Enrollment Growth" />
        </Grid>
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Department Details</Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Department</TableCell>
                      <TableCell align="right">Avg Score</TableCell>
                      <TableCell align="right">Pass Rate</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {(data?.departmentPerformance || []).map((d) => (
                      <TableRow key={d.departmentName} hover>
                        <TableCell>{d.departmentName}</TableCell>
                        <TableCell align="right">{d.averageScore.toFixed(1)}%</TableCell>
                        <TableCell align="right">{d.passRate.toFixed(1)}%</TableCell>
                      </TableRow>
                    ))}
                    {(!data?.departmentPerformance || data.departmentPerformance.length === 0) && (
                      <TableRow>
                        <TableCell colSpan={3} align="center">No data available</TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <LeaderboardTable data={data?.topPerformers || []} title="Top Performers" />
        </Grid>
        <Grid item xs={12} md={6}>
          <LeaderboardTable data={data?.lowPerformers || []} title="At-Risk Students" />
        </Grid>
      </Grid>

      {facultyPerf.length > 0 && (
        <Card sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Faculty Performance</Typography>
            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Faculty</TableCell>
                    <TableCell>Subject</TableCell>
                    <TableCell align="right">Total Exams</TableCell>
                    <TableCell align="right">Avg Class Score</TableCell>
                    <TableCell align="right">Students</TableCell>
                    <TableCell align="right">Pass Rate</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {facultyPerf.map((f: any, i: number) => (
                    <TableRow key={i} hover>
                      <TableCell>{f.facultyName}</TableCell>
                      <TableCell>{f.subjectName}</TableCell>
                      <TableCell align="right">{f.totalExams}</TableCell>
                      <TableCell align="right">{f.averageClassScore?.toFixed(1)}%</TableCell>
                      <TableCell align="right">{f.totalStudents}</TableCell>
                      <TableCell align="right">{f.passRate?.toFixed(1)}%</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default AdminAnalyticsPage;
