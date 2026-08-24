import React from 'react';
import { Card, CardContent, Box, Typography } from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import QuizIcon from '@mui/icons-material/Quiz';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import { QuestionStatistics } from '../../types';

interface QuestionStatsCardProps {
  statistics: QuestionStatistics;
}

const QuestionStatsCard: React.FC<QuestionStatsCardProps> = ({ statistics }) => {
  const stats = [
    { label: 'Views', value: statistics.viewCount, icon: <VisibilityIcon />, color: '#1565C0' },
    { label: 'Used In Exams', value: statistics.usageCount, icon: <QuizIcon />, color: '#7B1FA2' },
    { label: 'Correct', value: `${statistics.correctPercentage.toFixed(1)}%`, icon: <CheckCircleIcon />, color: '#2E7D32' },
    { label: 'Wrong', value: `${statistics.wrongPercentage.toFixed(1)}%`, icon: <CancelIcon />, color: '#C62828' },
  ];

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" sx={{ mb: 2 }}>Statistics</Typography>
        <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 2 }}>
          {stats.map((stat) => (
            <Box
              key={stat.label}
              sx={{
                p: 2,
                borderRadius: 2,
                backgroundColor: `${stat.color}08`,
                border: `1px solid ${stat.color}20`,
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                {React.cloneElement(stat.icon, { sx: { fontSize: 18, color: stat.color } })}
                <Typography variant="caption" color="text.secondary">{stat.label}</Typography>
              </Box>
              <Typography variant="h5" sx={{ fontWeight: 700, color: stat.color }}>{stat.value}</Typography>
            </Box>
          ))}
        </Box>
      </CardContent>
    </Card>
  );
};

export default QuestionStatsCard;
