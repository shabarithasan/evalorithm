import React from 'react';
import { Box, Typography, Card, CardContent } from '@mui/material';
import { Radar } from 'react-chartjs-2';
import { SubjectPerformanceItem } from '../../types';

interface Props {
  data: SubjectPerformanceItem[];
  title?: string;
}

const SubjectRadarChart: React.FC<Props> = ({ data, title = 'Subject Performance' }) => {
  const chartData = {
    labels: data.map((d) => d.subjectName),
    datasets: [
      {
        label: 'Accuracy %',
        data: data.map((d) => d.accuracy),
        backgroundColor: 'rgba(21, 101, 192, 0.2)',
        borderColor: '#1565C0',
        borderWidth: 2,
        pointBackgroundColor: '#1565C0',
        pointRadius: 4,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
    },
    scales: {
      r: {
        beginAtZero: true,
        max: 100,
        ticks: {
          stepSize: 20,
          callback: (v: any) => `${v}%`,
          backdropColor: 'transparent',
        },
        grid: { color: 'rgba(0,0,0,0.08)' },
        angleLines: { color: 'rgba(0,0,0,0.08)' },
        pointLabels: { font: { size: 12 } },
      },
    },
  };

  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Typography variant="h6" sx={{ mb: 2 }}>
          {title}
        </Typography>
        <Box sx={{ height: 320 }}>
          {data.length > 0 ? (
            <Radar data={chartData} options={options} />
          ) : (
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
              <Typography color="text.secondary">No subject data available</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default SubjectRadarChart;
