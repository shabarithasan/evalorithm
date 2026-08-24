import React from 'react';
import { Box, Typography, Card, CardContent } from '@mui/material';
import { PerformanceHeatmap as PerformanceHeatmapType } from '../../types';

interface Props {
  data: PerformanceHeatmapType[];
  title?: string;
}

const getHeatColor = (accuracy: number): string => {
  if (accuracy >= 80) return 'rgba(76, 175, 80, 0.7)';
  if (accuracy >= 60) return 'rgba(33, 150, 243, 0.7)';
  if (accuracy >= 40) return 'rgba(255, 152, 0, 0.7)';
  return 'rgba(244, 67, 54, 0.7)';
};

const PerformanceHeatmap: React.FC<Props> = ({ data, title = 'Performance Heatmap' }) => {
  const subjects = Array.from(new Set(data.map((d) => d.subject)));
  const topics = Array.from(new Set(data.map((d) => d.topic)));

  const lookup: Record<string, Record<string, number>> = {};
  data.forEach((d) => {
    if (!lookup[d.subject]) lookup[d.subject] = {};
    lookup[d.subject][d.topic] = d.accuracy;
  });

  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Typography variant="h6" sx={{ mb: 2 }}>
          {title}
        </Typography>
        {data.length === 0 ? (
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: 200 }}>
            <Typography color="text.secondary">No heatmap data available</Typography>
          </Box>
        ) : (
          <Box sx={{ overflowX: 'auto' }}>
            <Box component="table" sx={{ width: '100%', borderCollapse: 'collapse' }}>
              <Box component="thead">
                <Box component="tr">
                  <Box component="th" sx={{ p: 1, textAlign: 'left', fontWeight: 600, minWidth: 120, fontSize: '0.8rem' }}>
                    Subject / Topic
                  </Box>
                  {topics.map((t) => (
                    <Box key={t} component="th" sx={{ p: 1, textAlign: 'center', fontWeight: 600, minWidth: 100, fontSize: '0.75rem' }}>
                      {t}
                    </Box>
                  ))}
                </Box>
              </Box>
              <Box component="tbody">
                {subjects.map((s) => (
                  <Box component="tr" key={s}>
                    <Box component="td" sx={{ p: 1, fontWeight: 500, fontSize: '0.85rem', whiteSpace: 'nowrap' }}>
                      {s}
                    </Box>
                    {topics.map((t) => {
                      const val = lookup[s]?.[t];
                      return (
                        <Box
                          key={t}
                          component="td"
                          sx={{
                            p: 1,
                            textAlign: 'center',
                            backgroundColor: val !== undefined ? getHeatColor(val) : '#f5f5f5',
                            color: val !== undefined && val < 40 ? '#fff' : 'inherit',
                            fontWeight: 600,
                            fontSize: '0.8rem',
                            borderRadius: 1,
                            m: 0.25,
                          }}
                        >
                          {val !== undefined ? `${val}%` : '—'}
                        </Box>
                      );
                    })}
                  </Box>
                ))}
              </Box>
            </Box>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default PerformanceHeatmap;
