import React from 'react';
import { Box, Typography, Card, CardContent } from '@mui/material';
import { Bar } from 'react-chartjs-2';

interface Props {
  data: Record<string, number>;
  title?: string;
}

const COLORS = ['#4CAF50', '#2196F3', '#FF9800', '#F44336'];
const LABELS_MAP: Record<string, string> = {
  EASY: 'Easy',
  MEDIUM: 'Medium',
  HARD: 'Hard',
  EXPERT: 'Expert',
};

const DifficultyBarChart: React.FC<Props> = ({ data, title = 'Difficulty Performance' }) => {
  const keys = Object.keys(data);
  const chartData = {
    labels: keys.map((k) => LABELS_MAP[k] || k),
    datasets: [
      {
        label: 'Accuracy %',
        data: keys.map((k) => data[k]),
        backgroundColor: keys.map((_, i) => COLORS[i % COLORS.length] + 'CC'),
        borderColor: keys.map((_, i) => COLORS[i % COLORS.length]),
        borderWidth: 1,
        borderRadius: 6,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        ticks: { callback: (v: any) => `${v}%` },
        grid: { color: 'rgba(0,0,0,0.05)' },
      },
      x: { grid: { display: false } },
    },
  };

  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Typography variant="h6" sx={{ mb: 2 }}>
          {title}
        </Typography>
        <Box sx={{ height: 300 }}>
          {keys.length > 0 ? (
            <Bar data={chartData} options={options} />
          ) : (
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
              <Typography color="text.secondary">No difficulty data available</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default DifficultyBarChart;
