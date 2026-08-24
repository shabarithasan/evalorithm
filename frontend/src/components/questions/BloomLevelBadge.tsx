import React from 'react';
import { Chip } from '@mui/material';
import { BloomLevel } from '../../types';

interface BloomLevelBadgeProps {
  level: BloomLevel;
  size?: 'small' | 'medium';
}

const bloomConfig: Record<BloomLevel, { label: string; color: string; bgColor: string }> = {
  K1_REMEMBER: { label: 'Remember', color: '#5D4037', bgColor: '#EFEBE9' },
  K2_UNDERSTAND: { label: 'Understand', color: '#1565C0', bgColor: '#E3F2FD' },
  K3_APPLY: { label: 'Apply', color: '#2E7D32', bgColor: '#E8F5E9' },
  K4_ANALYZE: { label: 'Analyze', color: '#E65100', bgColor: '#FFF3E0' },
  K5_EVALUATE: { label: 'Evaluate', color: '#6A1B9A', bgColor: '#F3E5F5' },
  K6_CREATE: { label: 'Create', color: '#C62828', bgColor: '#FFEBEE' },
};

const BloomLevelBadge: React.FC<BloomLevelBadgeProps> = ({ level, size = 'small' }) => {
  const config = bloomConfig[level] || bloomConfig.K1_REMEMBER;
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

export default BloomLevelBadge;
