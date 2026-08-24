import React from 'react';
import { Box, Typography, Paper } from '@mui/material';
import { AnswerStatusType, StudentAnswer } from '../../types';

interface QuestionPaletteProps {
  totalQuestions: number;
  currentIndex: number;
  answers: Record<number, StudentAnswer>;
  onJumpToQuestion: (index: number) => void;
}

const statusColors: Record<AnswerStatusType, string> = {
  ANSWERED: '#4CAF50',
  NOT_ANSWERED: '#F44336',
  MARKED_FOR_REVIEW: '#9C27B0',
  ANSWERED_MARKED: '#2196F3',
};

const legendItems: { label: string; color: string }[] = [
  { label: 'Answered', color: '#4CAF50' },
  { label: 'Not Answered', color: '#F44336' },
  { label: 'Marked for Review', color: '#9C27B0' },
  { label: 'Answered & Marked', color: '#2196F3' },
  { label: 'Not Visited', color: '#9E9E9E' },
];

const QuestionPalette: React.FC<QuestionPaletteProps> = ({
  totalQuestions,
  currentIndex,
  answers,
  onJumpToQuestion,
}) => {
  const getButtonColor = (index: number): { bg: string; border: string; text: string } => {
    const questionIndex = index + 1;
    const answer = answers[questionIndex];

    if (!answer || answer.answerStatus === 'NOT_ANSWERED') {
      if (index === currentIndex) return { bg: '#F4433615', border: '#F44336', text: '#F44336' };
      return { bg: '#F44336', border: '#F44336', text: '#fff' };
    }

    if (answer.answerStatus === 'ANSWERED_MARKED') {
      if (index === currentIndex) return { bg: '#2196F315', border: '#2196F3', text: '#2196F3' };
      return { bg: '#2196F3', border: '#2196F3', text: '#fff' };
    }

    if (answer.answerStatus === 'MARKED_FOR_REVIEW') {
      if (index === currentIndex) return { bg: '#9C27B015', border: '#9C27B0', text: '#9C27B0' };
      return { bg: '#9C27B0', border: '#9C27B0', text: '#fff' };
    }

    if (answer.answerStatus === 'ANSWERED') {
      if (index === currentIndex) return { bg: '#4CAF5015', border: '#4CAF50', text: '#4CAF50' };
      return { bg: '#4CAF50', border: '#4CAF50', text: '#fff' };
    }

    if (index === currentIndex) return { bg: '#9E9E9E15', border: '#1565C0', text: '#1565C0' };
    return { bg: '#9E9E9E', border: '#9E9E9E', text: '#fff' };
  };

  return (
    <Paper variant="outlined" sx={{ p: 2 }}>
      <Typography variant="subtitle2" sx={{ mb: 1.5, fontWeight: 600 }}>
        Question Palette
      </Typography>

      <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: 0.75, mb: 2 }}>
        {Array.from({ length: totalQuestions }, (_, i) => {
          const colors = getButtonColor(i);
          const isCurrent = i === currentIndex;

          return (
            <Box
              key={i}
              onClick={() => onJumpToQuestion(i)}
              sx={{
                width: 36,
                height: 36,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                borderRadius: 1,
                cursor: 'pointer',
                fontWeight: 600,
                fontSize: '0.8rem',
                bgcolor: colors.bg,
                color: colors.text,
                border: isCurrent ? `2px solid ${colors.border}` : '1px solid transparent',
                '&:hover': {
                  opacity: 0.8,
                  transform: 'scale(1.05)',
                },
                transition: 'all 0.15s ease',
              }}
            >
              {i + 1}
            </Box>
          );
        })}
      </Box>

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
        {legendItems.map((item) => (
          <Box key={item.label} sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box
              sx={{
                width: 14,
                height: 14,
                borderRadius: 0.5,
                bgcolor: item.color,
                flexShrink: 0,
              }}
            />
            <Typography variant="caption" color="text.secondary">
              {item.label}
            </Typography>
          </Box>
        ))}
      </Box>
    </Paper>
  );
};

export default QuestionPalette;
