import React from 'react';
import { Card, CardContent, Box, Typography } from '@mui/material';
import { SvgIconProps } from '@mui/material/SvgIcon';

interface StatsCardProps {
  title: string;
  value: number | string;
  icon: React.ReactElement<SvgIconProps>;
  color?: string;
}

const StatsCard: React.FC<StatsCardProps> = ({
  title,
  value,
  icon,
  color = '#1565C0',
}) => {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <Box>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 1, fontWeight: 500 }}>
              {title}
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 700 }}>
              {value}
            </Typography>
          </Box>
          <Box
            sx={{
              p: 1.5,
              borderRadius: 2,
              backgroundColor: `${color}15`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            {React.cloneElement(icon, {
              sx: { fontSize: 28, color },
            })}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default StatsCard;
