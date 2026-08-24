import React from 'react';
import { Card, CardContent, Typography, Box, Chip, LinearProgress } from '@mui/material';
import { Prediction, RiskLevelType } from '../../types';

interface Props {
  data: Prediction;
}

const riskColors: Record<RiskLevelType, 'success' | 'warning' | 'error' | 'error'> = {
  LOW: 'success',
  MEDIUM: 'warning',
  HIGH: 'error',
  VERY_HIGH: 'error',
};

const riskBg: Record<RiskLevelType, string> = {
  LOW: 'rgba(76,175,80,0.1)',
  MEDIUM: 'rgba(255,152,0,0.1)',
  HIGH: 'rgba(244,67,54,0.1)',
  VERY_HIGH: 'rgba(211,47,47,0.1)',
};

const PredictionCard: React.FC<Props> = ({ data }) => {
  const probPercent = Math.round(data.passProbability * 100);

  return (
    <Card sx={{ mb: 2 }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 600 }}>
              {data.subjectName}
            </Typography>
            <Chip label={data.riskLevel.replace('_', ' ')} color={riskColors[data.riskLevel]} size="small" sx={{ mt: 0.5 }} />
          </Box>
          <Box sx={{ textAlign: 'center' }}>
            <Box
              sx={{
                width: 80,
                height: 80,
                borderRadius: '50%',
                border: `4px solid ${riskColors[data.riskLevel] === 'success' ? '#4CAF50' : riskColors[data.riskLevel] === 'warning' ? '#FF9800' : '#F44336'}`,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                bgcolor: riskBg[data.riskLevel],
              }}
            >
              <Typography variant="h5" sx={{ fontWeight: 700, lineHeight: 1 }}>
                {probPercent}%
              </Typography>
              <Typography variant="caption" sx={{ fontSize: '0.6rem', lineHeight: 1 }}>
                pass
              </Typography>
            </Box>
          </Box>
        </Box>

        <Box sx={{ mb: 2 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
            <Typography variant="body2" color="text.secondary">Predicted Marks</Typography>
            <Typography variant="body2" sx={{ fontWeight: 600 }}>{data.predictedMarks}</Typography>
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
            <Typography variant="body2" color="text.secondary">Predicted Grade</Typography>
            <Typography variant="body2" sx={{ fontWeight: 600 }}>{data.predictedGrade}</Typography>
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
            <Typography variant="body2" color="text.secondary">Confidence</Typography>
            <Typography variant="body2" sx={{ fontWeight: 600 }}>{Math.round(data.confidenceLevel * 100)}%</Typography>
          </Box>
          <LinearProgress
            variant="determinate"
            value={probPercent}
            color={riskColors[data.riskLevel]}
            sx={{ height: 8, borderRadius: 4, bgcolor: 'grey.200' }}
          />
        </Box>

        <Box sx={{ p: 1.5, bgcolor: 'grey.50', borderRadius: 2 }}>
          <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600, textTransform: 'uppercase' }}>
            Suggested Improvement
          </Typography>
          <Typography variant="body2" sx={{ mt: 0.5 }}>
            {data.suggestedImprovement}
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
};

export default PredictionCard;
