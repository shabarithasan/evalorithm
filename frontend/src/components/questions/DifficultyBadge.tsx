import React from 'react';
import { Chip } from '@mui/material';
import { QuestionDifficulty } from '../../types';

interface DifficultyBadgeProps {
  difficulty: QuestionDifficulty;
  size?: 'small' | 'medium';
}

const difficultyConfig: Record<QuestionDifficulty, { label: string; color: string; bgColor: string }> = {
  EASY: { label: 'Easy', color: '#2E7D32', bgColor: '#E8F5E9' },
  MEDIUM: { label: 'Medium', color: '#E65100', bgColor: '#FFF3E0' },
  HARD: { label: 'Hard', color: '#C62828', bgColor: '#FFEBEE' },
  EXPERT: { label: 'Expert', color: '#4A148C', bgColor: '#F3E5F5' },
};

const DifficultyBadge: React.FC<DifficultyBadgeProps> = ({ difficulty, size = 'small' }) => {
  const config = difficultyConfig[difficulty] || difficultyConfig.EASY;
  return (
    <Chip
      label={config.label}
      size={size}
      sx={{
        backgroundColor: config.bgColor,
        color: config.color,
        fontWeight: 600,
        fontSize: size === 'small' ? '0.7rem' : '0.8rem',
      }}
    />
  );
};

export default DifficultyBadge;
