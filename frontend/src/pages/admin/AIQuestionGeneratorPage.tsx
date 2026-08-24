import React, { useState, useEffect, useCallback } from 'react';
import {
  Box, Card, CardContent, Typography, Grid, TextField, MenuItem, Button,
  Slider, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Chip, Alert, CircularProgress, Dialog, DialogTitle, DialogContent,
  DialogActions, Pagination,
} from '@mui/material';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import SaveIcon from '@mui/icons-material/Save';
import RefreshIcon from '@mui/icons-material/Refresh';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import LoadingScreen from '../../components/common/LoadingScreen';
import { aiQuestionService, subjectService, unitService, topicService } from '../../services';
import {
  AIQuestion, AIQuestionGenerateRequest, Subject, Unit, Topic,
  QuestionType, QuestionDifficulty, BloomLevel,
} from '../../types';

const questionTypes: QuestionType[] = ['MCQ', 'TRUE_FALSE', 'FILL_BLANKS', 'DESCRIPTIVE', 'CASE_STUDY'];
const difficulties: QuestionDifficulty[] = ['EASY', 'MEDIUM', 'HARD', 'EXPERT'];
const bloomLevels: BloomLevel[] = ['K1_REMEMBER', 'K2_UNDERSTAND', 'K3_APPLY', 'K4_ANALYZE', 'K5_EVALUATE', 'K6_CREATE'];

