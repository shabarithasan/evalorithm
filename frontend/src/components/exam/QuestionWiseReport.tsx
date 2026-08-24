import React from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Box,
} from '@mui/material';

interface QuestionWiseReportProps {
  data: {
    questionNumber: number;
    questionType: string;
    difficulty: string;
    totalAttempts: number;
    correctPercentage: number;
    wrongPercentage: number;
    averageTime: number;
  }[];
}

const QuestionWiseReport: React.FC<QuestionWiseReportProps> = ({ data }) => {
  const getDifficultyColor = (diff: string): string => {
    switch (diff) {
      case 'EASY': return '#2E7D32';
      case 'MEDIUM': return '#E65100';
      case 'HARD': return '#C62828';
      case 'EXPERT': return '#7B1FA2';
      default: return '#757575';
    }
  };

  if (data.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography color="text.secondary">No question-wise data available</Typography>
      </Box>
    );
  }

  return (
    <TableContainer component={Paper} variant="outlined">
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell sx={{ fontWeight: 600 }}>#</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Type</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Difficulty</TableCell>
            <TableCell sx={{ fontWeight: 600 }} align="right">Attempts</TableCell>
            <TableCell sx={{ fontWeight: 600 }} align="right">Correct %</TableCell>
            <TableCell sx={{ fontWeight: 600 }} align="right">Wrong %</TableCell>
            <TableCell sx={{ fontWeight: 600 }} align="right">Avg Time (s)</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {data.map((row) => (
            <TableRow key={row.questionNumber} hover>
              <TableCell>{row.questionNumber}</TableCell>
              <TableCell>
                <Typography variant="body2" sx={{ textTransform: 'capitalize' }}>
                  {row.questionType.replace('_', ' ').toLowerCase()}
                </Typography>
              </TableCell>
              <TableCell>
                <Box
                  sx={{
                    display: 'inline-block',
                    px: 1,
                    py: 0.25,
                    borderRadius: 1,
                    bgcolor: `${getDifficultyColor(row.difficulty)}15`,
                    color: getDifficultyColor(row.difficulty),
                    fontSize: '0.75rem',
                    fontWeight: 600,
                  }}
                >
                  {row.difficulty}
                </Box>
              </TableCell>
              <TableCell align="right">{row.totalAttempts}</TableCell>
              <TableCell align="right">
                <Typography
                  variant="body2"
                  sx={{ color: row.correctPercentage >= 60 ? '#2E7D32' : '#C62828', fontWeight: 600 }}
                >
                  {row.correctPercentage.toFixed(1)}%
                </Typography>
              </TableCell>
              <TableCell align="right">
                <Typography
                  variant="body2"
                  sx={{ color: row.wrongPercentage > 40 ? '#C62828' : 'text.secondary', fontWeight: 600 }}
                >
                  {row.wrongPercentage.toFixed(1)}%
                </Typography>
              </TableCell>
              <TableCell align="right">{row.averageTime.toFixed(1)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default QuestionWiseReport;
