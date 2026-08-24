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
  LinearProgress,
  Chip,
} from '@mui/material';
import { Attainment } from '../../types';

interface AttainmentTableProps {
  data: Attainment[];
}

const AttainmentTable: React.FC<AttainmentTableProps> = ({ data }) => {
  const getProgressColor = (value: number, target: number): 'success' | 'warning' | 'error' => {
    if (value >= target) return 'success';
    if (value >= target * 0.75) return 'warning';
    return 'error';
  };

  if (data.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography color="text.secondary">No attainment data available.</Typography>
      </Box>
    );
  }

  return (
    <TableContainer component={Paper} variant="outlined">
      <Table size="small">
        <TableHead>
          <TableRow sx={{ backgroundColor: 'primary.main' }}>
            <TableCell sx={{ color: '#fff', fontWeight: 600 }}>CO Code</TableCell>
            <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Description</TableCell>
            <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="center">Target %</TableCell>
            <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="center">Actual %</TableCell>
            <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="center">Direct %</TableCell>
            <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="center">Indirect %</TableCell>
            <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="center">Progress</TableCell>
            <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="center">Status</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {data.map((row) => (
            <TableRow key={row.id} hover>
              <TableCell>
                <Typography variant="body2" fontWeight={600}>{row.coCode}</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="body2" sx={{ maxWidth: 250 }}>{row.coDescription}</Typography>
              </TableCell>
              <TableCell align="center">{row.targetAttainment.toFixed(1)}</TableCell>
              <TableCell align="center">
                <Typography variant="body2" fontWeight={600}>
                  {row.actualAttainment.toFixed(1)}
                </Typography>
              </TableCell>
              <TableCell align="center">{row.directAttainment.toFixed(1)}</TableCell>
              <TableCell align="center">{row.indirectAttainment.toFixed(1)}</TableCell>
              <TableCell align="center" sx={{ minWidth: 150 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <LinearProgress
                    variant="determinate"
                    value={Math.min(row.actualAttainment, 100)}
                    color={getProgressColor(row.actualAttainment, row.targetAttainment)}
                    sx={{ flex: 1, height: 8, borderRadius: 4 }}
                  />
                  <Typography variant="caption" sx={{ minWidth: 30 }}>
                    {row.actualAttainment.toFixed(0)}%
                  </Typography>
                </Box>
              </TableCell>
              <TableCell align="center">
                <Chip
                  label={row.isAchieved ? 'Achieved' : 'Not Achieved'}
                  color={row.isAchieved ? 'success' : 'error'}
                  size="small"
                />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default AttainmentTable;
