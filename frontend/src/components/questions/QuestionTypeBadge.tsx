import React from 'react';
import { Chip } from '@mui/material';
import { QuestionType } from '../../types';

interface QuestionTypeBadgeProps {
  type: QuestionType;
  size?: 'small' | 'medium';
}

const typeConfig: Record<QuestionType, { label: string; color: string; bgColor: string }> = {
  MCQ: { label: 'MCQ', color: '#1565C0', bgColor: '#E3F2FD' },
  TRUE_FALSE: { label: 'True/False', color: '#2E7D32', bgColor: '#E8F5E9' },
  MATCH_FOLLOWING: { label: 'Match', color: '#E65100', bgColor: '#FFF3E0' },
  FILL_BLANKS: { label: 'Fill Blanks', color: '#7B1FA2', bgColor: '#F3E5F5' },
  ASSERTION_REASON: { label: 'Assert/Reason', color: '#00838F', bgColor: '#E0F7FA' },
  DESCRIPTIVE: { label: 'Descriptive', color: '#616161', bgColor: '#F5F5F5' },
  CASE_STUDY: { label: 'Case Study', color: '#283593', bgColor: '#E8EAF6' },
  PROGRAMMING: { label: 'Programming', color: '#1B5E20', bgColor: '#E8F5E9' },
};

const QuestionTypeBadge: React.FC<QuestionTypeBadgeProps> = ({ type, size = 'small' }) => {
  const config = typeConfig[type] || typeConfig.MCQ;
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

export default QuestionTypeBadge;
