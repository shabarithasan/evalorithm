import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  Paper,
  Chip,
  Alert,
  CircularProgress,
  LinearProgress,
  Divider,
  Radio,
  RadioGroup,
  FormControl,
  FormControlLabel,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Tooltip,
  List,
  ListItem,
  ListItemText,
  Avatar,
  Badge,
} from '@mui/material';
import MenuItem from '@mui/material/MenuItem';
import {
  PlayArrow as PlayArrowIcon,
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
  Timer as TimerIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Psychology as PsychologyIcon,
  School as SchoolIcon,
  ArrowForward as ArrowForwardIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import PageHeader from '../../components/common/PageHeader';
import adaptiveExamService, { AdaptiveQuestion, AdaptiveExamRequest } from '../../services/adaptiveExamService';
import examService from '../../services/examService';
import { useAuth } from '../../hooks/useAuth';

interface AdaptiveExamState {
  step: 'setup' | 'taking' | 'review' | 'result';
  examId?: number;
  attemptId?: number;
  currentQuestion?: AdaptiveQuestion;
  questionIndex: number;
  totalQuestions: number;
  answers: Record<number, { selected: string | number[]; text: string; correct: boolean; time: number }>;
  startTime: Date;
  timeRemaining: number;
  score: number;
  correctCount: number;
  wrongCount: number;
  difficultyHistory: Array<{ question: number; difficulty: string; correct: boolean }>;
}

const AdaptiveExamPage: React.FC = () => {
  const { user } = useAuth();
  const [state, setState] = useState<AdaptiveExamState>({
    step: 'setup',
    questionIndex: 0,
    totalQuestions: 25,
    answers: {},
    startTime: new Date(),
    timeRemaining: 0,
    score: 0,
    correctCount: 0,
    wrongCount: 0,
    difficultyHistory: [],
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [subjects, setSubjects] = useState<any[]>([]);
  const [departments, setDepartments] = useState<any[]>([]);
  const [semesters, setSemesters] = useState<any[]>([]);
  const [formData, setFormData] = useState<AdaptiveExamRequest>({
    subjectId: 0,
    departmentId: 0,
    semesterId: 0,
    totalQuestions: 25,
    createdBy: user?.id || 0,
  });
  const [showReviewDialog, setShowReviewDialog] = useState(false);
  const [timerInterval, setTimerInterval] = useState<NodeJS.Timeout | null>(null);

  useEffect(() => {
    fetchDropdowns();
  }, []);

  const fetchDropdowns = async () => {
    try {
      const [subRes, deptRes, semRes] = await Promise.all([
        examService.getSubjects(),
        examService.getDepartments(),
        examService.getSemesters(),
      ]);
      if (subRes.success) setSubjects(subRes.data.content);
      if (deptRes.success) setDepartments(deptRes.data.content);
      if (semRes.success) setSemesters(semRes.data.content);
    } catch (err) {
      console.error('Failed to fetch dropdowns:', err);
    }
  };

  const handleCreateExam = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await adaptiveExamService.createAdaptiveExam(formData);
      if (response.success) {
        setState(prev => ({
          ...prev,
          step: 'taking',
          examId: response.data.examId,
          totalQuestions: response.data.totalQuestions,
          timeRemaining: response.data.durationMinutes * 60,
          startTime: new Date(),
        }));
        startExam(response.data.examId);
      } else {
        setError(response.message || 'Failed to create exam');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create exam');
    } finally {
      setLoading(false);
    }
  };

  const startExam = async (examId: number) => {
    try {
      const response = await examService.startExam(examId);
      if (response.success) {
        setState(prev => ({ ...prev, attemptId: response.data.attemptId }));
        fetchNextQuestion(true, null);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to start exam');
    }
  };

  const fetchNextQuestion = async (previousCorrect: boolean, previousQuestionId: number | null) => {
    if (!state.attemptId) return;
    setLoading(true);
    try {
      const response = await adaptiveExamService.getNextQuestion(state.attemptId, previousCorrect, previousQuestionId || undefined);
      if (response.success) {
        setState(prev => ({
          ...prev,
          currentQuestion: response.data,
          questionIndex: prev.questionIndex + 1,
        }));
      }
    } catch (err: any) {
      if (err.response?.status === 400 && err.response?.data?.message?.includes('All questions answered')) {
        handleSubmit();
      } else {
        setError(err.response?.data?.message || 'Failed to load question');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleAnswer = (selected: string | number[], text: string = '') => {
    if (!state.currentQuestion || !state.attemptId) return;
    const startTime = Date.now();
    setState(prev => ({
      ...prev,
      answers: {
        ...prev.answers,
        [state.currentQuestion!.examQuestionId]: {
          selected,
          text,
          correct: false,
          time: 0,
        },
      },
    }));
  };

  const handleNext = () => {
    if (!state.currentQuestion) return;
    const answer = state.answers[state.currentQuestion.examQuestionId];
    if (!answer && state.currentQuestion.questionType !== 'FILL_BLANKS') {
      setError('Please select an answer');
      return;
    }
    const isCorrect = checkAnswer(state.currentQuestion, answer);
    setState(prev => ({
      ...prev,
      correctCount: prev.correctCount + (isCorrect ? 1 : 0),
      wrongCount: prev.wrongCount + (isCorrect ? 0 : 1),
      difficultyHistory: [
        ...prev.difficultyHistory,
        { question: prev.questionIndex + 1, difficulty: state.currentQuestion!.difficulty, correct: isCorrect },
      ],
    }));
    fetchNextQuestion(isCorrect, state.currentQuestion.examQuestionId);
  };

  const checkAnswer = (question: AdaptiveQuestion, answer: any): boolean => {
    // In real implementation, this would call backend evaluation
    // For now, simulate based on first option
    return answer?.selected === 'A';
  };

  const handleSubmit = async () => {
    if (!state.attemptId) return;
    setLoading(true);
    try {
      const response = await examService.submitExam(state.attemptId);
      if (response.success) {
        setState(prev => ({
          ...prev,
          step: 'result',
          score: response.data.totalCorrect,
          correctCount: response.data.totalCorrect,
          wrongCount: response.data.totalWrong,
        }));
        stopTimer();
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit exam');
    } finally {
      setLoading(false);
    }
  };

  const startTimer = () => {
    const interval = setInterval(() => {
      setState(prev => {
        if (prev.timeRemaining <= 1) {
          stopTimer();
          handleSubmit();
          return { ...prev, timeRemaining: 0 };
        }
        return { ...prev, timeRemaining: prev.timeRemaining - 1 };
      });
    }, 1000);
    setTimerInterval(interval);
  };

  const stopTimer = () => {
    if (timerInterval) {
      clearInterval(timerInterval);
      setTimerInterval(null);
    }
  };

  useEffect(() => {
    if (state.step === 'taking' && state.timeRemaining > 0) {
      startTimer();
    }
    return () => stopTimer();
  }, [state.step, state.timeRemaining]);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const getDifficultyColor = (diff: string) => {
    switch (diff) {
      case 'EASY': return 'success';
      case 'MEDIUM': return 'warning';
      case 'HARD': return 'error';
      default: return 'default';
    }
  };

  if (state.step === 'setup') {
    return (
      <Box>
        <PageHeader
          title="Adaptive Exam"
          subtitle="Create a personalized exam that adapts difficulty based on your performance"
          actionLabel="Refresh Subjects"
          onAction={fetchDropdowns}
        />

        <Grid container spacing={3} justifyContent="center">
          <Grid item xs={12} md={8} lg={6}>
            <Card sx={{ p: 4 }}>
              <CardContent>
                <Typography variant="h5" gutterBottom align="center">
                  <PsychologyIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                  Adaptive Exam Setup
                </Typography>
                <Typography variant="body1" color="text.secondary" paragraph align="center">
                  Start with easy questions. Answer correctly → next gets harder. Answer wrong → next gets easier. 
                  25 questions total. Results sent to faculty automatically.
                </Typography>

                <Divider sx={{ my: 3 }} />

                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                  <TextField
                    select
                    label="Subject"
                    value={formData.subjectId}
                    onChange={e => setFormData({ ...formData, subjectId: Number(e.target.value) })}
                    required
                    fullWidth
                  >
                    {subjects.map(s => <MenuItem key={s.id} value={s.id}>{s.code} - {s.name}</MenuItem>)}
                  </TextField>

                  <TextField
                    select
                    label="Department"
                    value={formData.departmentId}
                    onChange={e => setFormData({ ...formData, departmentId: Number(e.target.value) })}
                    required
                    fullWidth
                  >
                    {departments.map(d => <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>)}
                  </TextField>

                  <TextField
                    select
                    label="Semester"
                    value={formData.semesterId}
                    onChange={e => setFormData({ ...formData, semesterId: Number(e.target.value) })}
                    required
                    fullWidth
                  >
                    {semesters.map(s => <MenuItem key={s.id} value={s.id}>{s.departmentName} - Semester {s.number}</MenuItem>)}
                  </TextField>

                  <TextField
                    label="Total Questions"
                    type="number"
                    value={formData.totalQuestions}
                    onChange={e => setFormData({ ...formData, totalQuestions: Number(e.target.value) })}
                    fullWidth
                    inputProps={{ step: 5, min: 10, max: 50 }}
                  />
                </Box>

                <Divider sx={{ my: 3 }} />

                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
                  <Button
                    variant="contained"
                    size="large"
                    startIcon={<PlayArrowIcon />}
                    onClick={handleCreateExam}
                    disabled={loading || !formData.subjectId || !formData.departmentId || !formData.semesterId}
                    sx={{ flex: 1 }}
                  >
                    {loading ? <CircularProgress size={24} color="inherit" /> : 'Start Adaptive Exam'}
                  </Button>
                </Box>

                {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>
    );
  }

  if (state.step === 'taking' && state.currentQuestion) {
    const q = state.currentQuestion;
    const progress = (state.questionIndex / state.totalQuestions) * 100;

    return (
      <Box sx={{ maxWidth: 900, mx: 'auto' }}>
        <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Typography variant="h6">Question {state.questionIndex} of {state.totalQuestions}</Typography>
              <Chip
                label={q.difficulty}
                color={getDifficultyColor(q.difficulty) as any}
                size="small"
                icon={
                  q.difficulty === 'EASY' ? <TrendingDownIcon fontSize="small" /> :
                  q.difficulty === 'HARD' ? <TrendingUpIcon fontSize="small" /> :
                  <SchoolIcon fontSize="small" />
                }
              />
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Typography variant="h6" sx={{ color: state.timeRemaining < 300 ? 'error.main' : 'inherit' }}>
                <TimerIcon sx={{ mr: 0.5, verticalAlign: 'middle' }} />
                {formatTime(state.timeRemaining)}
              </Typography>
              <LinearProgress
                variant="determinate"
                value={progress}
                sx={{ width: 200, height: 8, borderRadius: 4 }}
              />
              <Typography variant="body2" color="text.secondary">{Math.round(progress)}%</Typography>
            </Box>
          </Box>

          <Typography variant="h5" gutterBottom>{q.questionTitle}</Typography>
          {q.questionDescription && <Typography variant="body1" color="text.secondary" paragraph>{q.questionDescription}</Typography>}

          <Divider sx={{ my: 3 }} />

          {q.questionType === 'MCQ' && q.options && (
            <FormControl component="fieldset" sx={{ width: '100%' }}>
              <RadioGroup
                value={state.answers[q.examQuestionId]?.selected || ''}
                onChange={e => handleAnswer(e.target.value)}
                row={false}
              >
                {q.options.map((opt, idx) => (
                  <FormControlLabel
                    key={opt.optionLabel}
                    value={opt.optionLabel}
                    control={<Radio />}
                    label={opt.optionText}
                    labelPlacement="end"
                  />
                ))}
              </RadioGroup>
            </FormControl>
          )}

          {q.questionType === 'TRUE_FALSE' && (
            <FormControl component="fieldset" sx={{ width: '100%' }}>
              <RadioGroup
                value={state.answers[q.examQuestionId]?.selected || ''}
                onChange={e => handleAnswer(e.target.value)}
                row
              >
                <FormControlLabel value="True" control={<Radio />} label="True" />
                <FormControlLabel value="False" control={<Radio />} label="False" />
              </RadioGroup>
            </FormControl>
          )}

          {q.questionType === 'FILL_BLANKS' && (
            <TextField
              label="Your Answer"
              multiline
              rows={3}
              fullWidth
              value={state.answers[q.examQuestionId]?.text || ''}
              onChange={e => handleAnswer('', e.target.value)}
              placeholder="Type your answer here..."
            />
          )}

          <Divider sx={{ my: 3 }} />

          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Button
              variant="outlined"
              disabled={state.questionIndex <= 1}
              onClick={() => { /* Previous question logic */ }}
            >
              Previous
            </Button>
            <Button
              variant="contained"
              size="large"
              startIcon={<ArrowForwardIcon />}
              onClick={handleNext}
              disabled={loading}
            >
              {loading ? <CircularProgress size={24} color="inherit" /> : 'Next Question'}
            </Button>
          </Box>
        </Paper>

        <Card sx={{ mt: 2 }}>
          <CardContent>
            <Typography variant="subtitle1" gutterBottom>Difficulty Progression</Typography>
            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
              {state.difficultyHistory.map((d, i) => (
                <Chip
                  key={i}
                  label={`Q${d.question}: ${d.difficulty} ${d.correct ? '✓' : '✗'}`}
                  color={d.correct ? 'success' : 'error'}
                  size="small"
                  variant="outlined"
                />
              ))}
            </Box>
          </CardContent>
        </Card>
      </Box>
    );
  }

  if (state.step === 'result') {
    const percentage = Math.round((state.correctCount / state.totalQuestions) * 100);
    return (
      <Box sx={{ maxWidth: 600, mx: 'auto', textAlign: 'center', py: 4 }}>
        <Typography variant="h3" gutterBottom color={percentage >= 60 ? 'success' : 'error'}>
          {percentage >= 60 ? <CheckCircleIcon sx={{ verticalAlign: 'middle', mr: 1 }} /> : <CancelIcon sx={{ verticalAlign: 'middle', mr: 1 }} />}
          {percentage >= 60 ? 'Passed' : 'Needs Improvement'}
        </Typography>
        <Typography variant="h2" sx={{ mb: 2 }}>
          {state.correctCount} / {state.totalQuestions} ({percentage}%)
        </Typography>
        <Box sx={{ display: 'flex', justifyContent: 'center', gap: 4, mb: 4 }}>
          <Box>
            <Typography variant="h4" color="success">{state.correctCount}</Typography>
            <Typography variant="body2" color="text.secondary">Correct</Typography>
          </Box>
          <Box>
            <Typography variant="h4" color="error">{state.wrongCount}</Typography>
            <Typography variant="body2" color="text.secondary">Wrong</Typography>
          </Box>
          <Box>
            <Typography variant="h4" color="warning">{state.totalQuestions - state.correctCount - state.wrongCount}</Typography>
            <Typography variant="body2" color="text.secondary">Skipped</Typography>
          </Box>
        </Box>
        <Typography variant="body1" color="text.secondary" paragraph>
          Your results have been sent to your faculty for review.
        </Typography>
        <Button variant="contained" size="large" startIcon={<RefreshIcon />} onClick={() => setState({...state, step: 'setup'})}>
          Take Another Exam
        </Button>
      </Box>
    );
  }

  return null;
};

export default AdaptiveExamPage;