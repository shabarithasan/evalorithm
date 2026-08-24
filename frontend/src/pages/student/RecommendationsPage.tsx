import React, { useState, useEffect } from 'react';
import { Box, Typography, Chip, CircularProgress, Alert } from '@mui/material';
import RecommendationCard from '../../components/analytics/RecommendationCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { recommendationService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import { Recommendation, LearningPriorityLevel } from '../../types';

const RecommendationsPage: React.FC = () => {
  const { user } = useAuth();
  const studentId = user?.userId || 0;
  const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
  const [filter, setFilter] = useState<LearningPriorityLevel | 'ALL'>('ALL');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (studentId) fetchRecommendations();
  }, [studentId]);

  const fetchRecommendations = async () => {
    setLoading(true);
    try {
      const res = await recommendationService.getStudentRecommendations(studentId);
      if (res.data?.success) setRecommendations(res.data.data || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load recommendations');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkRead = async (id: number) => {
    try {
      await recommendationService.markAsRead(id);
      setRecommendations((prev) => prev.map((r) => r.id === id ? { ...r, isRead: true } : r));
    } catch { /* empty */ }
  };

  const handleAccept = async (id: number) => {
    try {
      await recommendationService.accept(id);
      setRecommendations((prev) => prev.map((r) => r.id === id ? { ...r, isRead: true } : r));
    } catch { /* empty */ }
  };

  if (loading) return <LoadingScreen />;
  if (error) return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;

  const priorities: (LearningPriorityLevel | 'ALL')[] = ['ALL', 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW'];
  const filtered = filter === 'ALL' ? recommendations : recommendations.filter((r) => r.priority === filter);

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>Recommendations</Typography>

      <Box sx={{ display: 'flex', gap: 1, mb: 3, flexWrap: 'wrap' }}>
        {priorities.map((p) => (
          <Chip
            key={p}
            label={p === 'ALL' ? 'All' : p}
            onClick={() => setFilter(p)}
            color={filter === p ? 'primary' : 'default'}
            variant={filter === p ? 'filled' : 'outlined'}
            sx={{ cursor: 'pointer' }}
          />
        ))}
      </Box>

      {filtered.length === 0 ? (
        <Alert severity="info">No recommendations available</Alert>
      ) : (
        filtered.map((rec) => (
          <RecommendationCard
            key={rec.id}
            data={rec}
            onMarkRead={handleMarkRead}
            onAccept={handleAccept}
          />
        ))
      )}
    </Box>
  );
};

export default RecommendationsPage;
