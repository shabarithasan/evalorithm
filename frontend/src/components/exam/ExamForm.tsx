import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  TextField,
  Typography,
  Button,
  Card,
  CardContent,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Switch,
  FormControlLabel,
  Divider,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Checkbox,
  IconButton,
  Chip,
  Autocomplete,
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { ExamRequest, ExamType, Question } from '../../types';
import { departmentService, semesterService, subjectService, questionService } from '../../services';

interface ExamFormProps {
  initialData?: Partial<ExamRequest>;
  onSubmit: (data: ExamRequest) => void;
  loading?: boolean;
  isEdit?: boolean;
}

const examTypes: { value: ExamType; label: string }[] = [
  { value: 'UNIT_TEST', label: 'Unit Test' },
  { value: 'SUBJECT_TEST', label: 'Subject Test' },
  { value: 'SEMESTER_TEST', label: 'Semester Test' },
  { value: 'INTERNAL_ASSESSMENT', label: 'Internal Assessment' },
  { value: 'MOCK_TEST', label: 'Mock Test' },
  { value: 'PRACTICE_TEST', label: 'Practice Test' },
  { value: 'FINAL_EXAMINATION', label: 'Final Examination' },
];

const ExamForm: React.FC<ExamFormProps> = ({ initialData, onSubmit, loading = false, isEdit = false }) => {
  const [formData, setFormData] = useState<Partial<ExamRequest>>({
    title: '',
    description: '',
    examType: 'UNIT_TEST',
    startDate: '',
    endDate: '',
    durationMinutes: 60,
    totalMarks: 100,
    passingMarks: 40,
    maxAttempts: 1,
    negativeMarksEnabled: false,
    negativeMarksValue: 0,
    randomizeQuestions: false,
    randomizeOptions: false,
    showResultsImmediately: false,
    autoSubmit: true,
    fullscreenRequired: false,
    preventTabSwitch: false,
    departmentId: 0,
    semesterId: 0,
    subjectId: 0,
    examQuestions: [],
    assignStudentIds: [],
    ...initialData,
  });

  const [departments, setDepartments] = useState<{ id: number; name: string }[]>([]);
  const [semesters, setSemesters] = useState<{ id: number; number: number }[]>([]);
  const [subjects, setSubjects] = useState<{ id: number; name: string }[]>([]);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [selectedQuestionIds, setSelectedQuestionIds] = useState<number[]>(initialData?.examQuestions?.map((q) => q.questionId) || []);
  const [questionMarks, setQuestionMarks] = useState<Record<number, number>>({});
  const [questionOrder, setQuestionOrder] = useState<Record<number, number>>({});
  const [step, setStep] = useState(0);

  useEffect(() => {
    loadDepartments();
  }, []);

  useEffect(() => {
    if (formData.departmentId) {
      semesterService.getByDepartment(formData.departmentId).then((res) => {
        if (res.success) setSemesters(res.data.map((s) => ({ id: s.id, number: s.number })));
      }).catch(() => setSemesters([]));
    }
  }, [formData.departmentId]);

  useEffect(() => {
    if (formData.semesterId) {
      subjectService.getBySemester(formData.semesterId).then((res) => {
        if (res.success) setSubjects(res.data.map((s) => ({ id: s.id, name: s.name })));
      }).catch(() => setSubjects([]));
    }
  }, [formData.semesterId]);

  useEffect(() => {
    if (formData.subjectId) {
      loadQuestions();
    }
  }, [formData.subjectId]);

  const loadDepartments = async () => {
    try {
      const res = await departmentService.getAll(0, 100);
      if (res.success) setDepartments(res.data.content.map((d) => ({ id: d.id, name: d.name })));
    } catch {}
  };

  const loadQuestions = async () => {
    try {
      const res = await questionService.getAll({
        page: 0,
        size: 200,
        subjectId: formData.subjectId,
        status: 'APPROVED',
      });
      if (res.success) setQuestions(res.data.content);
    } catch {}
  };

  const handleChange = (field: string, value: any) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleQuestionToggle = (questionId: number) => {
    setSelectedQuestionIds((prev) => {
      const newIds = prev.includes(questionId)
        ? prev.filter((id) => id !== questionId)
        : [...prev, questionId];
      if (!newIds.includes(questionId)) {
        const newMarks = { ...questionMarks };
        delete newMarks[questionId];
        setQuestionMarks(newMarks);
        const newOrder = { ...questionOrder };
        delete newOrder[questionId];
        setQuestionOrder(newOrder);
      }
      return newIds;
    });
  };

  const handleSelectAllQuestions = () => {
    if (selectedQuestionIds.length === questions.length) {
      setSelectedQuestionIds([]);
    } else {
      setSelectedQuestionIds(questions.map((q) => q.id));
    }
  };

  const handleSubmit = () => {
    const examQuestions = selectedQuestionIds.map((qId, index) => ({
      questionId: qId,
      marks: questionMarks[qId] || 1,
      orderNumber: questionOrder[qId] || index + 1,
    }));

    onSubmit({
      ...formData,
      departmentId: formData.departmentId || 0,
      semesterId: formData.semesterId || 0,
      subjectId: formData.subjectId || 0,
      examQuestions,
      assignStudentIds: formData.assignStudentIds || [],
    } as ExamRequest);
  };

  const steps = ['Basic Info', 'Schedule & Scoring', 'Options', 'Questions'];

  return (
    <Box>
      <Box sx={{ display: 'flex', gap: 1, mb: 3 }}>
        {steps.map((label, index) => (
          <Button
            key={label}
            variant={step === index ? 'contained' : 'outlined'}
            onClick={() => setStep(index)}
            sx={{ flex: 1 }}
          >
            {label}
          </Button>
        ))}
      </Box>

      {step === 0 && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Basic Information</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Exam Title"
                  value={formData.title}
                  onChange={(e) => handleChange('title', e.target.value)}
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Description"
                  multiline
                  rows={3}
                  value={formData.description}
                  onChange={(e) => handleChange('description', e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl fullWidth>
                  <InputLabel>Exam Type</InputLabel>
                  <Select
                    value={formData.examType}
                    label="Exam Type"
                    onChange={(e) => handleChange('examType', e.target.value)}
                  >
                    {examTypes.map((type) => (
                      <MenuItem key={type.value} value={type.value}>{type.label}</MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl fullWidth>
                  <InputLabel>Department</InputLabel>
                  <Select
                    value={formData.departmentId || ''}
                    label="Department"
                    onChange={(e) => handleChange('departmentId', Number(e.target.value))}
                  >
                    {departments.map((d) => (
                      <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl fullWidth>
                  <InputLabel>Semester</InputLabel>
                  <Select
                    value={formData.semesterId || ''}
                    label="Semester"
                    onChange={(e) => handleChange('semesterId', Number(e.target.value))}
                  >
                    {semesters.map((s) => (
                      <MenuItem key={s.id} value={s.id}>Semester {s.number}</MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl fullWidth>
                  <InputLabel>Subject</InputLabel>
                  <Select
                    value={formData.subjectId || ''}
                    label="Subject"
                    onChange={(e) => handleChange('subjectId', Number(e.target.value))}
                  >
                    {subjects.map((s) => (
                      <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      )}

      {step === 1 && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Schedule & Scoring</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Start Date & Time"
                  type="datetime-local"
                  value={formData.startDate ? formData.startDate.substring(0, 16) : ''}
                  onChange={(e) => handleChange('startDate', e.target.value)}
                  InputLabelProps={{ shrink: true }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="End Date & Time"
                  type="datetime-local"
                  value={formData.endDate ? formData.endDate.substring(0, 16) : ''}
                  onChange={(e) => handleChange('endDate', e.target.value)}
                  InputLabelProps={{ shrink: true }}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Duration (minutes)"
                  type="number"
                  value={formData.durationMinutes}
                  onChange={(e) => handleChange('durationMinutes', Number(e.target.value))}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Total Marks"
                  type="number"
                  value={formData.totalMarks}
                  onChange={(e) => handleChange('totalMarks', Number(e.target.value))}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Passing Marks"
                  type="number"
                  value={formData.passingMarks}
                  onChange={(e) => handleChange('passingMarks', Number(e.target.value))}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Max Attempts"
                  type="number"
                  value={formData.maxAttempts}
                  onChange={(e) => handleChange('maxAttempts', Number(e.target.value))}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.negativeMarksEnabled || false}
                      onChange={(e) => handleChange('negativeMarksEnabled', e.target.checked)}
                    />
                  }
                  label="Negative Marking"
                />
              </Grid>
              {formData.negativeMarksEnabled && (
                <Grid item xs={12} sm={4}>
                  <TextField
                    fullWidth
                    label="Negative Marks Value"
                    type="number"
                    value={formData.negativeMarksValue}
                    onChange={(e) => handleChange('negativeMarksValue', Number(e.target.value))}
                  />
                </Grid>
              )}
            </Grid>
          </CardContent>
        </Card>
      )}

      {step === 2 && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ mb: 2 }}>Exam Options</Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.randomizeQuestions || false}
                      onChange={(e) => handleChange('randomizeQuestions', e.target.checked)}
                    />
                  }
                  label="Randomize Questions"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.randomizeOptions || false}
                      onChange={(e) => handleChange('randomizeOptions', e.target.checked)}
                    />
                  }
                  label="Randomize Options"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.showResultsImmediately || false}
                      onChange={(e) => handleChange('showResultsImmediately', e.target.checked)}
                    />
                  }
                  label="Show Results Immediately"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.autoSubmit || false}
                      onChange={(e) => handleChange('autoSubmit', e.target.checked)}
                    />
                  }
                  label="Auto Submit on Time Expiry"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.fullscreenRequired || false}
                      onChange={(e) => handleChange('fullscreenRequired', e.target.checked)}
                    />
                  }
                  label="Require Fullscreen"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.preventTabSwitch || false}
                      onChange={(e) => handleChange('preventTabSwitch', e.target.checked)}
                    />
                  }
                  label="Prevent Tab Switching"
                />
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      )}

      {step === 3 && (
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6">Select Questions</Typography>
              <Typography variant="body2" color="text.secondary">
                {selectedQuestionIds.length} selected
              </Typography>
            </Box>
            {formData.subjectId ? (
              <TableContainer component={Paper} variant="outlined">
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell padding="checkbox">
                        <Checkbox
                          checked={questions.length > 0 && selectedQuestionIds.length === questions.length}
                          indeterminate={selectedQuestionIds.length > 0 && selectedQuestionIds.length < questions.length}
                          onChange={handleSelectAllQuestions}
                        />
                      </TableCell>
                      <TableCell>Title</TableCell>
                      <TableCell>Type</TableCell>
                      <TableCell>Difficulty</TableCell>
                      <TableCell>Marks</TableCell>
                      <TableCell>Order</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {questions.map((q) => (
                      <TableRow key={q.id} hover selected={selectedQuestionIds.includes(q.id)}>
                        <TableCell padding="checkbox">
                          <Checkbox
                            checked={selectedQuestionIds.includes(q.id)}
                            onChange={() => handleQuestionToggle(q.id)}
                          />
                        </TableCell>
                        <TableCell>{q.title}</TableCell>
                        <TableCell>
                          <Chip label={q.questionType} size="small" variant="outlined" />
                        </TableCell>
                        <TableCell>{q.difficulty}</TableCell>
                        <TableCell>
                          <TextField
                            size="small"
                            type="number"
                            value={questionMarks[q.id] || ''}
                            onChange={(e) => setQuestionMarks((prev) => ({ ...prev, [q.id]: Number(e.target.value) }))}
                            placeholder="1"
                            sx={{ width: 60 }}
                            disabled={!selectedQuestionIds.includes(q.id)}
                          />
                        </TableCell>
                        <TableCell>
                          <TextField
                            size="small"
                            type="number"
                            value={questionOrder[q.id] || ''}
                            onChange={(e) => setQuestionOrder((prev) => ({ ...prev, [q.id]: Number(e.target.value) }))}
                            placeholder={`${questions.indexOf(q) + 1}`}
                            sx={{ width: 60 }}
                            disabled={!selectedQuestionIds.includes(q.id)}
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            ) : (
              <Typography color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
                Please select a subject in Basic Info to load questions
              </Typography>
            )}
          </CardContent>
        </Card>
      )}

      <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
        {step > 0 && (
          <Button variant="outlined" onClick={() => setStep(step - 1)}>
            Previous
          </Button>
        )}
        {step < steps.length - 1 ? (
          <Button variant="contained" onClick={() => setStep(step + 1)}>
            Next
          </Button>
        ) : (
          <Button variant="contained" onClick={handleSubmit} disabled={loading}>
            {loading ? 'Saving...' : isEdit ? 'Update Exam' : 'Create Exam'}
          </Button>
        )}
      </Box>
    </Box>
  );
};

export default ExamForm;
