import React, { useState, useEffect } from 'react';
import { Box, Grid, Card, CardContent, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button } from '@mui/material';
import QuizIcon from '@mui/icons-material/Quiz';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import PendingIcon from '@mui/icons-material/Pending';
import CancelIcon from '@mui/icons-material/Cancel';
import AddIcon from '@mui/icons-material/Add';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import { useNavigate } from 'react-router-dom';
import { Pie, Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import StatsCard from '../../components/common/StatsCard';
import LoadingScreen from '../../components/common/LoadingScreen';
import QuestionTypeBadge from '../../components/questions/QuestionTypeBadge';
import DifficultyBadge from '../../components/questions/DifficultyBadge';
import { questionService } from '../../services';
import { QuestionDashboardData, Question } from '../../types';
import { formatDate } from '../../utils/helpers';

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend);

const QuestionDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [data, setData] = useState<QuestionDashboardData | null>(null);
  const [recentQuestions, setRecentQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [dashRes, recentRes] = await Promise.all([
        questionService.getDashboard(),
        questionService.getAll({ page: 0, size: 10, sortBy: 'createdAt', sortDir: 'desc' }),
      ]);
      if (dashRes.success) setData(dashRes.data);
      if (recentRes.success) setRecentQuestions(recentRes.data.content);
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingScreen />;
  if (!data) return <Typography color="error" sx={{ p: 3 }}>Failed to load dashboard</Typography>;

  const pieData = {
    labels: ['Approved', 'Pending', 'Rejected'],
    datasets: [{
      data: [data.approvedQuestions, data.pendingQuestions, data.rejectedQuestions],
      backgroundColor: ['rgba(46, 125, 50, 0.8)', 'rgba(230, 81, 0, 0.8)', 'rgba(198, 40, 40, 0.8)'],
      borderColor: ['#2E7D32', '#E65100', '#C62828'],
      borderWidth: 1,
    }],
  };

  const pieOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' as const } },
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>Question Dashboard</Typography>
          <Typography variant="body2" color="text.secondary">Overview of the question repository</Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button variant="outlined" startIcon={<UploadFileIcon />} onClick={() => navigate('/admin/bulk-import')}>
            Bulk Import
          </Button>
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => navigate('/admin/questions/create')}>
            Create Question
          </Button>
        </Box>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Total Questions" value={data.totalQuestions} icon={<QuizIcon />} color="#1565C0" />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Approved" value={data.approvedQuestions} icon={<CheckCircleIcon />} color="#2E7D32" />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Pending" value={data.pendingQuestions} icon={<PendingIcon />} color="#E65100" />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Rejected" value={data.rejectedQuestions} icon={<CancelIcon />} color="#C62828" />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatsCard title="Recently Added" value={data.recentlyAdded} icon={<AddIcon />} color="#7B1FA2" />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Status Distribution</Typography>
              <Box sx={{ height: 280 }}>
                <Pie data={pieData} options={pieOptions} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Recent Questions</Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Title</TableCell>
                      <TableCell>Type</TableCell>
                      <TableCell>Difficulty</TableCell>
                      <TableCell>Created</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {recentQuestions.map((q) => (
                      <TableRow
                        key={q.id}
                        hover
                        sx={{ cursor: 'pointer' }}
                        onClick={() => navigate(`/admin/questions/${q.id}`)}
                      >
                        <TableCell sx={{ maxWidth: 250, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          {q.title}
                        </TableCell>
                        <TableCell><QuestionTypeBadge type={q.questionType} /></TableCell>
                        <TableCell><DifficultyBadge difficulty={q.difficulty} /></TableCell>
                        <TableCell>{formatDate(q.createdAt)}</TableCell>
                      </TableRow>
                    ))}
                    {recentQuestions.length === 0 && (
                      <TableRow>
                        <TableCell colSpan={4} align="center">
                          <Typography variant="body2" color="text.secondary">No questions yet</Typography>
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default QuestionDashboard;