const AIQuestionGeneratorPage: React.FC = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [units, setUnits] = useState<Unit[]>([]);
  const [topics, setTopics] = useState<Topic[]>([]);
  const [selectedSubject, setSelectedSubject] = useState<number>(0);
  const [selectedUnit, setSelectedUnit] = useState<number>(0);
  const [selectedTopic, setSelectedTopic] = useState<number>(0);
  const [questionType, setQuestionType] = useState<string>('MCQ');
  const [difficulty, setDifficulty] = useState<string>('MEDIUM');
  const [bloomLevel, setBloomLevel] = useState<string>('K2_UNDERSTAND');
  const [count, setCount] = useState<number>(5);
  const [instructions, setInstructions] = useState('');
  const [generated, setGenerated] = useState<AIQuestion[]>([]);
  const [history, setHistory] = useState<AIQuestion[]>([]);
  const [historyPage, setHistoryPage] = useState(0);
  const [historyTotal, setHistoryTotal] = useState(0);
  const [generating, setGenerating] = useState(false);
  const [loadingHistory, setLoadingHistory] = useState(true);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetchSubjects();
    fetchHistory();
  }, []);

  useEffect(() => {
    if (selectedSubject) fetchUnits(selectedSubject);
    else { setUnits([]); setTopics([]); }
  }, [selectedSubject]);

  useEffect(() => {
    if (selectedUnit) fetchTopics(selectedUnit);
    else setTopics([]);
  }, [selectedUnit]);

  const fetchSubjects = async () => {
    try {
      const res = await subjectService.getAll();
      if (res.success) setSubjects(res.data?.content || []);
    } catch { /* empty */ }
  };

  const fetchUnits = async (subId: number) => {
    try {
      const res = await unitService.getBySubject(subId);
      if (res.success) setUnits(res.data || []);
    } catch { /* empty */ }
  };

  const fetchTopics = async (unitId: number) => {
    try {
      const res = await topicService.getByUnit(unitId);
      if (res.success) setTopics(res.data || []);
    } catch { /* empty */ }
  };

  const fetchHistory = useCallback(async (page = 0) => {
    setLoadingHistory(true);
    try {
      const res = await aiQuestionService.getAll({ page, size: 10 });
      if (res.data?.success) {
        setHistory(res.data.data?.content || []);
        setHistoryTotal(res.data.data?.totalPages || 0);
      }
    } catch { /* empty */ }
    setLoadingHistory(false);
  }, []);

  const handleGenerate = async () => {
    if (!selectedSubject) {
      setError('Please select a subject');
      return;
    }
    setGenerating(true);
    setError('');
    setSuccess('');
    try {
      const payload: AIQuestionGenerateRequest = {
        subjectId: selectedSubject,
        unitId: selectedUnit || undefined,
        topicId: selectedTopic || undefined,
        departmentId: subjects.find((s) => s.id === selectedSubject)?.departmentId || 0,
        questionType,
        difficulty,
        bloomLevel,
        count,
        additionalInstructions: instructions || undefined,
      };
      const res = await aiQuestionService.generate(payload);
      if (res.data?.success) {
        setGenerated(res.data.data || []);
        setSuccess(`Generated ${res.data.data?.length || 0} questions successfully`);
      } else {
        setError(res.data?.message || 'Generation failed');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to generate questions');
    } finally {
      setGenerating(false);
    }
  };

  const handleApprove = async (id: number) => {
    try {
      await aiQuestionService.approve(id);
      setGenerated((prev) => prev.map((q) => q.id === id ? { ...q, isApproved: true } : q));
      setSuccess('Question approved and saved to bank');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to approve');
    }
  };

  const handleReject = async (id: number) => {
    try {
      await aiQuestionService.reject(id);
      setGenerated((prev) => prev.filter((q) => q.id !== id));
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to reject');
    }
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>AI Question Generator</Typography>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2 }}>Generate Questions</Typography>
          {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={3}>
              <TextField select fullWidth label="Subject" value={selectedSubject} onChange={(e) => setSelectedSubject(Number(e.target.value))} size="small">
                <MenuItem value={0}>Select Subject</MenuItem>
                {subjects.map((s) => <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <TextField select fullWidth label="Unit (Optional)" value={selectedUnit} onChange={(e) => setSelectedUnit(Number(e.target.value))} size="small">
                <MenuItem value={0}>All Units</MenuItem>
                {units.map((u) => <MenuItem key={u.id} value={u.id}>{u.name}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <TextField select fullWidth label="Topic (Optional)" value={selectedTopic} onChange={(e) => setSelectedTopic(Number(e.target.value))} size="small">
                <MenuItem value={0}>All Topics</MenuItem>
                {topics.map((t) => <MenuItem key={t.id} value={t.id}>{t.name}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <TextField select fullWidth label="Question Type" value={questionType} onChange={(e) => setQuestionType(e.target.value)} size="small">
                {questionTypes.map((qt) => <MenuItem key={qt} value={qt}>{qt}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <TextField select fullWidth label="Difficulty" value={difficulty} onChange={(e) => setDifficulty(e.target.value)} size="small">
                {difficulties.map((d) => <MenuItem key={d} value={d}>{d}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <TextField select fullWidth label="Bloom Level" value={bloomLevel} onChange={(e) => setBloomLevel(e.target.value)} size="small">
                {bloomLevels.map((bl) => <MenuItem key={bl} value={bl}>{bl.replace('_', ' ')}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Typography variant="body2" gutterBottom>Count: {count}</Typography>
              <Slider value={count} onChange={(_, v) => setCount(v as number)} min={1} max={20} marks valueLabelDisplay="auto" />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth multiline rows={2} label="Additional Instructions (Optional)" value={instructions} onChange={(e) => setInstructions(e.target.value)} size="small" />
            </Grid>
            <Grid item xs={12}>
              <Button variant="contained" startIcon={generating ? <CircularProgress size={20} /> : <AutoAwesomeIcon />} onClick={handleGenerate} disabled={generating} sx={{ mr: 1 }}>
                {generating ? 'Generating...' : 'Generate Questions'}
              </Button>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {generated.length > 0 && (
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Generated Questions</Typography>
            {generated.map((q) => (
              <Card key={q.id} variant="outlined" sx={{ mb: 2, p: 2, borderLeft: 4, borderColor: q.isApproved ? 'success.main' : 'primary.main' }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                  <Typography variant="subtitle2" sx={{ flex: 1, mr: 1 }}>{q.questionText}</Typography>
                  <Box sx={{ display: 'flex', gap: 0.5, flexShrink: 0 }}>
                    <Chip label={q.questionType} size="small" color="primary" />
                    <Chip label={q.difficulty} size="small" variant="outlined" />
                  </Box>
                </Box>
                {q.options && q.options.length > 0 && (
                  <Box sx={{ mb: 1 }}>
                    {q.options.map((opt, i) => (
                      <Typography key={i} variant="body2" sx={{ color: opt.label === q.correctAnswer ? 'success.main' : 'text.secondary', fontWeight: opt.label === q.correctAnswer ? 600 : 400 }}>
                        {opt.label}. {opt.text}
                      </Typography>
                    ))}
                  </Box>
                )}
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  <strong>Answer:</strong> {q.correctAnswer}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  <strong>Explanation:</strong> {q.explanation}
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    Confidence: {Math.round(q.confidenceScore * 100)}%
                  </Typography>
                  <Box sx={{ flex: 1 }} />
                  {!q.isApproved && (
                    <>
                      <Button size="small" color="success" startIcon={<CheckCircleIcon />} onClick={() => handleApprove(q.id)}>
                        Save to Bank
                      </Button>
                      <Button size="small" color="error" startIcon={<CancelIcon />} onClick={() => handleReject(q.id)}>
                        Reject
                      </Button>
                    </>
                  )}
                  {q.isApproved && (
                    <Chip label="Approved" color="success" size="small" icon={<CheckCircleIcon />} />
                  )}
                </Box>
              </Card>
            ))}
          </CardContent>
        </Card>
      )}

      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2 }}>Generation History</Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Question</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Difficulty</TableCell>
                  <TableCell>Subject</TableCell>
                  <TableCell>Confidence</TableCell>
                  <TableCell>Status</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {loadingHistory ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center"><CircularProgress size={24} /></TableCell>
                  </TableRow>
                ) : history.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center">No history found</TableCell>
                  </TableRow>
                ) : (
                  history.map((q) => (
                    <TableRow key={q.id} hover>
                      <TableCell sx={{ maxWidth: 300, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {q.questionText}
                      </TableCell>
                      <TableCell><Chip label={q.questionType} size="small" /></TableCell>
                      <TableCell>{q.difficulty}</TableCell>
                      <TableCell>{q.subjectName}</TableCell>
                      <TableCell>{Math.round(q.confidenceScore * 100)}%</TableCell>
                      <TableCell>
                        {q.isApproved ? (
                          <Chip label="Approved" color="success" size="small" />
                        ) : (
                          <Chip label="Pending" color="warning" size="small" />
                        )}
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
          {historyTotal > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
              <Pagination count={historyTotal} page={historyPage + 1} onChange={(_, p) => { setHistoryPage(p - 1); fetchHistory(p - 1); }} />
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default AIQuestionGeneratorPage;
