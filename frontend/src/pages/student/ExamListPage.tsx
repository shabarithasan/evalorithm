import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  Snackbar,
  Alert,
} from '@mui/material';
import AssignmentIcon from '@mui/icons-material/Assignment';
import TimerIcon from '@mui/icons-material/Timer';
import EventIcon from '@mui/icons-material/Event';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import VisibilityIcon from '@mui/icons-material/Visibility';
import LoadingScreen from '../../components/common/LoadingScreen';
import { examService, examTakingService } from '../../services';
import { Exam } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const ExamListPage: React.FC = () => {
  const navigate = useNavigate();
  const [exams, setExams] = useState<Exam[]>([]);
  const [loading, setLoading] = useState(true);
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({
    open: false, message: '', severity: 'success',
  });

  useEffect(() => {
    fetchExams();
  }, []);

  const fetchExams = async () => {
    try {
      const res = await examService.getAll({ page: 0, size: 50, status: 'PUBLISHED' });
      const activeRes = await examService.getAll({ page: 0, size: 50, status: 'ACTIVE' });
      const published = res.success ? res.data.content : [];
      const active = activeRes.success ? activeRes.data.content : [];
      const allExams = [...active, ...published.filter((p) => !active.find((a) => a.id === p.id))];
      setExams(allExams);
    } catch {
    } finally {
      setLoading(false);
    }
  };

  const handleStartExam = async (examId: number) => {
    try {
      const res = await examTakingService.startExam(examId);
      if (res.success) {
        navigate(`/student/exams/${examId}/take`);
      }
    } catch (err: any) {
      setSnackbar({
        open: true,
        message: err.response?.data?.message || 'Unable to start exam',
        severity: 'error',
      });
    }
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'ACTIVE': return '#1565C0';
      case 'PUBLISHED': return '#2E7D32';
      default: return '#757575';
    }
  };

  const getExamTypeColor = (type: string): string => {
    switch (type) {
      case 'UNIT_TEST': return '#1565C0';
      case 'SUBJECT_TEST': return '#2E7D32';
      case 'SEMESTER_TEST': return '#7B1FA2';
      case 'INTERNAL_ASSESSMENT': return '#E65100';
      case 'MOCK_TEST': return '#C62828';
      case 'PRACTICE_TEST': return '#78909C';
      case 'FINAL_EXAMINATION': return '#F57F17';
      default: return '#757575';
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 1, fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>My Exams</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        View and take your assigned examinations
      </Typography>

      {exams.length === 0 ? (
        <Card>
          <CardContent>
            <Box sx={{ textAlign: 'center', py: 6 }}>
              <AssignmentIcon sx={{ fontSize: 48, color: 'grey.300', mb: 2 }} />
              <Typography color="text.secondary">No exams available at this time</Typography>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {exams.map((exam) => (
            <Grid item xs={12} sm={6} md={4} key={exam.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', '&:hover': { boxShadow: 4 } }}>
                <CardContent sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                  <Box sx={{ display: 'flex', gap: 1, mb: 1.5 }}>
                    <Chip
                      label={exam.examType.replace('_', ' ')}
                      size="small"
                      sx={{
                        bgcolor: `${getExamTypeColor(exam.examType)}15`,
                        color: getExamTypeColor(exam.examType),
                        fontWeight: 600,
                        fontSize: '0.7rem',
                      }}
                    />
                    <Chip
                      label={exam.status}
                      size="small"
                      sx={{
                        bgcolor: `${getStatusColor(exam.status)}15`,
                        color: getStatusColor(exam.status),
                        fontWeight: 600,
                        fontSize: '0.7rem',
                      }}
                    />
                  </Box>

                  <Typography variant="h6" sx={{ fontWeight: 600, mb: 1, fontSize: '1rem' }}>
                    {exam.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2, flex: 1 }}>
                    {exam.subjectName}
                  </Typography>

                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.75, mb: 2 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <EventIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                      <Typography variant="caption" color="text.secondary">
                        {formatDateTime(exam.startDate)}
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <TimerIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                      <Typography variant="caption" color="text.secondary">
                        {exam.durationMinutes} minutes | {exam.totalMarks} marks
                      </Typography>
                    </Box>
                  </Box>

                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={exam.status === 'COMPLETED' ? <VisibilityIcon /> : <PlayArrowIcon />}
                    onClick={() => {
                      if (exam.status === 'COMPLETED') {
                        navigate(`/student/exams/${exam.id}/result`);
                      } else {
                        handleStartExam(exam.id);
                      }
                    }}
                  >
                    {exam.status === 'COMPLETED' ? 'View Result' : 'Start Exam'}
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={() => setSnackbar((s) => ({ ...s, open: false }))}>
        <Alert severity={snackbar.severity}>{snackbar.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default ExamListPage;
