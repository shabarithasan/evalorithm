import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Chip,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Button,
  IconButton,
  Tooltip,
  MenuItem,
  TextField,
  Divider,
  Alert,
  Paper,
  LinearProgress,
} from '@mui/material';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import TopicIcon from '@mui/icons-material/Topic';
import AddIcon from '@mui/icons-material/Add';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import SchoolIcon from '@mui/icons-material/School';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import QuizIcon from '@mui/icons-material/Quiz';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import EmptyState from '../../components/common/EmptyState';
import FormDialog from '../../components/common/FormDialog';
import {
  subjectService,
  unitService,
  topicService,
  departmentService,
  semesterService,
  syllabusUploadService,
} from '../../services';
import {
  Subject,
  Unit,
  Topic,
  Department,
  Semester,
  SubjectRequest,
  UnitRequest,
  TopicRequest,
  Status,
  SyllabusUploadResult,
} from '../../types';

const FacultySubjectsPage: React.FC = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [subjectDialogOpen, setSubjectDialogOpen] = useState(false);
  const [subjectForm, setSubjectForm] = useState<SubjectRequest>({
    code: '',
    name: '',
    departmentId: 0,
    semesterId: 0,
    credits: 3,
    description: '',
    status: 'ACTIVE',
  });
  const [subjectSubmitting, setSubjectSubmitting] = useState(false);
  const [subjectError, setSubjectError] = useState('');

  const [unitsBySubject, setUnitsBySubject] = useState<Record<number, Unit[]>>({});
  const [topicsByUnit, setTopicsByUnit] = useState<Record<number, Topic[]>>({});
  const [syllabusLoading, setSyllabusLoading] = useState<Record<number, boolean>>({});
  const [expandedSubjects, setExpandedSubjects] = useState<Record<number, boolean>>({});
  const [expandedUnits, setExpandedUnits] = useState<Record<number, boolean>>({});

  const [unitDialogOpen, setUnitDialogOpen] = useState(false);
  const [unitSubjectId, setUnitSubjectId] = useState<number | null>(null);
  const [unitForm, setUnitForm] = useState<UnitRequest>({ number: 1, name: '', subjectId: 0, description: '' });
  const [unitSubmitting, setUnitSubmitting] = useState(false);
  const [unitError, setUnitError] = useState('');

  const [topicDialogOpen, setTopicDialogOpen] = useState(false);
  const [topicUnitId, setTopicUnitId] = useState<number | null>(null);
  const [topicForm, setTopicForm] = useState<TopicRequest>({ name: '', unitId: 0, description: '', keywords: '' });
  const [topicSubmitting, setTopicSubmitting] = useState(false);
  const [topicError, setTopicError] = useState('');

  const [importDialogOpen, setImportDialogOpen] = useState(false);
  const [importSubject, setImportSubject] = useState<Subject | null>(null);
  const [importFile, setImportFile] = useState<File | null>(null);
  const [importing, setImporting] = useState(false);
  const [importError, setImportError] = useState('');
  const [importResult, setImportResult] = useState<SyllabusUploadResult | null>(null);

  const fetchSubjects = useCallback(async () => {
    setLoading(true);
    try {
      const response = await subjectService.getAll(0, 100);
      if (response.success) {
        const subjectsList = response.data.content;
        setSubjects(subjectsList);
        // Pre-load units for all subjects to show the correct Unit count
        subjectsList.forEach((sub: Subject) => {
          unitService.getBySubject(sub.id).then(res => {
            if (res.success) {
              setUnitsBySubject(prev => ({ ...prev, [sub.id]: res.data }));
            }
          }).catch(() => {});
        });
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load subjects');
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchDropdowns = useCallback(async (): Promise<{ departments: Department[]; semesters: Semester[] }> => {
    let depts: Department[] = [];
    let sems: Semester[] = [];
    try {
      const [deptRes, semRes] = await Promise.all([
        departmentService.getAll(0, 100),
        semesterService.getAll(0, 100),
      ]);
      if (deptRes.success) depts = deptRes.data.content;
      if (semRes.success) sems = semRes.data.content;
    } catch {
      // Dropdowns stay empty; the dialog shows a retry option
    }
    setDepartments(depts);
    setSemesters(sems);
    return { departments: depts, semesters: sems };
  }, []);

  useEffect(() => {
    fetchSubjects();
    fetchDropdowns();
  }, [fetchSubjects, fetchDropdowns]);

  const loadTopicsForUnit = useCallback(async (unitId: number) => {
    try {
      const response = await topicService.getByUnit(unitId);
      if (response.success) {
        setTopicsByUnit((prev) => ({ ...prev, [unitId]: response.data }));
      }
    } catch {
      setTopicsByUnit((prev) => ({ ...prev, [unitId]: [] }));
    }
  }, []);

  const loadSyllabus = useCallback(
    async (subjectId: number) => {
      setSyllabusLoading((prev) => ({ ...prev, [subjectId]: true }));
      try {
        const response = await unitService.getBySubject(subjectId);
        const units = response.success ? response.data : [];
        setUnitsBySubject((prev) => ({ ...prev, [subjectId]: units }));
      } catch {
        setUnitsBySubject((prev) => ({ ...prev, [subjectId]: [] }));
      } finally {
        setSyllabusLoading((prev) => ({ ...prev, [subjectId]: false }));
      }
    },
    []
  );

  const handleSubjectExpand = (subjectId: number, expanded: boolean) => {
    setExpandedSubjects((prev) => ({ ...prev, [subjectId]: expanded }));
    if (expanded && unitsBySubject[subjectId] === undefined) {
      loadSyllabus(subjectId);
    }
  };

  const handleUnitExpand = (unitId: number, expanded: boolean) => {
    setExpandedUnits((prev) => ({ ...prev, [unitId]: expanded }));
    if (expanded && topicsByUnit[unitId] === undefined) {
      loadTopicsForUnit(unitId);
    }
  };

  const handleOpenSubjectDialog = async () => {
    setSubjectError('');
    let depts = departments;
    let sems = semesters;
    if (depts.length === 0 || sems.length === 0) {
      const fresh = await fetchDropdowns();
      depts = fresh.departments;
      sems = fresh.semesters;
    }
    setSubjectForm({
      code: '',
      name: '',
      departmentId: depts[0]?.id || 0,
      semesterId: sems[0]?.id || 0,
      credits: 3,
      description: '',
      status: 'ACTIVE',
    });
    setSubjectDialogOpen(true);
  };

  const handleDepartmentChange = (departmentId: number) => {
    const firstSem = semesters.find((sem) => sem.departmentId === departmentId);
    setSubjectForm((prev) => ({
      ...prev,
      departmentId,
      semesterId: firstSem?.id || prev.semesterId,
    }));
  };

  const handleSubmitSubject = async () => {
    setSubjectSubmitting(true);
    setSubjectError('');
    try {
      const response = await subjectService.create(subjectForm);
      if (response.success) {
        setSubjectDialogOpen(false);
        fetchSubjects();
      } else {
        setSubjectError(response.message || 'Failed to create subject');
      }
    } catch (err: any) {
      setSubjectError(err.response?.data?.message || 'Failed to create subject');
    } finally {
      setSubjectSubmitting(false);
    }
  };

  const handleOpenUnitDialog = (subject: Subject) => {
    setUnitError('');
    const existing = unitsBySubject[subject.id] || [];
    const nextNumber = existing.length > 0
      ? Math.max(...existing.map((u) => u.number)) + 1
      : 1;
    setUnitSubjectId(subject.id);
    setUnitForm({ number: nextNumber, name: '', subjectId: subject.id, description: '' });
    setUnitDialogOpen(true);
  };

  const handleSubmitUnit = async () => {
    if (!unitSubjectId) return;
    setUnitSubmitting(true);
    setUnitError('');
    try {
      const response = await unitService.create(unitForm);
      if (response.success) {
        setUnitDialogOpen(false);
        setUnitsBySubject((prev) => ({
          ...prev,
          [unitSubjectId]: [...(prev[unitSubjectId] || []), response.data],
        }));
      } else {
        setUnitError(response.message || 'Failed to create unit');
      }
    } catch (err: any) {
      setUnitError(err.response?.data?.message || 'Failed to create unit');
    } finally {
      setUnitSubmitting(false);
    }
  };

  const handleOpenTopicDialog = (unit: Unit) => {
    setTopicError('');
    setTopicUnitId(unit.id);
    setTopicForm({ name: '', unitId: unit.id, description: '', keywords: '' });
    setTopicDialogOpen(true);
  };

  const handleSubmitTopic = async () => {
    if (!topicUnitId) return;
    setTopicSubmitting(true);
    setTopicError('');
    try {
      const response = await topicService.create(topicForm);
      if (response.success) {
        setTopicDialogOpen(false);
        setTopicsByUnit((prev) => ({
          ...prev,
          [topicUnitId]: [...(prev[topicUnitId] || []), response.data],
        }));
      } else {
        setTopicError(response.message || 'Failed to create topic');
      }
    } catch (err: any) {
      setTopicError(err.response?.data?.message || 'Failed to create topic');
    } finally {
      setTopicSubmitting(false);
    }
  };

  const handleOpenImportDialog = (subject: Subject) => {
    setImportSubject(subject);
    setImportFile(null);
    setImportError('');
    setImportResult(null);
    setImportDialogOpen(true);
  };

  const handleImportSubmit = async () => {
    if (importResult) {
      setImportDialogOpen(false);
      return;
    }
    if (!importSubject || !importFile) return;
    setImporting(true);
    setImportError('');
    setImportResult(null);
    try {
      const response = await syllabusUploadService.uploadSyllabus(
        importFile,
        importSubject.departmentId,
        importSubject.semesterId,
        importSubject.id
      );
      if (response.success) {
        setImportResult(response.data);
        loadSyllabus(importSubject.id);
      } else {
        setImportError(response.message || 'Import failed');
      }
    } catch (err: any) {
      setImportError(err.response?.data?.message || 'Import failed');
    } finally {
      setImporting(false);
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box sx={{ px: { xs: 2, sm: 3, md: 5 }, py: 3, maxWidth: '1400px', mx: 'auto' }}>
      <PageHeader
        title="My Subjects"
        subtitle="Add subjects and manage their syllabus (units & topics)"
        actionLabel="Add Subject"
        onAction={handleOpenSubjectDialog}
      />

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {subjects.length === 0 ? (
        <EmptyState
          title="No subjects yet"
          message="Click 'Add Subject' to create your first subject (e.g. DBMS, OS, IOT, VE, NM, ASP.NET)."
        />
      ) : (
        <Grid container spacing={3}>
          {subjects.map((subject) => (
            <Grid item xs={12} sm={12} md={6} lg={4} key={subject.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', boxShadow: { xs: 1, md: 4 }, borderRadius: 3 }}>
                <CardContent sx={{ flex: 1 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                    <Box
                      sx={{
                        p: 1,
                        borderRadius: 1.5,
                        bgcolor: 'primary.50',
                        display: 'flex',
                        alignItems: 'center',
                      }}
                    >
                      <MenuBookIcon sx={{ color: 'primary.main' }} />
                    </Box>
                    <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                      {subject.code}
                    </Typography>
                  </Box>
                  <Typography variant="h6" sx={{ mb: 1, fontSize: '1rem' }}>
                    {subject.name}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    {subject.description || 'No description available'}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 2 }}>
                    <Chip
                      icon={<ViewModuleIcon />}
                      label={`${subject.credits} Credits`}
                      size="small"
                      variant="outlined"
                    />
                    <Chip
                      label={subject.departmentName}
                      size="small"
                      variant="outlined"
                      color="primary"
                    />
                    <Chip label={`Sem ${subject.semesterNumber}`} size="small" variant="outlined" />
                    <Chip
                      icon={<ViewModuleIcon />}
                      label={`${(unitsBySubject[subject.id] || []).length} Units`}
                      size="small"
                      variant="outlined"
                      color="secondary"
                    />
                  </Box>
                  <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                    <Button
                      variant="outlined"
                      size="small"
                      startIcon={<AddIcon />}
                      onClick={() => handleOpenUnitDialog(subject)}
                    >
                      Add Unit
                    </Button>
                    <Button
                      variant="contained"
                      size="small"
                      color="success"
                      startIcon={<CloudUploadIcon />}
                      onClick={() => handleOpenImportDialog(subject)}
                    >
                      Import Syllabus
                    </Button>
                  </Box>
                </CardContent>

                <Divider />

                <Accordion
                  expanded={!!expandedSubjects[subject.id]}
                  onChange={(_e, expanded) => handleSubjectExpand(subject.id, expanded)}
                  sx={{ boxShadow: 'none' }}
                >
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <SchoolIcon fontSize="small" color="primary" />
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        Syllabus
                      </Typography>
                    </Box>
                  </AccordionSummary>
                  <AccordionDetails sx={{ pt: 0 }}>
                    {syllabusLoading[subject.id] ? (
                      <Typography variant="body2" color="text.secondary">
                        Loading syllabus...
                      </Typography>
                    ) : (unitsBySubject[subject.id] || []).length === 0 ? (
                      <Box sx={{ py: 1 }}>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                          No units yet. Add up to 5 units for this subject.
                        </Typography>
                        <Button
                          variant="text"
                          size="small"
                          startIcon={<AddIcon />}
                          onClick={() => handleOpenUnitDialog(subject)}
                        >
                          Add Unit 1
                        </Button>
                      </Box>
                    ) : (
                      (unitsBySubject[subject.id] || []).map((unit) => (
                        <Accordion
                          key={unit.id}
                          expanded={!!expandedUnits[unit.id]}
                          onChange={(_e, expanded) => handleUnitExpand(unit.id, expanded)}
                          sx={{ boxShadow: 'none', '&:before': { display: 'none' } }}
                        >
                          <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ minHeight: 40 }}>
                            <Typography variant="body2" sx={{ fontWeight: 600 }}>
                              Unit {unit.number}: {unit.name}
                            </Typography>
                          </AccordionSummary>
                          <AccordionDetails>
                            {unit.description && (
                              <Typography variant="body2" color="text.secondary" sx={{ mb: 1.5 }}>
                                {unit.description}
                              </Typography>
                            )}
                            {(topicsByUnit[unit.id] || []).length === 0 ? (
                              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                                No topics added yet.
                              </Typography>
                            ) : (
                              <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap', mb: 1 }}>
                                {(topicsByUnit[unit.id] || []).map((topic) => (
                                  <Chip
                                    key={topic.id}
                                    icon={<TopicIcon />}
                                    label={topic.name}
                                    size="small"
                                    variant="outlined"
                                  />
                                ))}
                              </Box>
                            )}
                            <Button
                              variant="text"
                              size="small"
                              startIcon={<AddIcon />}
                              onClick={() => handleOpenTopicDialog(unit)}
                            >
                              Add Topic
                            </Button>
                          </AccordionDetails>
                        </Accordion>
                      ))
                    )}
                  </AccordionDetails>
                </Accordion>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <FormDialog
        open={subjectDialogOpen}
        onClose={() => setSubjectDialogOpen(false)}
        title="Add Subject"
        onSubmit={handleSubmitSubject}
        loading={subjectSubmitting}
        submitLabel="Create Subject"
        maxWidth="md"
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          {subjectError && <Alert severity="error">{subjectError}</Alert>}
          {(departments.length === 0 || semesters.length === 0) && (
            <Alert
              severity="warning"
              action={
                <Button size="small" onClick={() => fetchDropdowns()} color="inherit">
                  Retry
                </Button>
              }
            >
              Department / Semester list could not be loaded. Click Retry.
            </Alert>
          )}
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Code"
              value={subjectForm.code}
              onChange={(e) => setSubjectForm({ ...subjectForm, code: e.target.value })}
              placeholder="e.g. CS501"
              required
              fullWidth
            />
            <TextField
              label="Name"
              value={subjectForm.name}
              onChange={(e) => setSubjectForm({ ...subjectForm, name: e.target.value })}
              placeholder="e.g. DBMS, OS, IOT, VE, NM, ASP.NET"
              required
              fullWidth
            />
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              select
              label="Department"
              value={subjectForm.departmentId || ''}
              onChange={(e) => handleDepartmentChange(Number(e.target.value))}
              required
              fullWidth
            >
              {departments.map((dept) => (
                <MenuItem key={dept.id} value={dept.id}>
                  {dept.name}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Semester"
              value={subjectForm.semesterId}
              onChange={(e) => setSubjectForm({ ...subjectForm, semesterId: Number(e.target.value) })}
              required
              fullWidth
            >
              {semesters.map((sem) => (
                <MenuItem key={sem.id} value={sem.id}>
                  {sem.departmentName} - Semester {sem.number}
                </MenuItem>
              ))}
            </TextField>
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Credits"
              type="number"
              value={subjectForm.credits}
              onChange={(e) => setSubjectForm({ ...subjectForm, credits: Number(e.target.value) })}
              required
              fullWidth
              inputProps={{ min: 1 }}
            />
            <TextField
              select
              label="Status"
              value={subjectForm.status}
              onChange={(e) => setSubjectForm({ ...subjectForm, status: e.target.value as Status })}
              fullWidth
            >
              <MenuItem value="ACTIVE">Active</MenuItem>
              <MenuItem value="INACTIVE">Inactive</MenuItem>
            </TextField>
          </Box>
          <TextField
            label="Description"
            value={subjectForm.description}
            onChange={(e) => setSubjectForm({ ...subjectForm, description: e.target.value })}
            multiline
            rows={3}
            fullWidth
          />
        </Box>
      </FormDialog>

      <FormDialog
        open={unitDialogOpen}
        onClose={() => setUnitDialogOpen(false)}
        title="Add Unit"
        onSubmit={handleSubmitUnit}
        loading={unitSubmitting}
        submitLabel="Create Unit"
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          {unitError && <Alert severity="error">{unitError}</Alert>}
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Unit Number"
              type="number"
              value={unitForm.number}
              onChange={(e) => setUnitForm({ ...unitForm, number: Number(e.target.value) })}
              required
              fullWidth
              inputProps={{ min: 1 }}
            />
            <TextField
              label="Name"
              value={unitForm.name}
              onChange={(e) => setUnitForm({ ...unitForm, name: e.target.value })}
              placeholder="e.g. Database Concepts"
              required
              fullWidth
            />
          </Box>
          <TextField
            label="Description"
            value={unitForm.description}
            onChange={(e) => setUnitForm({ ...unitForm, description: e.target.value })}
            multiline
            rows={3}
            fullWidth
          />
        </Box>
      </FormDialog>

      <FormDialog
        open={topicDialogOpen}
        onClose={() => setTopicDialogOpen(false)}
        title="Add Topic"
        onSubmit={handleSubmitTopic}
        loading={topicSubmitting}
        submitLabel="Create Topic"
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          {topicError && <Alert severity="error">{topicError}</Alert>}
          <TextField
            label="Topic Name"
            value={topicForm.name}
            onChange={(e) => setTopicForm({ ...topicForm, name: e.target.value })}
            placeholder="e.g. ER Diagrams, SQL Queries"
            required
            fullWidth
          />
          <TextField
            label="Keywords"
            value={topicForm.keywords}
            onChange={(e) => setTopicForm({ ...topicForm, keywords: e.target.value })}
            placeholder="comma separated, e.g. schema, normalization"
            fullWidth
          />
          <TextField
            label="Description"
            value={topicForm.description}
            onChange={(e) => setTopicForm({ ...topicForm, description: e.target.value })}
            multiline
            rows={2}
            fullWidth
          />
        </Box>
      </FormDialog>
      <FormDialog
        open={importDialogOpen}
        onClose={() => setImportDialogOpen(false)}
        title={`Import Syllabus - ${importSubject?.name || ''}`}
        onSubmit={handleImportSubmit}
        loading={importing}
        submitLabel={importResult ? "Done" : "Upload & Extract"}
        maxWidth="md"
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          {importError && <Alert severity="error">{importError}</Alert>}
          <Typography variant="body2" color="text.secondary">
            Upload your full syllabus (photo or document). Units and topics will be
            extracted automatically and added to this subject.
          </Typography>
          <Box
            sx={{
              border: '2px dashed',
              borderColor: importFile ? 'success.main' : 'grey.400',
              borderRadius: 2,
              p: 3,
              textAlign: 'center',
              backgroundColor: importFile ? 'success.50' : 'grey.50',
            }}
            component="label"
          >
            <input
              type="file"
              hidden
              accept=".pdf,.docx,.doc,.png,.jpg,.jpeg,.txt"
              onChange={(e) => {
                const selected = e.target.files?.[0];
                if (selected) {
                  setImportFile(selected);
                  setImportError('');
                  setImportResult(null);
                }
              }}
            />
            <CloudUploadIcon sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
            <Typography variant="body1" sx={{ mb: 0.5 }}>
              {importFile ? importFile.name : 'Click to choose a file'}
            </Typography>
            <Typography variant="caption" color="text.secondary">
                Supported: PDF, DOCX, TXT, or image (PNG/JPG). Images are read via OCR.
            </Typography>
          </Box>
          {importing && <LinearProgress />}
          {importResult && (
    <Box sx={{ px: { xs: 2, sm: 3, md: 5 }, py: 3, maxWidth: '1400px', mx: 'auto' }}>
              <Alert severity="success" sx={{ mb: 1.5 }}>
                {importResult.message}
              </Alert>
              {importResult.createdExam && (
                <Typography variant="body2" sx={{ mb: 1, fontWeight: 600 }}>
                  <CheckCircleIcon color="success" sx={{ fontSize: 16, verticalAlign: 'middle', mr: 0.5 }} />
                  Exam auto-created: {importResult.createdExam.examTitle} (
                  {importResult.createdExam.totalQuestions} questions, {importResult.createdExam.totalMarks} marks)
                </Typography>
              )}
              {importResult.savedUnits && importResult.savedUnits.length > 0 ? (
                importResult.savedUnits.map((unit) => (
                  <Paper key={unit.unitId} variant="outlined" sx={{ p: 1.5, mb: 1 }}>
                    <Typography variant="body2" sx={{ fontWeight: 600, mb: 0.5 }}>
                      Unit {unit.unitNumber}: {unit.unitName}
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                      {unit.topicIds.length === 0 && (
                        <Typography variant="body2" color="text.secondary">No topics parsed.</Typography>
                      )}
                    </Box>
                    {(importResult.extractedTopics || []).find((t) => t.unitNumber === unit.unitNumber)?.topics.map((topic, idx) => (
                      <Chip key={idx} label={topic} size="small" variant="outlined" sx={{ mr: 0.5, mb: 0.5 }} />
                    ))}
                  </Paper>
                ))
              ) : (
                (importResult.extractedTopics || []).map((topic, i) => (
                  <Paper key={i} variant="outlined" sx={{ p: 1.5, mb: 1 }}>
                    <Typography variant="body2" sx={{ fontWeight: 600, mb: 0.5 }}>
                      Unit {topic.unitNumber}: {topic.unitName}
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                      {topic.topics.map((t, j) => (
                        <Chip key={j} label={t} size="small" variant="outlined" />
                      ))}
                    </Box>
                  </Paper>
                ))
              )}
              {importResult.generatedQuestions && importResult.generatedQuestions.length > 0 && (
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  <QuizIcon sx={{ fontSize: 16, verticalAlign: 'middle', mr: 0.5 }} />
                  {importResult.generatedQuestions.length} questions generated automatically.
                </Typography>
              )}
            </Box>
          )}
        </Box>
      </FormDialog>
    </Box>
  );
};

export default FacultySubjectsPage;
