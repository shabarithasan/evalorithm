import React from 'react';
import { Card, CardContent, Typography, Box } from '@mui/material';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import { LeaderboardItem } from '../../types';

interface Props {
  data: LeaderboardItem[];
  title?: string;
}

const rankColors: Record<number, string> = {
  1: '#FFD700',
  2: '#C0C0C0',
  3: '#CD7F32',
};

const LeaderboardTable: React.FC<Props> = ({ data, title = 'Leaderboard' }) => {
  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
          <EmojiEventsIcon sx={{ color: '#FFD700' }} />
          <Typography variant="h6">{title}</Typography>
        </Box>
        {data.length === 0 ? (
          <Typography color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
            No leaderboard data available
          </Typography>
        ) : (
          <Box sx={{ overflowX: 'auto' }}>
            <Box component="table" sx={{ width: '100%', borderCollapse: 'collapse' }}>
              <Box component="thead">
                <Box component="tr" sx={{ borderBottom: '2px solid', borderColor: 'grey.200' }}>
                  {['Rank', 'Name', 'Department', 'Score', 'Accuracy', 'Exams'].map((h) => (
                    <Box
                      key={h}
                      component="th"
                      sx={{
                        p: 1.5,
                        textAlign: h === 'Rank' || h === 'Score' || h === 'Accuracy' || h === 'Exams' ? 'center' : 'left',
                        fontWeight: 600,
                        fontSize: '0.8rem',
                        color: 'text.secondary',
                        textTransform: 'uppercase',
                        letterSpacing: 0.5,
                      }}
                    >
                      {h}
                    </Box>
                  ))}
                </Box>
              </Box>
              <Box component="tbody">
                {data.map((item) => (
                  <Box
                    component="tr"
                    key={item.rank}
                    sx={{
                      borderBottom: '1px solid',
                      borderColor: 'grey.100',
                      backgroundColor: rankColors[item.rank] ? `${rankColors[item.rank]}15` : 'transparent',
                      '&:hover': { bgcolor: 'grey.50' },
                    }}
                  >
                    <Box component="td" sx={{ p: 1.5, textAlign: 'center' }}>
                      <Box
                        sx={{
                          display: 'inline-flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          width: 32,
                          height: 32,
                          borderRadius: '50%',
                          bgcolor: rankColors[item.rank] || 'grey.200',
                          color: item.rank <= 3 ? '#fff' : 'text.primary',
                          fontWeight: 700,
                          fontSize: '0.85rem',
                        }}
                      >
                        {item.rank}
                      </Box>
                    </Box>
                    <Box component="td" sx={{ p: 1.5, fontWeight: 500 }}>
                      {item.studentName || item.departmentName || '—'}
                    </Box>
                    <Box component="td" sx={{ p: 1.5, color: 'text.secondary' }}>
                      {item.departmentName || '—'}
                    </Box>
                    <Box component="td" sx={{ p: 1.5, textAlign: 'center', fontWeight: 600, color: 'primary.main' }}>
                      {item.score}
                    </Box>
                    <Box component="td" sx={{ p: 1.5, textAlign: 'center' }}>
                      {item.accuracy.toFixed(1)}%
                    </Box>
                    <Box component="td" sx={{ p: 1.5, textAlign: 'center' }}>
                      {item.totalExams}
                    </Box>
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

export default LeaderboardTable;
