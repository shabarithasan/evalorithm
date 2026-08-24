import React from 'react';
import { Box, Typography, Card, CardContent } from '@mui/material';
import { Line } from 'react-chartjs-2';

interface Props {
  data: { month: string; count: number }[];
  title?: string;
}

const GrowthLineChart: React.FC<Props> = ({ data, title = 'Student Growth' }) => {
  const chartData = {
    labels: data.map((d) => d.month),
    datasets: [
      {
        label: 'Students',
        data: data.map((d) => d.count),
        borderColor: '#2E7D32',
        backgroundColor: (ctx: any) => {
          const chart = ctx.chart;
          const { ctx: c, chartArea } = chart;
          if (!chartArea) return 'rgba(46,125,50,0.1)';
          const gradient = c.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
          gradient.addColorStop(0, 'rgba(46,125,50,0.3)');
          gradient.addColorStop(1, 'rgba(46,125,50,0.02)');
          return gradient;
        },
        fill: true,
        tension: 0.4,
        pointRadius: 4,
        pointBackgroundColor: '#2E7D32',
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
          {data.length > 0 ? (
            <Line data={chartData} options={options} />
          ) : (
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
              <Typography color="text.secondary">No growth data available</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default GrowthLineChart;
