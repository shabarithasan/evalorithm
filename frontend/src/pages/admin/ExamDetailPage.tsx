import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Tabs,
  Tab,
  Card,
  CardContent,
  Grid,
  Button,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Snackbar,
  Alert,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import DeleteIcon from '@mui/icons-material/Delete';
import LoadingScreen from '../../components/common/LoadingScreen';
import ResultCard from '../../components/exam/ResultCard';
import QuestionWiseReport from '../../components/exam/QuestionWiseReport';
import { examService, examResultService, examAttendanceService, examReportService } from '../../services';
import { Exam, ExamResult, ExamAttendance, ExamReport, ExamQuestion } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const ExamDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [exam, setExam] = useState<Exam | null>(null);
  const [loading, setLoading] = useState(true);
  const [tabValue, setTabValue] = useState(0);
  const [results, setResults] = useState<ExamResult[]>([]);
  const [attendance, setAttendance] = useState<ExamAttendance[]>([]);
  const [report, setReport] = useState<ExamReport | null>(null);
  const [questionReport, setQuestionReport] = useState<any[]>([]);
  const [examQuestions, setExamQuestions] = useState<any[]>([]);
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({
    open: false, message: '', severity: 'success',
  });

  useEffect(() => {
    if (id) fetchExam();
  }, [id]);

  const fetchExam = async () => {
    try {
      const res = await examService.getById(Number(id));
      if (res.success) setExam(res.data);
    } catch {
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (tabValue === 1 && id) loadQuestions();
    if (tabValue === 3 && id) loadResults();
    if (tabValue === 4 && id) loadAttendance();
    if (tabValue === 5 && id) loadReports();
  }, [tabValue, id]);

  const loadQuestions = async () => {
    try {
      const res = await examService.getExamQuestions(Number(id));
      if (res.success) setExamQuestions(res.data);
    } catch {}
  };

  const loadResults = async () => {
    try {
      const res = await examResultService.getExamResults(Number(id));
      if (res.success) setResults(res.data.content);
    } catch {}
  };

  const loadAttendance = async () => {
    try {
      const res = await examAttendanceService.getByExam(Number(id));
      if (res.success) setAttendance(res.data);
    } catch {}
  };

  const loadReports = async () => {
    try {
      const [reportRes, qReportRes] = await Promise.all([
        examReportService.getExamReport(Number(id)),
        examReportService.getQuestionWiseReport(Number(id)),
      ]);
      if (reportRes.success) setReport(reportRes.data);
      if (qReportRes.success) setQuestionReport(qReportRes.data);
    } catch {}
  };

  const handleRemoveQuestion = async (questionId: number) => {
    try {
      await examService.removeQuestion(Number(id), questionId);
      loadQuestions();
      setSnackbar({ open: true, message: 'Question removed', severity: 'success' });
    } catch {
      setSnackbar({ open: true, message: 'Failed to remove question', severity: 'error' });
    }
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'ACTIVE': return '#1565C0';
      case 'PUBLISHED': return '#2E7D32';
      case 'DRAFT': return '#757575';
      case 'COMPLETED': return '#E65100';
      default: return '#757575';
    }
  };

  if (loading) return <LoadingScreen />;
  if (!exam) return <Typography color="error">Exam not found</Typography>;

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
        <IconButton onClick={() => navigate('/admin/exams')}>
          <ArrowBackIcon />
        </IconButton>
        <Box sx={{ flex: 1 }}>
          <Typography variant="h4" sx={{ fontSize: { xs: '1.3rem', sm: '1.6rem' } }}>{exam.title}</Typography>
          <Box sx={{ display: 'flex', gap: 1, mt: 0.5 }}>
            <Chip label={exam.examType.replace('_', ' ')} size="small" variant="outlined" />
            <Chip
              label={exam.status}
              size="small"
              sx={{ bgcolor: `${getStatusColor(exam.status)}15`, color: getStatusColor(exam.status), fontWeight: 600 }}
            />
          </Box>
        </Box>
      </Box>

      <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)} sx={{ mb: 3 }}>
        <Tab label="Overview" />
        <Tab label="Questions" />
        <Tab label="Students" />
        <Tab label="Results" />
        <Tab label="Attendance" />
        <Tab label="Reports" />
      </Tabs>

      {tabValue === 0 && (
        <Grid container spacing={3}>
          <Grid item xs={12} md={8}>
            <Card>
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2 }}>Exam Information</Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Description</Typography>
                    <Typography>{exam.description || 'No description'}</Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Subject</Typography>
                    <Typography>{exam.subjectName}</Typography>
                  </Grid>
                  <Grid item xs={6} sm={3}>
                    <Typography variant="body2" color="text.secondary">Start Date</Typography>
                    <Typography variant="body2">{formatDateTime(exam.startDate)}</Typography>
                  </Grid>
                  <Grid item xs={6} sm={3}>
                    <Typography variant="body2" color="text.secondary">End Date</Typography>
                    <Typography variant="body2">{formatDateTime(exam.endDate)}</Typography>
                  </Grid>
                  <Grid item xs={6} sm={3}>
                    <Typography variant="body2" color="text.secondary">Duration</Typography>
                    <Typography variant="body2">{exam.durationMinutes} minutes</Typography>
                  </Grid>
                  <Grid item xs={6} sm={3}>
                    <Typography variant="body2" color="text.secondary">Total Marks</Typography>
                    <Typography variant="body2">{exam.totalMarks}</Typography>
                  </Grid>
                  <Grid item xs={6} sm={3}>
                    <Typography variant="body2" color="text.secondary">Passing Marks</Typography>
                    <Typography variant="body2">{exam.passingMarks}</Typography>
                  </Grid>
                  <Grid item xs={6} sm={3}>
                    <Typography variant="body2" color="text.secondary">Max Attempts</Typography>
                    <Typography variant="body2">{exam.maxAttempts}</Typography>
                  </Grid>
                  <Grid item xs={6} sm={3}>
                    <Typography variant="body2" color="text.secondary">Questions</Typography>
                    <Typography variant="body2">{exam.questionCount}</Typography>
                  </Grid>
                  <Grid item xs={6} sm={3}>
                    <Typography variant="body2" color="text.secondary">Students</Typography>
                    <Typography variant="body2">{exam.studentCount}</Typography>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={4}>
            <Card>
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2 }}>Settings</Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                  {[
                    { label: 'Negative Marking', value: exam.negativeMarksEnabled },
                    { label: 'Randomize Questions', value: exam.randomizeQuestions },
                    { label: 'Randomize Options', value: exam.randomizeOptions },
                    { label: 'Show Results Immediately', value: exam.showResultsImmediately },
                    { label: 'Auto Submit', value: exam.autoSubmit },
                    { label: 'Fullscreen Required', value: exam.fullscreenRequired },
                    { label: 'Prevent Tab Switch', value: exam.preventTabSwitch },
                  ].map((item) => (
                    <Box key={item.label} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Typography variant="body2">{item.label}</Typography>
                      <Chip
                        label={item.value ? 'Yes' : 'No'}
                        size="small"
                        color={item.value ? 'success' : 'default'}
                        variant="outlined"
                      />
                    </Box>
                  ))}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {tabValue === 1 && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Assigned Questions ({examQuestions.length})</Typography>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>#</TableCell>
                    <TableCell>Title</TableCell>
                    <TableCell>Type</TableCell>
                    <TableCell>Difficulty</TableCell>
                    <TableCell>Marks</TableCell>
                    <TableCell align="right">Action</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {examQuestions.map((q: any, idx: number) => (
                    <TableRow key={q.id || idx} hover>
                      <TableCell>{idx + 1}</TableCell>
                      <TableCell>{q.questionTitle}</TableCell>
                      <TableCell><Chip label={q.questionType} size="small" variant="outlined" /></TableCell>
                      <TableCell>{q.difficulty}</TableCell>
                      <TableCell>{q.marks}</TableCell>
                      <TableCell align="right">
                        <IconButton size="small" color="error" onClick={() => handleRemoveQuestion(q.questionId)}>
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      )}

      {tabValue === 2 && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Assigned Students</Typography>
            <Typography color="text.secondary">Student assignment management</Typography>
          </CardContent>
        </Card>
      )}

      {tabValue === 3 && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Results ({results.length})</Typography>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Student</TableCell>
                    <TableCell>Register No</TableCell>
                    <TableCell align="right">Marks</TableCell>
                    <TableCell align="right">Percentage</TableCell>
                    <TableCell>Grade</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell align="right">Time (min)</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {results.map((r) => (
                    <TableRow key={r.id} hover>
                      <TableCell>{r.studentName}</TableCell>
                      <TableCell>{r.registerNumber || '-'}</TableCell>
                      <TableCell align="right">{r.totalMarksObtained}/{r.totalMarksPossible}</TableCell>
                      <TableCell align="right">{r.percentage.toFixed(1)}%</TableCell>
                      <TableCell>
                        <Chip label={r.grade} size="small" sx={{ fontWeight: 600 }} />
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={r.isPassed ? 'Passed' : 'Failed'}
                          size="small"
                          color={r.isPassed ? 'success' : 'error'}
                        />
                      </TableCell>
                      <TableCell align="right">{r.timeTakenMinutes}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      )}

      {tabValue === 4 && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Attendance</Typography>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Student</TableCell>
                    <TableCell>Register No</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Join Time</TableCell>
                    <TableCell>Leave Time</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {attendance.map((a) => (
                    <TableRow key={a.id} hover>
                      <TableCell>{a.studentName}</TableCell>
                      <TableCell>{a.registerNumber}</TableCell>
                      <TableCell>
                        <Chip label={a.status.replace('_', ' ')} size="small" variant="outlined" />
                      </TableCell>
                      <TableCell>{a.joinTime ? formatDateTime(a.joinTime) : '-'}</TableCell>
                      <TableCell>{a.leaveTime ? formatDateTime(a.leaveTime) : '-'}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      )}

      {tabValue === 5 && (
        <Grid container spacing={3}>
          {report && (
            <Grid item xs={12} md={4}>
              <ResultCard
                totalObtained={report.averageMarks}
                totalPossible={exam.totalMarks}
                percentage={report.passPercentage}
                grade="-"
                isPassed={report.passPercentage >= 50}
                correctAnswers={report.passed}
                wrongAnswers={report.failed}
                skippedQuestions={report.totalStudents - report.appeared}
                timeTakenMinutes={0}
              />
            </Grid>
          )}
          <Grid item xs={12} md={8}>
            <Card>
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2 }}>Question-Wise Analysis</Typography>
                <QuestionWiseReport data={questionReport} />
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      <Snackbar open={snackbar.open} autoHideDuration={3000} onClose={() => setSnackbar((s) => ({ ...s, open: false }))}>
        <Alert severity={snackbar.severity}>{snackbar.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default ExamDetailPage;
