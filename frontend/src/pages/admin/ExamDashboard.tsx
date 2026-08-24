import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Grid, Typography, Card, CardContent, Button } from '@mui/material';
import AssignmentIcon from '@mui/icons-material/Assignment';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import ScheduleIcon from '@mui/icons-material/Schedule';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import EditNoteIcon from '@mui/icons-material/EditNote';
import AddIcon from '@mui/icons-material/Add';
import { Pie } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import StatsCard from '../../components/common/StatsCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import { examService } from '../../services';
import { ExamDashboardData, Exam } from '../../types';
import { formatDateTime } from '../../utils/helpers';

ChartJS.register(ArcElement, Tooltip, Legend);

const ExamDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [data, setData] = useState<ExamDashboardData | null>(null);
  const [recentExams, setRecentExams] = useState<Exam[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const [dashRes, examsRes] = await Promise.all([
        examService.getDashboard(),
        examService.getAll({ page: 0, size: 5 }),
      ]);
      if (dashRes.success) setData(dashRes.data);
      if (examsRes.success) setRecentExams(examsRes.data.content);
    } catch {
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (!data) return null;

  const pieData = {
    labels: ['Active', 'Scheduled', 'Completed', 'Draft'],
    datasets: [
      {
        data: [data.activeExams, data.scheduledExams, data.completedExams, data.draftExams],
        backgroundColor: ['#1565C0', '#E65100', '#2E7D32', '#757575'],
        borderWidth: 0,
      },
    ],
  };

  const pieOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom' as const },
    },
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'ACTIVE': return '#1565C0';
      case 'PUBLISHED': return '#2E7D32';
      case 'DRAFT': return '#757575';
      case 'COMPLETED': return '#E65100';
      case 'CANCELLED': return '#C62828';
      default: return '#757575';
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>Exam Dashboard</Typography>
          <Typography variant="body2" color="text.secondary">Overview of examination system</Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/admin/exams/create')}
        >
          Create Exam
        </Button>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Total Exams" value={data.totalExams} icon={<AssignmentIcon />} color="#1565C0" />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Active" value={data.activeExams} icon={<PlayArrowIcon />} color="#2E7D32" />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Scheduled" value={data.scheduledExams} icon={<ScheduleIcon />} color="#E65100" />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Completed" value={data.completedExams} icon={<CheckCircleIcon />} color="#7B1FA2" />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Draft" value={data.draftExams} icon={<EditNoteIcon />} color="#757575" />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid item xs={12} md={5}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Exam Distribution</Typography>
              <Box sx={{ height: 280 }}>
                <Pie data={pieData} options={pieOptions} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Recent Exams</Typography>
              {recentExams.length === 0 ? (
                <Typography color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
                  No exams created yet
                </Typography>
              ) : (
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                  {recentExams.map((exam) => (
                    <Box
                      key={exam.id}
                      sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        p: 1.5,
                        borderRadius: 1,
                        border: '1px solid',
                        borderColor: 'grey.200',
                        cursor: 'pointer',
                        '&:hover': { bgcolor: 'grey.50' },
                      }}
                      onClick={() => navigate(`/admin/exams/${exam.id}`)}
                    >
                      <Box>
                        <Typography variant="body2" sx={{ fontWeight: 600 }}>{exam.title}</Typography>
                        <Typography variant="caption" color="text.secondary">
                          {exam.subjectName} - {formatDateTime(exam.startDate)}
                        </Typography>
                      </Box>
                      <Box
                        sx={{
                          px: 1,
                          py: 0.25,
                          borderRadius: 1,
                          bgcolor: `${getStatusColor(exam.status)}15`,
                          color: getStatusColor(exam.status),
                          fontSize: '0.75rem',
                          fontWeight: 600,
                        }}
                      >
                        {exam.status}
                      </Box>
                    </Box>
                  ))}
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default ExamDashboard;
