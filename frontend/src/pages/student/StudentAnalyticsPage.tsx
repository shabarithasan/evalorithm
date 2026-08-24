import React, { useState, useEffect } from 'react';
import { Box, Grid, Typography, Button, Card, CardContent } from '@mui/material';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import CalculateIcon from '@mui/icons-material/Calculate';
import SubjectRadarChart from '../../components/analytics/SubjectRadarChart';
import DifficultyBarChart from '../../components/analytics/DifficultyBarChart';
import PerformanceLineChart from '../../components/analytics/PerformanceLineChart';
import StatsCard from '../../components/common/StatsCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { analyticsService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import {
  SubjectPerformanceItem, AccuracyTrend, UnitPerformanceItem, TopicPerformanceItem,
} from '../../types';
import { Box as MuiBox, LinearProgress, Chip } from '@mui/material';

const StudentAnalyticsPage: React.FC = () => {
  const { user } = useAuth();
  const studentId = user?.userId || 0;
  const [subjectPerf, setSubjectPerf] = useState<SubjectPerformanceItem[]>([]);
  const [difficultyPerf, setDifficultyPerf] = useState<Record<string, number>>({});
  const [accuracyTrend, setAccuracyTrend] = useState<AccuracyTrend[]>([]);
  const [unitPerf, setUnitPerf] = useState<UnitPerformanceItem[]>([]);
  const [topicPerf, setTopicPerf] = useState<TopicPerformanceItem[]>([]);
  const [totalAttempted, setTotalAttempted] = useState(0);
  const [accuracy, setAccuracy] = useState(0);
  const [avgScore, setAvgScore] = useState(0);
  const [loading, setLoading] = useState(true);
  const [calculating, setCalculating] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (studentId) fetchAll();
  }, [studentId]);

  const fetchAll = async () => {
    setLoading(true);
    try {
      const [subRes, diffRes, trendRes] = await Promise.allSettled([
        analyticsService.getSubjectPerformance(studentId),
        analyticsService.getDifficultyPerformance(studentId),
        analyticsService.getAccuracyTrend(studentId),
      ]);

      if (subRes.status === 'fulfilled' && subRes.value.success) {
        const subData = subRes.value.data;
        setSubjectPerf(Array.isArray(subData) ? subData : []);
        if (Array.isArray(subData) && subData.length > 0) {
          setTotalAttempted(subData.reduce((s: number, p: any) => s + (p.totalQuestions || 0), 0));
          setAccuracy(subData.reduce((s: number, p: any) => s + p.accuracy, 0) / subData.length);
          setAvgScore(subData.reduce((s: number, p: any) => s + p.accuracy, 0) / subData.length);
        }
      }
      if (diffRes.status === 'fulfilled' && diffRes.value.success) {
        setDifficultyPerf(diffRes.value.data || {});
      }
      if (trendRes.status === 'fulfilled' && trendRes.value.success) {
        setAccuracyTrend(trendRes.value.data || []);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  const handleCalculate = async () => {
    setCalculating(true);
    try {
      await analyticsService.calculateAnalytics(studentId);
      await fetchAll();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to calculate analytics');
    } finally {
      setCalculating(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">My Analytics</Typography>
        <Button variant="contained" startIcon={calculating ? undefined : <CalculateIcon />} onClick={handleCalculate} disabled={calculating}>
          {calculating ? 'Calculating...' : 'Calculate Analytics'}
        </Button>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard title="Total Attempted" value={totalAttempted} icon={<TrendingUpIcon />} color="#1565C0" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard title="Accuracy" value={`${accuracy.toFixed(1)}%`} icon={<TrendingUpIcon />} color="#2E7D32" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard title="Average Score" value={`${avgScore.toFixed(1)}%`} icon={<TrendingUpIcon />} color="#9C27B0" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard title="Subjects" value={subjectPerf.length} icon={<TrendingUpIcon />} color="#FF9800" />
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <SubjectRadarChart data={subjectPerf} title="Subject Performance" />
        </Grid>
        <Grid item xs={12} md={6}>
          <DifficultyBarChart data={difficultyPerf} title="Difficulty Performance" />
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12}>
          <PerformanceLineChart data={accuracyTrend} title="Accuracy Trend Over Time" />
        </Grid>
      </Grid>

      {unitPerf.length > 0 && (
        <Card sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Unit Performance</Typography>
            {unitPerf.map((u, i) => (
              <MuiBox key={i} sx={{ mb: 2 }}>
                <MuiBox sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>{u.unitName}</Typography>
                  <Typography variant="body2" color="text.secondary">{u.accuracy.toFixed(1)}%</Typography>
                </MuiBox>
                <LinearProgress variant="determinate" value={u.accuracy} sx={{ height: 8, borderRadius: 4, bgcolor: 'grey.200' }} />
              </MuiBox>
            ))}
          </CardContent>
        </Card>
      )}

      {topicPerf.length > 0 && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Topic Performance</Typography>
            {topicPerf.map((t, i) => (
              <MuiBox key={i} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 1, borderBottom: '1px solid', borderColor: 'grey.100' }}>
                <MuiBox>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>{t.topicName}</Typography>
                  <Typography variant="caption" color="text.secondary">{t.unitName}</Typography>
                </MuiBox>
                <Chip label={`${t.accuracy.toFixed(1)}%`} size="small" color={t.accuracy >= 70 ? 'success' : t.accuracy >= 40 ? 'warning' : 'error'} />
              </MuiBox>
            ))}
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default StudentAnalyticsPage;
