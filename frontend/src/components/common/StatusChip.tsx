import React from 'react';
import { Chip, ChipProps } from '@mui/material';
import { Status } from '../../types';

interface StatusChipProps {
  status: Status;
  size?: ChipProps['size'];
}

const StatusChip: React.FC<StatusChipProps> = ({ status, size = 'small' }) => {
  return (
    <Chip
      label={status}
      size={size}
      color={status === 'ACTIVE' ? 'success' : 'error'}
      variant="outlined"
      sx={{ fontWeight: 600 }}
    />
  );
};

export default StatusChip;
