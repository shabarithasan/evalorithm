import React from 'react';
import { Box, Typography, Card, CardContent } from '@mui/material';
import { Pie } from 'react-chartjs-2';

interface Props {
  data: { departmentName: string; value: number }[];
  title?: string;
}

const COLORS = [
  '#1565C0', '#1E88E5', '#42A5F5', '#64B5F6', '#90CAF9',
  '#0D47A1', '#2196F3', '#03A9F4', '#00BCD4', '#009688',
];

const DepartmentPieChart: React.FC<Props> = ({ data, title = 'Department Distribution' }) => {
  const chartData = {
    labels: data.map((d) => d.departmentName),
    datasets: [
      {
        data: data.map((d) => d.value),
        backgroundColor: data.map((_, i) => COLORS[i % COLORS.length] + 'CC'),
        borderColor: data.map((_, i) => COLORS[i % COLORS.length]),
        borderWidth: 2,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right' as const,
        labels: { padding: 12, usePointStyle: true, pointStyle: 'circle' },
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
            <Pie data={chartData} options={options} />
          ) : (
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
              <Typography color="text.secondary">No department data available</Typography>
            </Box>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default DepartmentPieChart;
