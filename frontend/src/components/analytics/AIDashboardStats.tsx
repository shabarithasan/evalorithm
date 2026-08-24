import React from 'react';
import { Grid, Card, CardContent, Typography, Box } from '@mui/material';
import SmartToyIcon from '@mui/icons-material/SmartToy';
import PsychologyIcon from '@mui/icons-material/Psychology';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import LightbulbIcon from '@mui/icons-material/Lightbulb';
import StatsCard from '../common/StatsCard';
import { AIDashboardData } from '../../types';

interface Props {
  data: AIDashboardData;
}

const AIDashboardStats: React.FC<Props> = ({ data }) => {
  return (
    <Grid container spacing={3} sx={{ mb: 4 }}>
      <Grid item xs={12} sm={6} md={4} lg={2}>
        <StatsCard title="AI Questions" value={data.aiGeneratedQuestions} icon={<SmartToyIcon />} color="#1565C0" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2}>
        <StatsCard title="Adaptive Exams" value={data.adaptiveExams} icon={<PsychologyIcon />} color="#9C27B0" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2}>
        <StatsCard title="Avg Performance" value={`${data.studentPerformance}%`} icon={<TrendingUpIcon />} color="#2E7D32" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2}>
        <StatsCard title="Weak Topics" value={data.weakTopicsCount} icon={<WarningAmberIcon />} color="#D32F2F" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2}>
        <StatsCard title="Strong Topics" value={data.strongTopicsCount} icon={<CheckCircleIcon />} color="#4CAF50" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2}>
        <StatsCard title="Recommendations" value={data.recommendationsCount} icon={<LightbulbIcon />} color="#FF9800" />
      </Grid>
    </Grid>
  );
};

export default AIDashboardStats;
