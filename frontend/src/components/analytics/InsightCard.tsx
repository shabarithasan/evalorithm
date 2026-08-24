import React from 'react';
import { Card, CardContent, Typography, Box, Chip } from '@mui/material';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import StarIcon from '@mui/icons-material/Star';
import ErrorIcon from '@mui/icons-material/Error';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import { AIInsight, InsightTypeValue } from '../../types';

interface Props {
  data: AIInsight;
}

const insightConfig: Record<InsightTypeValue, { icon: React.ReactElement; color: string }> = {
  BEST_SUBJECT: { icon: <StarIcon />, color: '#4CAF50' },
  WEAKEST_SUBJECT: { icon: <ErrorIcon />, color: '#F44336' },
  FREQUENTLY_WRONG: { icon: <ErrorIcon />, color: '#FF9800' },
  IMPROVEMENT_TREND: { icon: <TrendingUpIcon />, color: '#2196F3' },
  LEARNING_CURVE: { icon: <AutoAwesomeIcon />, color: '#9C27B0' },
};

const insightLabels: Record<InsightTypeValue, string> = {
  BEST_SUBJECT: 'Best Subject',
  WEAKEST_SUBJECT: 'Weakest Subject',
  FREQUENTLY_WRONG: 'Frequently Wrong',
  IMPROVEMENT_TREND: 'Improvement Trend',
  LEARNING_CURVE: 'Learning Curve',
};

const InsightCard: React.FC<Props> = ({ data }) => {
  const config = insightConfig[data.insightType] || { icon: <AutoAwesomeIcon />, color: '#1565C0' };

  return (
    <Card sx={{ mb: 2 }}>
      <CardContent>
        <Box sx={{ display: 'flex', gap: 1.5, alignItems: 'flex-start' }}>
          <Box
            sx={{
              p: 1.5,
              borderRadius: 2,
              bgcolor: `${config.color}15`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: config.color,
              flexShrink: 0,
            }}
          >
            {React.cloneElement(config.icon, { sx: { fontSize: 28 } })}
          </Box>
          <Box sx={{ flex: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                {data.title}
              </Typography>
              <Chip
                label={insightLabels[data.insightType]}
                size="small"
                sx={{ height: 22, fontSize: '0.7rem', bgcolor: `${config.color}15`, color: config.color }}
              />
            </Box>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
              {data.description}
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Typography variant="h6" sx={{ fontWeight: 700, color: config.color }}>
                {data.value}%
              </Typography>
              {data.subjectName && (
                <Chip label={data.subjectName} size="small" variant="outlined" sx={{ height: 20, fontSize: '0.7rem' }} />
              )}
            </Box>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default InsightCard;
