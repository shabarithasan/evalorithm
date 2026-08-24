import React, { useState, useEffect } from 'react';
import { Box, Typography, Alert, Button, Grid } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import PredictionCard from '../../components/analytics/PredictionCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { predictionService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import { Prediction } from '../../types';

const PredictionsPage: React.FC = () => {
  const { user } = useAuth();
  const studentId = user?.userId || 0;
  const [predictions, setPredictions] = useState<Prediction[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (studentId) fetchPredictions();
  }, [studentId]);

  const fetchPredictions = async () => {
    setLoading(true);
    try {
      const res = await predictionService.getStudentPredictions(studentId);
      if (res.data?.success) setPredictions(res.data.data || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load predictions');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerate = async () => {
    setGenerating(true);
    setError('');
    try {
      const res = await predictionService.generate(studentId);
      if (res.data?.success) setPredictions(res.data.data || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to generate predictions');
    } finally {
      setGenerating(false);
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Predictions</Typography>
        <Button
          variant="contained"
          startIcon={generating ? undefined : <RefreshIcon />}
          onClick={handleGenerate}
          disabled={generating}
        >
          {generating ? 'Generating...' : 'Generate Predictions'}
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {predictions.length === 0 ? (
        <Alert severity="info">
          No predictions available yet. Click "Generate Predictions" to analyze your performance and get predictions.
        </Alert>
      ) : (
        <Grid container spacing={3}>
          {predictions.map((pred) => (
            <Grid item xs={12} sm={6} md={4} key={pred.id}>
              <PredictionCard data={pred} />
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};

export default PredictionsPage;
