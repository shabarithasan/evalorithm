import React, { useState, useEffect } from 'react';
import { Box, Typography, Alert } from '@mui/material';
import InsightCard from '../../components/analytics/InsightCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { insightService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import { AIInsight } from '../../types';

const InsightsPage: React.FC = () => {
  const { user } = useAuth();
  const studentId = user?.userId || 0;
  const [insights, setInsights] = useState<AIInsight[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (studentId) fetchInsights();
  }, [studentId]);

  const fetchInsights = async () => {
    setLoading(true);
    try {
      const res = await insightService.getStudentInsights(studentId);
      if (res.data?.success) setInsights(res.data.data || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load insights');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>AI Insights</Typography>

      {insights.length === 0 ? (
        <Alert severity="info">No insights available yet. Insights will be generated as you complete more exams.</Alert>
      ) : (
        insights.map((insight) => (
          <InsightCard key={insight.id} data={insight} />
        ))
      )}
    </Box>
  );
};

export default InsightsPage;
