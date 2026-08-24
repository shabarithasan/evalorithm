import React, { useState, useEffect } from 'react';
import {
  Box,
  Stepper,
  Step,
  StepLabel,
  TextField,
  MenuItem,
  Button,
  Typography,
  Paper,
  Grid,
  CircularProgress,
  Alert,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import SaveIcon from '@mui/icons-material/Save';
import SendIcon from '@mui/icons-material/Send';
import PreviewIcon from '@mui/icons-material/Preview';
import MCQOptionsEditor from './MCQOptionsEditor';
import MatchFollowingEditor from './MatchFollowingEditor';
import AssertionReasonEditor from './AssertionReasonEditor';
import ProgrammingEditor from './ProgrammingEditor';
import CaseStudyEditor from './CaseStudyEditor';
import QuestionPreview from './QuestionPreview';
import {
  Question,
  QuestionRequest,
  QuestionType,
  QuestionDifficulty,
  BloomLevel,
  MCQOption,
  ProgrammingQuestionData,
  CaseStudyData,
} from '../../types';
import {
  questionService,
  questionCategoryService,
  departmentService,
  semesterService,
  subjectService,
  unitService,
  topicService,
} from '../../services';

interface QuestionFormProps {
  existingQuestion?: Question;
  onSave: (data: QuestionRequest) => Promise<void>;
  onSubmitForReview?: (data: QuestionRequest) => Promise<void>;
}

const steps = ['Basic Info', 'Academic Mapping', 'Outcome Mapping', 'Question Content', 'Preview'];

const defaultMCQOptions: MCQOption[] = [
  { optionLabel: 'A', optionText: '', isCorrect: true, explanation: '' },
  { optionLabel: 'B', optionText: '', isCorrect: false, explanation: '' },
  { optionLabel: 'C', optionText: '', isCorrect: false, explanation: '' },
  { optionLabel: 'D', optionText: '', isCorrect: false, explanation: '' },
];

const defaultProgramming: ProgrammingQuestionData = {
  problemStatement: '',
  inputFormat: '',
  outputFormat: '',
  constraints: '',
  sampleInput: '',
  sampleOutput: '',
  testCases: '[]',
  starterCode: '',
  solutionCode: '',
  programmingLanguage: 'C',
};

const defaultCaseStudy: CaseStudyData = {
  scenario: '',
  subQuestions: '[]',
};

const defaultMatchPairs = [
  { left: '', right: '' },
  { left: '', right: '' },
  { left: '', right: '' },
  { left: '', right: '' },
];

const defaultAssertionReason = {
  assertion: '',
  reason: '',
  correctOption: 'A',
};

const QuestionForm: React.FC<QuestionFormProps> = ({ existingQuestion, onSave, onSubmitForReview }) => {
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState<QuestionRequest>({
    title: '',
    description: '',
    questionType: 'MCQ',
    difficulty: 'MEDIUM',
    bloomLevel: 'K2_UNDERSTAND',
    marks: 1,
    estimatedTime: 5,
    explanation: '',
    reference: '',
    categoryId: 0,
    departmentId: 0,
    semesterId: 0,
    subjectId: 0,
    unitId: 0,
    topicId: 0,
    courseOutcome: '',
    programOutcome: '',
    programSpecificOutcome: '',
    mcqOptions: defaultMCQOptions,
    programmingQuestion: defaultProgramming,
    caseStudy: defaultCaseStudy,
    matchPairs: defaultMatchPairs,
    assertionReason: defaultAssertionReason,
  });

  const [departments, setDepartments] = useState<{ id: number; name: string }[]>([]);
  const [semesters, setSemesters] = useState<{ id: number; number: number }[]>([]);
  const [subjects, setSubjects] = useState<{ id: number; name: string }[]>([]);
  const [units, setUnits] = useState<{ id: number; name: string }[]>([]);
  const [topics, setTopics] = useState<{ id: number; name: string }[]>([]);
  const [categories, setCategories] = useState<{ id: number; categoryName: string }[]>([]);

  useEffect(() => {
    loadDropdowns();
    if (existingQuestion) {
      setFormData({
        title: existingQuestion.title,
        description: existingQuestion.description,
        questionType: existingQuestion.questionType,
        difficulty: existingQuestion.difficulty,
        bloomLevel: existingQuestion.bloomLevel,
        marks: existingQuestion.marks,
        estimatedTime: existingQuestion.estimatedTime,
        explanation: existingQuestion.explanation,
        reference: existingQuestion.reference,
        categoryId: existingQuestion.categoryId,
        departmentId: existingQuestion.departmentId,
        semesterId: existingQuestion.semesterId,
        subjectId: existingQuestion.subjectId,
        unitId: existingQuestion.unitId,
        topicId: existingQuestion.topicId,
        courseOutcome: existingQuestion.courseOutcome,
        programOutcome: existingQuestion.programOutcome,
        programSpecificOutcome: existingQuestion.programSpecificOutcome,
        mcqOptions: existingQuestion.mcqOptions?.length ? existingQuestion.mcqOptions : defaultMCQOptions,
        programmingQuestion: existingQuestion.programmingQuestion || defaultProgramming,
        caseStudy: existingQuestion.caseStudy || defaultCaseStudy,
        matchPairs: (existingQuestion as any).matchPairs || defaultMatchPairs,
        assertionReason: (existingQuestion as any).assertionReason || defaultAssertionReason,
      });
    }
  }, [existingQuestion]);

  const loadDropdowns = async () => {
    try {
      const [deptRes, catRes] = await Promise.all([
        departmentService.getAll(0, 100),
        questionCategoryService.getAllActive(),
      ]);
      if (deptRes.success) setDepartments(deptRes.data.content.map((d) => ({ id: d.id, name: d.name })));
      if (catRes.success) setCategories(catRes.data.map((c) => ({ id: c.id, categoryName: c.categoryName })));
    } catch {
      // Handle error silently
    }
  };

  useEffect(() => {
    if (formData.departmentId) {
      semesterService.getByDepartment(formData.departmentId).then((res) => {
        if (res.success) setSemesters(res.data.map((s) => ({ id: s.id, number: s.number })));
      }).catch(() => {});
    } else {
      setSemesters([]);
    }
    setFormData((prev) => ({ ...prev, semesterId: 0, subjectId: 0, unitId: 0, topicId: 0 }));
  }, [formData.departmentId]);

  useEffect(() => {
    if (formData.semesterId) {
      subjectService.getBySemester(formData.semesterId).then((res) => {
        if (res.success) setSubjects(res.data.map((s) => ({ id: s.id, name: s.name })));
      }).catch(() => {});
    } else {
      setSubjects([]);
    }
    setFormData((prev) => ({ ...prev, subjectId: 0, unitId: 0, topicId: 0 }));
  }, [formData.semesterId]);

  useEffect(() => {
    if (formData.subjectId) {
      unitService.getBySubject(formData.subjectId).then((res) => {
        if (res.success) setUnits(res.data.map((u) => ({ id: u.id, name: u.name })));
      }).catch(() => {});
    } else {
      setUnits([]);
    }
    setFormData((prev) => ({ ...prev, unitId: 0, topicId: 0 }));
  }, [formData.subjectId]);

  useEffect(() => {
    if (formData.unitId) {
      topicService.getByUnit(formData.unitId).then((res) => {
        if (res.success) setTopics(res.data.map((t) => ({ id: t.id, name: t.name })));
      }).catch(() => {});
    } else {
      setTopics([]);
    }
    setFormData((prev) => ({ ...prev, topicId: 0 }));
  }, [formData.unitId]);

  const updateField = (field: keyof QuestionRequest, value: any) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleNext = () => {
    setActiveStep((prev) => Math.min(prev + 1, steps.length - 1));
    setError('');
  };

  const handleBack = () => {
    setActiveStep((prev) => Math.max(prev - 1, 0));
  };

  const handleSave = async (asDraft: boolean = true) => {
    setLoading(true);
    setError('');
    try {
      await onSave(formData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save question');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitForReview = async () => {
    if (!onSubmitForReview) return;
    setLoading(true);
    setError('');
    try {
      await onSubmitForReview(formData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit for review');
    } finally {
      setLoading(false);
    }
  };

  const renderStepContent = () => {
    switch (activeStep) {
      case 0:
        return (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
            <TextField
              label="Question Title"
              value={formData.title}
              onChange={(e) => updateField('title', e.target.value)}
              required
              fullWidth
              size="small"
            />
            <TextField
              label="Description"
              value={formData.description}
              onChange={(e) => updateField('description', e.target.value)}
              multiline
              rows={4}
              fullWidth
              size="small"
            />
            <Grid container spacing={2}>
              <Grid item xs={12} sm={4}>
                <TextField
                  select
                  label="Question Type"
                  value={formData.questionType}
                  onChange={(e) => updateField('questionType', e.target.value as QuestionType)}
                  fullWidth
                  size="small"
                >
                  {(['MCQ', 'TRUE_FALSE', 'MATCH_FOLLOWING', 'FILL_BLANKS', 'ASSERTION_REASON', 'DESCRIPTIVE', 'CASE_STUDY', 'PROGRAMMING'] as QuestionType[]).map((t) => (
                    <MenuItem key={t} value={t}>{t.replace('_', ' ')}</MenuItem>
                  ))}
                </TextField>
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  select
                  label="Difficulty"
                  value={formData.difficulty}
                  onChange={(e) => updateField('difficulty', e.target.value as QuestionDifficulty)}
                  fullWidth
                  size="small"
                >
                  {(['EASY', 'MEDIUM', 'HARD', 'EXPERT'] as QuestionDifficulty[]).map((d) => (
                    <MenuItem key={d} value={d}>{d}</MenuItem>
                  ))}
                </TextField>
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  select
                  label="Bloom Level"
                  value={formData.bloomLevel}
                  onChange={(e) => updateField('bloomLevel', e.target.value as BloomLevel)}
                  fullWidth
                  size="small"
                >
                  {(['K1_REMEMBER', 'K2_UNDERSTAND', 'K3_APPLY', 'K4_ANALYZE', 'K5_EVALUATE', 'K6_CREATE'] as BloomLevel[]).map((b) => (
                    <MenuItem key={b} value={b}>{b.replace('_', ' ')}</MenuItem>
                  ))}
                </TextField>
              </Grid>
            </Grid>
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <TextField
                  label="Marks"
                  type="number"
                  value={formData.marks}
                  onChange={(e) => updateField('marks', Number(e.target.value))}
                  fullWidth
                  size="small"
                  inputProps={{ min: 1 }}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  label="Estimated Time (minutes)"
                  type="number"
                  value={formData.estimatedTime}
                  onChange={(e) => updateField('estimatedTime', Number(e.target.value))}
                  fullWidth
                  size="small"
                  inputProps={{ min: 1 }}
                />
              </Grid>
            </Grid>
          </Box>
        );

      case 1:
        return (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
            <TextField
              select
              label="Department"
              value={formData.departmentId || ''}
              onChange={(e) => updateField('departmentId', Number(e.target.value))}
              fullWidth
              size="small"
            >
              <MenuItem value="">Select Department</MenuItem>
              {departments.map((d) => (
                <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Semester"
              value={formData.semesterId || ''}
              onChange={(e) => updateField('semesterId', Number(e.target.value))}
              fullWidth
              size="small"
              disabled={!formData.departmentId}
            >
              <MenuItem value="">Select Semester</MenuItem>
              {semesters.map((s) => (
                <MenuItem key={s.id} value={s.id}>Semester {s.number}</MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Subject"
              value={formData.subjectId || ''}
              onChange={(e) => updateField('subjectId', Number(e.target.value))}
              fullWidth
              size="small"
              disabled={!formData.semesterId}
            >
              <MenuItem value="">Select Subject</MenuItem>
              {subjects.map((s) => (
                <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Unit"
              value={formData.unitId || ''}
              onChange={(e) => updateField('unitId', Number(e.target.value))}
              fullWidth
              size="small"
              disabled={!formData.subjectId}
            >
              <MenuItem value="">Select Unit</MenuItem>
              {units.map((u) => (
                <MenuItem key={u.id} value={u.id}>{u.name}</MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Topic"
              value={formData.topicId || ''}
              onChange={(e) => updateField('topicId', Number(e.target.value))}
              fullWidth
              size="small"
              disabled={!formData.unitId}
            >
              <MenuItem value="">Select Topic</MenuItem>
              {topics.map((t) => (
                <MenuItem key={t.id} value={t.id}>{t.name}</MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Category"
              value={formData.categoryId || ''}
              onChange={(e) => updateField('categoryId', Number(e.target.value))}
              fullWidth
              size="small"
            >
              <MenuItem value="">Select Category</MenuItem>
              {categories.map((c) => (
                <MenuItem key={c.id} value={c.id}>{c.categoryName}</MenuItem>
              ))}
            </TextField>
          </Box>
        );

      case 2:
        return (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
            <TextField
              label="Course Outcome"
              value={formData.courseOutcome}
              onChange={(e) => updateField('courseOutcome', e.target.value)}
              multiline
              rows={3}
              fullWidth
              size="small"
              placeholder="Describe the course outcome this question addresses..."
            />
            <TextField
              label="Program Outcome"
              value={formData.programOutcome}
              onChange={(e) => updateField('programOutcome', e.target.value)}
              multiline
              rows={3}
              fullWidth
              size="small"
              placeholder="Describe the program outcome (e.g., PO1, PO2...)..."
            />
            <TextField
              label="Program Specific Outcome"
              value={formData.programSpecificOutcome}
              onChange={(e) => updateField('programSpecificOutcome', e.target.value)}
              multiline
              rows={3}
              fullWidth
              size="small"
              placeholder="Describe the program specific outcome (e.g., PSO1, PSO2...)..."
            />
          </Box>
        );

      case 3:
        return (
          <Box>
            {formData.questionType === 'MCQ' && (
              <MCQOptionsEditor
                options={formData.mcqOptions}
                onChange={(opts) => updateField('mcqOptions', opts)}
              />
            )}

            {formData.questionType === 'TRUE_FALSE' && (
              <Box>
                <Typography variant="subtitle2" sx={{ mb: 1.5 }}>Correct Answer</Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                  <Button
                    variant={formData.mcqOptions[0]?.isCorrect ? 'contained' : 'outlined'}
                    onClick={() => updateField('mcqOptions', [
                      { optionLabel: 'A', optionText: 'True', isCorrect: true, explanation: '' },
                      { optionLabel: 'B', optionText: 'False', isCorrect: false, explanation: '' },
                    ])}
                  >
                    True
                  </Button>
                  <Button
                    variant={formData.mcqOptions[1]?.isCorrect ? 'contained' : 'outlined'}
                    onClick={() => updateField('mcqOptions', [
                      { optionLabel: 'A', optionText: 'True', isCorrect: false, explanation: '' },
                      { optionLabel: 'B', optionText: 'False', isCorrect: true, explanation: '' },
                    ])}
                  >
                    False
                  </Button>
                </Box>
              </Box>
            )}

            {formData.questionType === 'PROGRAMMING' && (
              <ProgrammingEditor
                data={formData.programmingQuestion}
                onChange={(data) => updateField('programmingQuestion', data)}
              />
            )}

            {formData.questionType === 'CASE_STUDY' && (
              <CaseStudyEditor
                data={formData.caseStudy}
                onChange={(data) => updateField('caseStudy', data)}
              />
            )}

            {formData.questionType === 'MATCH_FOLLOWING' && (
              <MatchFollowingEditor
                pairs={formData.matchPairs || defaultMatchPairs}
                onChange={(pairs) => updateField('matchPairs', pairs)}
              />
            )}

            {formData.questionType === 'ASSERTION_REASON' && (
              <AssertionReasonEditor
                data={formData.assertionReason || defaultAssertionReason}
                onChange={(data) => updateField('assertionReason', data)}
              />
            )}

            {['FILL_BLANKS', 'DESCRIPTIVE'].includes(formData.questionType) && (
              <Alert severity="info" sx={{ mb: 2 }}>
                For {formData.questionType.replace('_', ' ').toLowerCase()} questions, enter the full question content in the description field above.
              </Alert>
            )}

            <TextField
              label="Explanation / Answer Key"
              value={formData.explanation}
              onChange={(e) => updateField('explanation', e.target.value)}
              multiline
              rows={4}
              fullWidth
              size="small"
              sx={{ mt: 2 }}
              placeholder="Provide a detailed explanation or answer key..."
            />
            <TextField
              label="Reference"
              value={formData.reference}
              onChange={(e) => updateField('reference', e.target.value)}
              fullWidth
              size="small"
              placeholder="Textbook, paper, or URL reference..."
            />
          </Box>
        );

      case 4:
        return (
          <QuestionPreview
            question={{
              ...formData,
              id: 0,
              status: 'DRAFT',
              categoryId: formData.categoryId,
              categoryName: categories.find((c) => c.id === formData.categoryId)?.categoryName || '',
              departmentId: formData.departmentId,
              departmentName: departments.find((d) => d.id === formData.departmentId)?.name || '',
              semesterId: formData.semesterId,
              semesterNumber: semesters.find((s) => s.id === formData.semesterId)?.number || 0,
              subjectId: formData.subjectId,
              subjectName: subjects.find((s) => s.id === formData.subjectId)?.name || '',
              unitId: formData.unitId,
              unitName: units.find((u) => u.id === formData.unitId)?.name || '',
              topicId: formData.topicId,
              topicName: topics.find((t) => t.id === formData.topicId)?.name || '',
              createdByName: '',
              updatedByName: '',
              version: 1,
              isArchived: false,
              statistics: { id: 0, viewCount: 0, usageCount: 0, correctCount: 0, wrongCount: 0, correctPercentage: 0, wrongPercentage: 0, lastUsedAt: '' },
              media: [],
              createdAt: '',
              updatedAt: '',
            } as Question}
          />
        );

      default:
        return null;
    }
  };

  return (
    <Box>
      <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Paper sx={{ p: 3, mb: 3 }}>
        {renderStepContent()}
      </Paper>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Button
          onClick={handleBack}
          disabled={activeStep === 0 || loading}
          startIcon={<ArrowBackIcon />}
          variant="outlined"
        >
          Back
        </Button>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          {activeStep < steps.length - 1 ? (
            <Button
              onClick={handleNext}
              variant="contained"
              endIcon={<ArrowForwardIcon />}
            >
              Next
            </Button>
          ) : (
            <>
              <Button
                onClick={() => handleSave(true)}
                variant="outlined"
                disabled={loading}
                startIcon={loading ? <CircularProgress size={18} /> : <SaveIcon />}
              >
                Save as Draft
              </Button>
              {onSubmitForReview && (
                <Button
                  onClick={handleSubmitForReview}
                  variant="contained"
                  disabled={loading}
                  startIcon={loading ? <CircularProgress size={18} /> : <SendIcon />}
                >
                  Submit for Review
                </Button>
              )}
            </>
          )}
        </Box>
      </Box>
    </Box>
  );
};

export default QuestionForm;
