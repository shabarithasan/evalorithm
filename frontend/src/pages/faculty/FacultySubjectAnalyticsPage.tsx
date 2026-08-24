import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Typography, Card, CardContent, Grid, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import StatsCard from '../../components/common/StatsCard';
import LeaderboardTable from '../../components/analytics/LeaderboardTable';
import DifficultyBarChart from '../../components/analytics/DifficultyBarChart';
import LoadingScreen from '../../components/common/LoadingScreen';
import { analyticsService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import { LeaderboardItem } from '../../types';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import PeopleIcon from '@mui/icons-material/People';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const FacultySubjectAnalyticsPage: React.FC = () => {
  const { subjectId } = useParams<{ subjectId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const facultyId = user?.userId || 0;
  const sid = Number(subjectId) || 0;

  const [classPerf, setClassPerf] = useState<any>(null);
  const [topPerformers, setTopPerformers] = useState<LeaderboardItem[]>([]);
  const [lowPerformers, setLowPerformers] = useState<LeaderboardItem[]>([]);
  const [difficultyPerf, setDifficultyPerf] = useState<Record<string, number>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (facultyId && sid) fetchData();
  }, [facultyId, sid]);

  const fetchData = async () => {
    try {
      const [classRes, topRes, lowRes] = await Promise.allSettled([
        analyticsService.getClassPerformance(facultyId, sid),
        analyticsService.getTopPerformers(facultyId, sid),
        analyticsService.getLowPerformers(facultyId, sid),
      ]);

      if (classRes.status === 'fulfilled' && classRes.value.success) {
        setClassPerf(classRes.value.data);
      }
      if (topRes.status === 'fulfilled' && topRes.value.success) setTopPerformers(topRes.value.data || []);
      if (lowRes.status === 'fulfilled' && lowRes.value.success) setLowPerformers(lowRes.value.data || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load subject analytics');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/faculty/analytics')} sx={{ mb: 2 }}>
        Back to Analytics
      </Button>
      <Typography variant="h4" sx={{ mb: 3 }}>Subject Analytics</Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard title="Class Average" value={`${classPerf?.averageScore?.toFixed(1) || 0}%`} icon={<TrendingUpIcon />} color="#1565C0" />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard title="Total Students" value={classPerf?.totalStudents || 0} icon={<PeopleIcon />} color="#2E7D32" />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatsCard title="Pass Rate" value={`${classPerf?.passRate?.toFixed(1) || 0}%`} icon={<CheckCircleIcon />} color="#FF9800" />
        </Grid>
      </Grid>

      {classPerf?.studentRankings && (
        <Card sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Student Rankings</Typography>
            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Rank</TableCell>
                    <TableCell>Student</TableCell>
                    <TableCell align="right">Score</TableCell>
                    <TableCell align="right">Accuracy</TableCell>
                    <TableCell align="right">Exams</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {classPerf.studentRankings.map((s: any) => (
                    <TableRow key={s.rank} hover>
                      <TableCell>{s.rank}</TableCell>
                      <TableCell>{s.studentName}</TableCell>
                      <TableCell align="right">{s.score}</TableCell>
                      <TableCell align="right">{s.accuracy?.toFixed(1)}%</TableCell>
                      <TableCell align="right">{s.totalExams}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      )}

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

export default FacultySubjectAnalyticsPage;
