import React from 'react';
import { Box, Typography, Card, CardContent } from '@mui/material';
import { Line } from 'react-chartjs-2';
import { AccuracyTrend } from '../../types';

interface Props {
  data: AccuracyTrend[];
  title?: string;
}

const PerformanceLineChart: React.FC<Props> = ({ data, title = 'Accuracy Trend' }) => {
  const chartData = {
    labels: data.map((d) => d.date),
    datasets: [
      {
        label: 'Accuracy %',
        data: data.map((d) => d.accuracy),
        borderColor: '#1565C0',
        backgroundColor: (ctx: any) => {
          const chart = ctx.chart;
          const { ctx: c, chartArea } = chart;
          if (!chartArea) return 'rgba(21,101,192,0.1)';
          const gradient = c.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
          gradient.addColorStop(0, 'rgba(21,101,192,0.3)');
          gradient.addColorStop(1, 'rgba(21,101,192,0.02)');
          return gradient;
        },
        fill: true,
        tension: 0.4,
        pointRadius: 3,
        pointBackgroundColor: '#1565C0',
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: false },
      tooltip: {
        callbacks: {
          label: (item: any) => `Accuracy: ${item.parsed.y}%`,
        },
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        ticks: { callback: (v: any) => `${v}%` },
        grid: { color: 'rgba(0,0,0,0.05)' },
      },
      x: {
        grid: { display: false },
      },
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
              <Typography color="text.secondary">No trend data available</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default PerformanceLineChart;
