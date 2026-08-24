import React from 'react';
import { Box, Card, CardContent, Typography, Chip } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';

interface ResultCardProps {
  totalObtained: number;
  totalPossible: number;
  percentage: number;
  grade: string;
  isPassed: boolean;
  correctAnswers: number;
  wrongAnswers: number;
  skippedQuestions: number;
  timeTakenMinutes: number;
}

const ResultCard: React.FC<ResultCardProps> = ({
  totalObtained,
  totalPossible,
  percentage,
  grade,
  isPassed,
  correctAnswers,
  wrongAnswers,
  skippedQuestions,
  timeTakenMinutes,
}) => {
  const getGradeColor = (pct: number): string => {
    if (pct >= 90) return '#2E7D32';
    if (pct >= 80) return '#1565C0';
    if (pct >= 70) return '#7B1FA2';
    if (pct >= 60) return '#E65100';
    if (pct >= 50) return '#F57F17';
    return '#C62828';
  };

  const gradeColor = getGradeColor(percentage);

  return (
    <Card sx={{ maxWidth: 500, mx: 'auto' }}>
      <CardContent>
        <Box sx={{ textAlign: 'center', mb: 3 }}>
          <Box
            sx={{
              width: 120,
              height: 120,
              borderRadius: '50%',
              border: `4px solid ${gradeColor}`,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              mx: 'auto',
              mb: 2,
            }}
          >
            <Typography variant="h3" sx={{ fontWeight: 700, color: gradeColor, lineHeight: 1 }}>
              {Math.round(percentage)}%
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, mb: 1 }}>
            {isPassed ? (
              <CheckCircleIcon sx={{ color: '#2E7D32', fontSize: 28 }} />
            ) : (
              <CancelIcon sx={{ color: '#C62828', fontSize: 28 }} />
            )}
            <Typography variant="h5" sx={{ fontWeight: 600, color: isPassed ? '#2E7D32' : '#C62828' }}>
              {isPassed ? 'PASSED' : 'FAILED'}
            </Typography>
          </Box>

          <Chip
            label={`Grade: ${grade}`}
            sx={{
              bgcolor: `${gradeColor}15`,
              color: gradeColor,
              fontWeight: 600,
              fontSize: '0.9rem',
            }}
          />
        </Box>

        <Box sx={{ textAlign: 'center', mb: 3 }}>
          <Typography variant="h4" sx={{ fontWeight: 700, color: 'primary.main' }}>
            {totalObtained} / {totalPossible}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Marks Obtained
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'space-around', py: 2, borderTop: '1px solid', borderColor: 'grey.200' }}>
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="h6" sx={{ fontWeight: 600, color: '#2E7D32' }}>
              {correctAnswers}
            </Typography>
            <Typography variant="caption" color="text.secondary">Correct</Typography>
          </Box>
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="h6" sx={{ fontWeight: 600, color: '#C62828' }}>
              {wrongAnswers}
            </Typography>
            <Typography variant="caption" color="text.secondary">Wrong</Typography>
          </Box>
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="h6" sx={{ fontWeight: 600, color: '#757575' }}>
              {skippedQuestions}
            </Typography>
            <Typography variant="caption" color="text.secondary">Skipped</Typography>
          </Box>
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="h6" sx={{ fontWeight: 600, color: '#1565C0' }}>
              {timeTakenMinutes} min
            </Typography>
            <Typography variant="caption" color="text.secondary">Time Taken</Typography>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ResultCard;
