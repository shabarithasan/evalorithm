import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Typography,
  Breadcrumbs,
  Link,
  Paper,
  Button,
  LinearProgress,
  Alert,
  AlertTitle,
  Chip,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  Card,
  CardContent,
  Divider,
  Stepper,
  Step,
  StepLabel,
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import QuizIcon from '@mui/icons-material/Quiz';
import { syllabusUploadService, departmentService, semesterService, subjectService } from '../../services';
import { SyllabusUploadResult, Department, Semester, Subject } from '../../types';

const SyllabusUploadPage: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [result, setResult] = useState<SyllabusUploadResult | null>(null);
  const [error, setError] = useState('');

  const [departments, setDepartments] = useState<Department[]>([]);
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [selectedDept, setSelectedDept] = useState<number | ''>('');
  const [selectedSem, setSelectedSem] = useState<number | ''>('');
  const [selectedSubj, setSelectedSubj] = useState<number | ''>('');

  const [activeStep, setActiveStep] = useState(0);

  useEffect(() => {
    departmentService.getAll(0, 100).then(res => {
      if (res.success) setDepartments(res.data.content);
    }).catch(() => {});
  }, []);

  const loadSemesters = useCallback((deptId: number) => {
    semesterService.getByDepartment(deptId).then(res => {
      if (res.success) setSemesters(res.data);
    }).catch(() => {});
  }, []);

  const loadSubjects = useCallback((semId: number) => {
    subjectService.getBySemester(semId).then(res => {
      if (res.success) setSubjects(res.data);
    }).catch(() => {});
  }, []);

  useEffect(() => {
    if (selectedDept) {
      setSelectedSem('');
      setSelectedSubj('');
      setSubjects([]);
      loadSemesters(selectedDept as number);
    }
  }, [selectedDept, loadSemesters]);

  useEffect(() => {
    if (selectedSem) {
      setSelectedSubj('');
      loadSubjects(selectedSem as number);
    }
  }, [selectedSem, loadSubjects]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selected = e.target.files?.[0];
    if (selected) {
      setFile(selected);
      setResult(null);
      setError('');
      setActiveStep(0);
    }
  };

  const handleUpload = async () => {
    if (!file || !selectedDept || !selectedSem || !selectedSubj) return;
    setUploading(true);
    setError('');
    setActiveStep(0);
    try {
      const response = await syllabusUploadService.uploadSyllabus(file, selectedDept as number, selectedSem as number, selectedSubj as number);
      if (response.success) {
        setResult(response.data);
        setActiveStep(3);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Upload failed.');
    } finally {
      setUploading(false);
    }
  };

  const canUpload = file && selectedDept !== '' && selectedSem !== '' && selectedSubj !== '';

  return (
    <Box>
      <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} sx={{ mb: 2 }}>
        <Link component={RouterLink} to="/admin/dashboard" underline="hover" color="inherit">Dashboard</Link>
        <Typography color="text.primary">Syllabus Upload</Typography>
      </Breadcrumbs>

      <Typography variant="h4" sx={{ mb: 3, fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>
        Upload Syllabus
      </Typography>

      <Stepper activeStep={activeStep} sx={{ mb: 3 }}>
        <Step><StepLabel>Select Department & Subject</StepLabel></Step>
        <Step><StepLabel>Upload Syllabus File</StepLabel></Step>
        <Step><StepLabel>Auto-Generate & Create Exam</StepLabel></Step>
      </Stepper>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" sx={{ mb: 2 }}>1. Select Department, Semester & Subject</Typography>
        <Grid container spacing={2} sx={{ mb: 2 }}>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth size="small">
              <InputLabel>Department</InputLabel>
              <Select value={selectedDept} label="Department" onChange={e => setSelectedDept(e.target.value as number)}>
                {departments.map(d => <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>)}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth size="small" disabled={!selectedDept}>
              <InputLabel>Semester</InputLabel>
              <Select value={selectedSem} label="Semester" onChange={e => setSelectedSem(e.target.value as number)}>
                {semesters.map(s => <MenuItem key={s.id} value={s.id}>Semester {s.number}</MenuItem>)}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth size="small" disabled={!selectedSem}>
              <InputLabel>Subject</InputLabel>
              <Select value={selectedSubj} label="Subject" onChange={e => setSelectedSubj(e.target.value as number)}>
                {subjects.map(s => <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>)}
              </Select>
            </FormControl>
          </Grid>
        </Grid>

        <Divider sx={{ my: 2 }} />

        <Typography variant="h6" sx={{ mb: 2 }}>2. Upload Syllabus File</Typography>
        <Box
          sx={{
            border: '2px dashed',
            borderColor: file ? 'primary.main' : 'grey.400',
            borderRadius: 2,
            p: 4,
            textAlign: 'center',
            backgroundColor: file ? 'primary.50' : 'grey.50',
            mb: 2,
            cursor: 'pointer',
          }}
          component="label"
        >
          <input type="file" hidden accept=".pdf,.docx,.doc" onChange={handleFileChange} />
          <CloudUploadIcon sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
          <Typography variant="body1" sx={{ mb: 0.5 }}>
            {file ? file.name : 'Drag & drop or click to browse'}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Supported formats: PDF, DOCX
          </Typography>
        </Box>

        {uploading && <LinearProgress sx={{ mb: 2 }} />}

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            <AlertTitle>Error</AlertTitle>
            {error}
          </Alert>
        )}

        <Button variant="contained" size="large" onClick={handleUpload} disabled={!canUpload || uploading}>
          <AutoAwesomeIcon sx={{ mr: 1 }} />
          Upload & Auto-Generate
        </Button>
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
          This will upload the syllabus, extract topics, auto-generate questions, and create an exam.
        </Typography>
      </Paper>

      {result && (
        <>
          <Alert severity="success" sx={{ mb: 2 }}>{result.message}</Alert>

          {result.createdExam && (
            <Card sx={{ mb: 2, border: '2px solid', borderColor: 'success.main' }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                  <CheckCircleIcon color="success" sx={{ mr: 1, fontSize: 32 }} />
                  <Typography variant="h6">Exam Auto-Created</Typography>
                </Box>
                <Typography variant="body1" sx={{ mb: 1 }}><strong>{result.createdExam.examTitle}</strong></Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  {result.createdExam.totalQuestions} questions | {result.createdExam.totalMarks} marks | Status: {result.createdExam.status}
                </Typography>
                <Button variant="outlined" component={RouterLink} to={`/admin/exams/${result.createdExam.examId}`}>
                  View Exam
                </Button>
              </CardContent>
            </Card>
          )}

          {result.generatedQuestions && result.generatedQuestions.length > 0 && (
            <Paper sx={{ p: 3, mb: 2 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <QuizIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Generated Questions ({result.generatedQuestions.length})</Typography>
              </Box>
              {result.generatedQuestions.slice(0, 10).map((q, i) => (
                <Paper key={i} variant="outlined" sx={{ p: 1.5, mb: 1 }}>
                  <Typography variant="body2" sx={{ fontWeight: 500, mb: 0.5 }}>{q.questionText}</Typography>
                  <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                    <Chip label={q.questionType} size="small" color="primary" variant="outlined" />
                    <Chip label={q.difficulty} size="small" color={q.difficulty === 'EASY' ? 'success' : q.difficulty === 'MEDIUM' ? 'warning' : 'error'} variant="outlined" />
                    <Chip label={q.bloomLevel} size="small" variant="outlined" />
                    <Chip label={q.topicName} size="small" variant="outlined" sx={{ borderStyle: 'dashed' }} />
                  </Box>
                </Paper>
              ))}
              {result.generatedQuestions.length > 10 && (
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  ...and {result.generatedQuestions.length - 10} more questions
                </Typography>
              )}
            </Paper>
          )}

          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>Extracted Units & Topics</Typography>
            {result.extractedTopics.map((topic, i) => (
              <Paper key={i} variant="outlined" sx={{ p: 2, mb: 1.5 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>
                  Unit {topic.unitNumber}: {topic.unitName}
                </Typography>
                <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                  {topic.topics.map((t, j) => (
                    <Chip key={j} label={t} size="small" variant="outlined" />
                  ))}
                </Box>
              </Paper>
            ))}
          </Paper>
        </>
      )}
    </Box>
  );
};

export default SyllabusUploadPage;
