import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Card, CardContent, Button, Radio, RadioGroup, FormControlLabel,
  Alert, Chip, Grid, LinearProgress, CircularProgress,
} from '@mui/material';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import PsychologyIcon from '@mui/icons-material/Psychology';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';
import LoadingScreen from '../../components/common/LoadingScreen';
import { adaptiveService, subjectService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import { AdaptiveSession, AdaptiveQuestion, Subject } from '../../types';

const AdaptiveTestPage: React.FC = () => {
  const { user } = useAuth();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [selectedSubject, setSelectedSubject] = useState<number>(0);
  const [session, setSession] = useState<AdaptiveSession | null>(null);
  const [question, setQuestion] = useState<AdaptiveQuestion | null>(null);
  const [selectedOption, setSelectedOption] = useState('');
  const [starting, setStarting] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [history, setHistory] = useState<any[]>([]);
  const [phase, setPhase] = useState<'start' | 'active' | 'result'>('start');

  useEffect(() => {
    fetchSubjects();
  }, []);

  const fetchSubjects = async () => {
    try {
      const res = await subjectService.getAll();
      if (res.success) setSubjects(res.data?.content || []);
    } catch { /* empty */ }
  };

  const handleStart = async () => {
    if (!selectedSubject) { setError('Please select a subject'); return; }
    setStarting(true);
    setError('');
    try {
      const res = await adaptiveService.start(selectedSubject);
      if (res.data?.success) {
        setSession(res.data.data);
        setPhase('active');
        loadNext(res.data.data.id);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to start session');
    } finally {
      setStarting(false);
    }
  };

  const loadNext = async (sessionId: number) => {
    try {
      const res = await adaptiveService.getNext(sessionId);
      if (res.data?.success) {
        setQuestion(res.data.data);
        setSelectedOption('');
      } else {
        handleEnd(sessionId);
      }
    } catch {
      handleEnd(sessionId);
    }
  };

  const handleSubmit = async () => {
    if (!session || !question || !selectedOption) return;
    setSubmitting(true);
    setError('');
    try {
      const res = await adaptiveService.submitAnswer(session.id, {
        questionId: question.questionId,
        selectedOption,
      });
      if (res.data?.success) {
        const updatedSession = res.data.data?.session || session;
        setSession(updatedSession);
        if (res.data.data?.correct) {
          setSuccess('Correct!');
        } else {
          setSuccess('Incorrect');
        }
        setTimeout(() => { setSuccess(''); loadNext(session.id); }, 1200);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit answer');
    } finally {
      setSubmitting(false);
    }
  };

  const handleEnd = async (sessionId?: number) => {
    const sid = sessionId || session?.id;
    if (!sid) return;
    try {
      const res = await adaptiveService.endSession(sid);
      if (res.data?.success) {
        setSession(res.data.data);
        setHistory(res.data.data?.history || []);
      }
    } catch { /* empty */ }
    setPhase('result');
  };

  if (phase === 'start') {
    return (
      <Box>
        <Typography variant="h4" sx={{ mb: 3 }}>Adaptive Test</Typography>
        <Card sx={{ maxWidth: 600, mx: 'auto' }}>
          <CardContent>
            <Box sx={{ textAlign: 'center', mb: 3 }}>
              <PsychologyIcon sx={{ fontSize: 64, color: 'primary.main' }} />
              <Typography variant="h5" sx={{ mt: 1, fontWeight: 600 }}>Start Adaptive Test</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                AI adapts question difficulty based on your performance in real-time
              </Typography>
            </Box>
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mb: 3 }}>
              <Typography variant="body2" sx={{ fontWeight: 500 }}>Select Subject</Typography>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {subjects.map((s) => (
                  <Chip
                    key={s.id}
                    label={s.name}
                    onClick={() => setSelectedSubject(s.id)}
                    color={selectedSubject === s.id ? 'primary' : 'default'}
                    variant={selectedSubject === s.id ? 'filled' : 'outlined'}
                    sx={{ cursor: 'pointer' }}
                  />
                ))}
              </Box>
            </Box>
            <Button
              variant="contained"
              fullWidth
              size="large"
              startIcon={starting ? <CircularProgress size={20} /> : <PlayArrowIcon />}
              onClick={handleStart}
              disabled={starting || !selectedSubject}
            >
              {starting ? 'Starting...' : 'Start Test'}
            </Button>
          </CardContent>
        </Card>
      </Box>
    );
  }

  if (phase === 'result') {
    return (
      <Box>
        <Typography variant="h4" sx={{ mb: 3 }}>Test Results</Typography>
        <Card sx={{ maxWidth: 600, mx: 'auto', mb: 3 }}>
          <CardContent>
            <Box sx={{ textAlign: 'center', mb: 3 }}>
              <Typography variant="h3" color="primary" sx={{ fontWeight: 700 }}>
                {session?.score || 0}
              </Typography>
              <Typography variant="h6" color="text.secondary">Final Score</Typography>
            </Box>
            <Grid container spacing={2} sx={{ mb: 3 }}>
              <Grid item xs={4}>
                <Box sx={{ textAlign: 'center' }}>
                  <Typography variant="h5" sx={{ fontWeight: 600, color: 'success.main' }}>{session?.correctAnswers || 0}</Typography>
                  <Typography variant="caption" color="text.secondary">Correct</Typography>
                </Box>
              </Grid>
              <Grid item xs={4}>
                <Box sx={{ textAlign: 'center' }}>
                  <Typography variant="h5" sx={{ fontWeight: 600, color: 'error.main' }}>{session?.wrongAnswers || 0}</Typography>
                  <Typography variant="caption" color="text.secondary">Wrong</Typography>
                </Box>
              </Grid>
              <Grid item xs={4}>
                <Box sx={{ textAlign: 'center' }}>
                  <Typography variant="h5" sx={{ fontWeight: 600, color: 'info.main' }}>{session?.accuracy?.toFixed(0) || 0}%</Typography>
                  <Typography variant="caption" color="text.secondary">Accuracy</Typography>
                </Box>
              </Grid>
            </Grid>
            <Box sx={{ mb: 2 }}>
              <Typography variant="body2" color="text.secondary">Difficulty Reached</Typography>
              <Chip label={session?.currentDifficulty || 'N/A'} color="primary" sx={{ mt: 0.5 }} />
            </Box>
            <Box sx={{ mb: 2 }}>
              <Typography variant="body2" color="text.secondary">Max Streak</Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 0.5 }}>
                <LocalFireDepartmentIcon sx={{ color: '#FF9800' }} />
                <Typography variant="h6" sx={{ fontWeight: 600 }}>{session?.maxStreak || 0}</Typography>
              </Box>
            </Box>
          </CardContent>
        </Card>
        <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1 }}>
          <Button variant="contained" onClick={() => { setPhase('start'); setSession(null); setQuestion(null); }}>
            Start New Test
          </Button>
        </Box>
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Adaptive Test</Typography>
        <Button variant="outlined" color="error" startIcon={<StopIcon />} onClick={() => handleEnd()}>
          End Test
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      {success && <Alert severity={success === 'Correct!' ? 'success' : 'warning'} sx={{ mb: 2 }}>{success}</Alert>}

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          {question ? (
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <Chip label={question.questionType} color="primary" size="small" />
                  <Chip label={question.difficulty} variant="outlined" size="small" />
                </Box>
                <Typography variant="h6" sx={{ mb: 3 }}>{question.questionText}</Typography>
                <RadioGroup value={selectedOption} onChange={(e) => setSelectedOption(e.target.value)}>
                  {question.options?.map((opt) => (
                    <FormControlLabel
                      key={opt.optionLabel}
                      value={opt.optionLabel}
                      control={<Radio />}
                      label={`${opt.optionLabel}. ${opt.optionText}`}
                      sx={{ mb: 1, p: 1, borderRadius: 2, border: '1px solid', borderColor: selectedOption === opt.optionLabel ? 'primary.main' : 'grey.200', bgcolor: selectedOption === opt.optionLabel ? 'primary.50' : 'transparent' }}
                    />
                  ))}
                </RadioGroup>
                <Button
                  variant="contained"
                  fullWidth
                  sx={{ mt: 2 }}
                  onClick={handleSubmit}
                  disabled={!selectedOption || submitting}
                >
                  {submitting ? 'Submitting...' : 'Submit Answer'}
                </Button>
              </CardContent>
            </Card>
          ) : (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
              <CircularProgress />
            </Box>
          )}
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>Session Stats</Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Typography variant="body2" color="text.secondary">Score</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 600 }}>{session?.score || 0}</Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Typography variant="body2" color="text.secondary">Answered</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 600 }}>{session?.questionsAnswered || 0}</Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Typography variant="body2" color="text.secondary">Accuracy</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 600 }}>{session?.accuracy?.toFixed(1) || 0}%</Typography>
                </Box>
                <LinearProgress variant="determinate" value={session?.accuracy || 0} sx={{ height: 8, borderRadius: 4 }} />
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Typography variant="body2" color="text.secondary">Difficulty</Typography>
                  <Chip label={session?.currentDifficulty || 'N/A'} size="small" />
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" color="text.secondary">Streak</Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    <LocalFireDepartmentIcon sx={{ fontSize: 18, color: '#FF9800' }} />
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>{session?.streakCount || 0}</Typography>
                  </Box>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AdaptiveTestPage;
