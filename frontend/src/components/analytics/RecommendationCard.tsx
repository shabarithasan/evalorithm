import React from 'react';
import { Card, CardContent, Typography, Box, Chip, IconButton } from '@mui/material';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import LightbulbIcon from '@mui/icons-material/Lightbulb';
import SchoolIcon from '@mui/icons-material/School';
import QuizIcon from '@mui/icons-material/Quiz';
import DateRangeIcon from '@mui/icons-material/DateRange';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import { Recommendation, RecommendationTypeValue, LearningPriorityLevel } from '../../types';

interface Props {
  data: Recommendation;
  onMarkRead?: (id: number) => void;
  onAccept?: (id: number) => void;
}

const typeIcons: Record<RecommendationTypeValue, React.ReactElement> = {
  WEAK_TOPIC: <SchoolIcon />,
  REVISION: <DateRangeIcon />,
  PRACTICE_QUESTIONS: <QuizIcon />,
  MOCK_TEST: <QuizIcon />,
  STUDY_PLAN: <TrendingUpIcon />,
  PRIORITY_TOPIC: <LightbulbIcon />,
};

const typeLabels: Record<RecommendationTypeValue, string> = {
  WEAK_TOPIC: 'Weak Topic',
  REVISION: 'Revision',
  PRACTICE_QUESTIONS: 'Practice',
  MOCK_TEST: 'Mock Test',
  STUDY_PLAN: 'Study Plan',
  PRIORITY_TOPIC: 'Priority Topic',
};

const priorityColors: Record<LearningPriorityLevel, 'error' | 'warning' | 'info' | 'default'> = {
  CRITICAL: 'error',
  HIGH: 'warning',
  MEDIUM: 'info',
  LOW: 'default',
};

const RecommendationCard: React.FC<Props> = ({ data, onMarkRead, onAccept }) => {
  return (
    <Card
      sx={{
        mb: 2,
        borderLeft: 4,
        borderColor: data.priority === 'CRITICAL' ? 'error.main' : data.priority === 'HIGH' ? 'warning.main' : 'primary.main',
        opacity: data.isRead ? 0.7 : 1,
      }}
    >
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <Box sx={{ display: 'flex', gap: 1.5, flex: 1 }}>
            <Box sx={{ color: 'primary.main', mt: 0.5 }}>
              {typeIcons[data.type] || <LightbulbIcon />}
            </Box>
            <Box sx={{ flex: 1 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                  {data.title}
                </Typography>
                <Chip label={typeLabels[data.type]} size="small" variant="outlined" sx={{ height: 22, fontSize: '0.7rem' }} />
                <Chip label={data.priority} size="small" color={priorityColors[data.priority]} sx={{ height: 22, fontSize: '0.7rem' }} />
              </Box>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                {data.description}
              </Typography>
              <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                {data.subjectName && (
                  <Chip label={data.subjectName} size="small" sx={{ height: 20, fontSize: '0.7rem' }} />
                )}
                {data.topicName && (
                  <Chip label={data.topicName} size="small" variant="outlined" sx={{ height: 20, fontSize: '0.7rem' }} />
                )}
                {data.unitName && (
                  <Chip label={data.unitName} size="small" variant="outlined" sx={{ height: 20, fontSize: '0.7rem' }} />
                )}
              </Box>
            </Box>
          </Box>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5, ml: 1 }}>
            {!data.isRead && onMarkRead && (
              <IconButton size="small" onClick={() => onMarkRead(data.id)} title="Mark as read">
                <CheckCircleOutlineIcon fontSize="small" />
              </IconButton>
            )}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default RecommendationCard;
