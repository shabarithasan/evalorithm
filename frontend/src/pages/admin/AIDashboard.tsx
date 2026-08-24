import React, { useState, useEffect } from 'react';
import { Box, Grid, Card, CardContent, Typography, Button } from '@mui/material';
import SmartToyIcon from '@mui/icons-material/SmartToy';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import { useNavigate } from 'react-router-dom';
import AIDashboardStats from '../../components/analytics/AIDashboardStats';
import SubjectRadarChart from '../../components/analytics/SubjectRadarChart';
import DifficultyBarChart from '../../components/analytics/DifficultyBarChart';
import GrowthLineChart from '../../components/analytics/GrowthLineChart';
import InsightCard from '../../components/analytics/InsightCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { aiQuestionService, analyticsService, insightService } from '../../services';
import { AIDashboardData, SubjectPerformanceItem, AIInsight } from '../../types';

const AIDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [dashboardData, setDashboardData] = useState<AIDashboardData | null>(null);
  const [subjectPerf, setSubjectPerf] = useState<SubjectPerformanceItem[]>([]);
  const [insights, setInsights] = useState<AIInsight[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [dashRes, deptRes, growthRes] = await Promise.allSettled([
        aiQuestionService.getDashboard(),
        analyticsService.getDepartmentPerformance(),
        analyticsService.getStudentGrowth(),
      ]);

      if (dashRes.status === 'fulfilled' && dashRes.value.data?.success) {
        setDashboardData(dashRes.value.data.data);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load AI dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  const fallbackDashData: AIDashboardData = dashboardData || {
    aiGeneratedQuestions: 0,
    adaptiveExams: 0,
    studentPerformance: 0,
    weakTopicsCount: 0,
    strongTopicsCount: 0,
    recommendationsCount: 0,
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">AI Engine Dashboard</Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="contained"
            startIcon={<SmartToyIcon />}
            onClick={() => navigate('/admin/ai-questions')}
          >
            Generate Questions
          </Button>
          <Button
            variant="outlined"
            startIcon={<EmojiEventsIcon />}
            onClick={() => navigate('/admin/leaderboard')}
          >
            Leaderboard
          </Button>
        </Box>
      </Box>

      <AIDashboardStats data={fallbackDashData} />

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <SubjectRadarChart data={subjectPerf} title="Subject Performance Overview" />
        </Grid>
        <Grid item xs={12} md={6}>
          <GrowthLineChart data={[]} title="Student Growth Trend" />
        </Grid>
      </Grid>

      <Box sx={{ mt: 3 }}>
        <Typography variant="h6" sx={{ mb: 2 }}>Recent AI Insights</Typography>
        {insights.length > 0 ? (
          insights.slice(0, 5).map((insight) => (
            <InsightCard key={insight.id} data={insight} />
          ))
        ) : (
          <Card>
            <CardContent>
              <Typography color="text.secondary" sx={{ textAlign: 'center', py: 3 }}>
                No insights generated yet. Analytics will appear once students start taking exams.
              </Typography>
            </CardContent>
          </Card>
        )}
      </Box>
    </Box>
  );
};

export default AIDashboard;
